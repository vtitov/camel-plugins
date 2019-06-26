package io.jenkins.plugins.camel;

import hudson.Extension;
import hudson.Plugin;
import hudson.PluginWrapper;
import hudson.init.InitMilestone;
import hudson.init.Initializer;
import hudson.util.FormValidation;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ScanResult;
import io.vavr.collection.HashMap;
import io.vavr.collection.List;
import io.vavr.control.Option;
import io.vavr.control.Try;
import jenkins.model.GlobalConfiguration;
import jenkins.model.GlobalConfigurationCategory;
import jenkins.model.Jenkins;
import lombok.Getter;
import lombok.extern.java.Log;
import lombok.val;
import net.sf.json.JSONObject;
import org.apache.camel.CamelContext;
import org.apache.camel.Component;
import org.apache.camel.ShutdownableService;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.impl.DefaultComponent;
import org.kohsuke.stapler.DataBoundSetter;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;

import javax.annotation.concurrent.GuardedBy;
import java.io.IOException;
import java.util.Collection;
import java.util.logging.Level;

/**
 * Global configuration of Camel for Jenkins.
 */
@Log
@Extension
public class CamelConfiguration extends GlobalConfiguration {

    /** @return the singleton instance */
    public static CamelConfiguration get() {
        return GlobalConfiguration.all().get(CamelConfiguration.class);
    }

    //    // TODO remove
    //    private String label;

    @Override
    public GlobalConfigurationCategory getCategory() {
        return GlobalConfigurationCategory.get(GlobalConfigurationCategory.Security.class);
    }

    @GuardedBy("this")
    @Getter
    //private transient DefaultCamelContext camelContext = null;
    private transient DefaultCamelContext camelContext = new JenkinsCamelContext();


    public CamelConfiguration() {
        // When Jenkins is restarted, load any saved configuration from disk.
        load();
    }

    //    /** @return the currently configured label, if any */
    //    public String getLabel() {
    //        return label;
    //    }
    //    /**
    //     * Together with {@link #getLabel}, binds to entry in {@code config.jelly}.
    //     * @param label the new value of this field
    //     */
    //    @DataBoundSetter
    //    public void setLabel(String label) {
    //        this.label = label;
    //        save();
    //    }
    //    public FormValidation doCheckLabel(@QueryParameter String value) {
    //        if (StringUtils.isEmpty(value)) {
    //            return FormValidation.warning("Please specify a label.");
    //        }
    //        return FormValidation.ok();
    //    }


    public synchronized void restart() {
        Try.of(() -> { stop(); start(); return camelContext; })
            .onFailure(e->log.log(Level.INFO, "Failed to restart camel", e));
    }

    public synchronized void stop() throws Exception {
        Option.of(camelContext).toTry()
            .andThenTry(ShutdownableService::stop)
            .onFailure(e->log.log(Level.INFO, "Failed to stop camel", e));
        //camelContext = null;
    }

    public synchronized void start() throws IOException, InterruptedException {
        log.fine("Starting Camel");
        //camelContext = new JenkinsCamelContext();
        camelContext.setStreamCaching(true);
        log.fine(String.format("New Camel context: %s", camelContext));
        Try.of(() -> {
            camelContext.start();
            return camelContext;
        })
            .onSuccess(c->log.log(Level.INFO, String.format("Camel started with context: %s", camelContext.toString())))
            .onFailure(e->{log.log(Level.INFO, "Failed to start camel", e);});
    }


    @Override
    public boolean configure(StaplerRequest req, JSONObject json) throws FormException {
        val result = super.configure(req, json);
        // TODO
        return result;
    }

    @Initializer(after= InitMilestone.PLUGINS_PREPARED, before= InitMilestone.PLUGINS_STARTED,fatal=false)
    public static void init() throws IOException, InterruptedException {
        get().start();
    }

    @Initializer(after= InitMilestone.PLUGINS_STARTED, before= InitMilestone.EXTENSIONS_AUGMENTED,fatal=false)
    public static void registerComponentsWithClassLoaders() throws Exception {
        Thread t = Thread.currentThread();
        ClassLoader orig = t.getContextClassLoader();

        try {
            registerComponents(
                get().camelContext,
                List.ofAll(Jenkins.get().pluginManager.getPlugins())
                    .map(pw -> pw.classLoader)
                    .toJavaArray(ClassLoader[]::new)
            );
        } catch (Exception e) {
            log.log(Level.WARNING, "Can't register components: ", e);
        } finally {
            t.setContextClassLoader(orig);
        }
    }


    //private static void registerComponents(CamelContext camelContext) throws Exception {
    //    registerComponents(camelContext, null);
    //}
    private static void registerComponents(CamelContext camelContext, ClassLoader[] classLoaders) throws Exception {
        final String componentsPackageName = "org.apache.camel.component";
        final String componentInterfaceName = "org.apache.camel.Component";
        final List<String> filteredCompinents = List.of(
            "BindingComponent"
            ,"RestApiComponent"
        );

        try (val scanResult = new ClassGraph()
            .enableAllInfo()
            .overrideClassLoaders(classLoaders)
            .whitelistPackages(componentsPackageName)
            .scan()) {

            for (val componentClass : scanResult.getClassesImplementing(componentInterfaceName)) {
                if(componentClass.isAbstract()) {
                    log.info(String.format("skipping abstract class: %s", componentClass.getName()));
                    continue;
                }
                if(filteredCompinents.exists(skipped -> skipped.equals(componentClass.getSimpleName()))) {
                    log.info(String.format("skipping filtered class: %s", componentClass.getName()));
                    continue;
                }
                val parentPackageName = componentClass.getPackageInfo().getParent().getName();
                if(componentsPackageName.equals(parentPackageName)) {
                    val simplePackageName = componentClass.getPackageName()
                        .replace(parentPackageName + ".", "");
                    if(camelContext.hasComponent(simplePackageName) == null) {
                        log.info(String.format("adding component. simple package name = %s, class = %s"
                            ,simplePackageName
                            ,componentClass.getName()
                        ));
                        val comp = (Component)componentClass.loadClass().newInstance();
                        camelContext.addComponent(simplePackageName, comp);
                    } else {
                        log.info(String.format("context already has component. simple package name = %s, class = %s"
                            , simplePackageName
                            , camelContext.hasComponent(simplePackageName)
                        ));
                    }
                }
            }
        }
        val defaultComponents = camelContext.getRegistry().findByTypeWithName(DefaultComponent.class);
        log.fine(String.format("found in context [%s] defaulf components", defaultComponents));
        val components = camelContext.getRegistry().findByTypeWithName(Component.class);
        log.fine(String.format("found in context [%s] components", components));
        HashMap.ofAll(components)
            .forEach((k, v) -> {
                log.info(String.format("found in context registered with name [%s] component [%s]", k, v));
            });
    }

    static private void debug(String s) {log.info(s);}

}
