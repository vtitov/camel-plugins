package io.jenkins.plugins.camel.component;


import io.jenkins.plugins.camel.CamelConfiguration;
import io.vavr.collection.HashMap;
import lombok.extern.java.Log;
import lombok.val;
import org.apache.camel.Component;

import java.lang.reflect.ParameterizedType;
import java.util.logging.Level;


@Log
public abstract class CamelExtensionBase<COMPONENT extends Component> {

    private Component createComponent() throws Exception {
        val compClass = getComponentClass();
        val comp = compClass.newInstance();
        comp.setCamelContext(CamelConfiguration.get().getCamelContext());
        return comp;
    }

    static private String getComponentScheme(Component comp) {
        return comp.getClass().getSimpleName().replaceAll("Component", "").toLowerCase();
    }

    Class<? extends Component> getComponentClass() {
        return (Class<COMPONENT>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }

    protected static void initContext(CamelExtensionBase<? extends Component> ce) throws Exception {
        try {
            initContext(ce.createComponent());
        } catch (Exception e) {
            log.log(Level.WARNING, String.format("could not initialize camel component for %s", ce.getClass().getCanonicalName()), e);
            throw e;
        }
    }

    private static void initContext(Component comp) {
        String compName = "n/a";
        val ctx = comp.getCamelContext();
        try {
            compName =  comp.getClass().getTypeName().toLowerCase().replace("Component", "");
            if(ctx.hasComponent(getComponentScheme(comp)) == null) {
                ctx.addComponent(getComponentScheme(comp), comp);
            }

        } catch (Exception e) {
            log.log(Level.WARNING, String.format("could not initialize camel component for %s", compName), e);
            throw e;
        }
        log.fine(String.format("added with schame %s camel component %s to context %s", getComponentScheme(comp), comp, ctx));
        dumpComponentRegistrationData(comp);
    }

    private static void dumpComponentRegistrationData(Component comp) {
        {
            val components = comp.getCamelContext().getRegistry().findByTypeWithName(comp.getClass());
            log.fine(String.format("found in context [%s] components", components));
            HashMap.ofAll(components)
                .forEach((k, v) -> {
                    log.fine(String.format("found in context registered with name [%s] component [%s]", k, v));
                });
        }
    }
}
