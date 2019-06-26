package io.jenkins.plugins.camel;

import io.vavr.collection.List;
import lombok.extern.java.Log;
import lombok.val;
import org.apache.camel.CamelContext;
import org.apache.camel.Component;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.spi.UriEndpoint;
import org.junit.Ignore;
import org.junit.Test;

//import io.github.classgraph.ScanResult;
import io.github.classgraph.*;


@Log
public class ScanAnotationsTest {


    @Ignore
    public void scanEndpoints() throws Exception {
        final String annotationClassName = "org.apache.camel.spi.UriEndpoint";
        final String componentsPackageName = "org.apache.camel.component";

        try (ScanResult scanResult = new ClassGraph().enableAllInfo().whitelistPackages(componentsPackageName)
            .scan()) {
            ClassInfoList uriEndpointClassInfoList = scanResult.getClassesWithAnnotation(annotationClassName);
            for (ClassInfo uriEndpointClassInfo : uriEndpointClassInfoList) {
                // Get the annotation on the class
                AnnotationInfo annotationInfo = uriEndpointClassInfo.getAnnotationInfo(annotationClassName);
                AnnotationParameterValueList paramVals = annotationInfo.getParameterValues();

                // The annotation has a named parameter
                String scheme = paramVals.get("scheme").toString();

                // Alternatively, you can load and instantiate the annotation, so that the annotation
                // methods can be called directly to get the annotation parameter values (this sets up
                // an InvocationHandler to emulate the annotation instance, since annotations
                // can't be instantiated directly without also loading the annotated class).
                UriEndpoint uriEndpoint = (UriEndpoint) annotationInfo.loadClassAndInstantiate();

                debug
                    (String.format("@UriEndpoint(firstVersion = '%s', scheme = '%s', title = '%s'," +
                        " syntax = '%s', consumerClass = %s, label = '%s')"
                    ,uriEndpoint.firstVersion()
                    ,uriEndpoint.scheme()
                    ,uriEndpoint.title()
                    ,uriEndpoint.syntax()
                    ,uriEndpoint.consumerClass()
                    ,uriEndpoint.label()
                ));
                debug
                    (String.format("class = %s"
                        ,uriEndpointClassInfo.getName()
                        //,uriEndpointClassInfo.getClass().getCanonicalName()
                    ));
            }
        }
    }

    @Test
    public void scanComponents() throws Exception {
        val camelContext = new DefaultCamelContext();
        registerComponents(camelContext);
    }

    private void registerComponents(CamelContext camelContext) throws Exception {
        final String componentsPackageName = "org.apache.camel.component";
        final String componentInterfaceName = "org.apache.camel.Component";
        final List<String> filteredCompinents = List.of(
            "BindingComponent"
            ,"RestApiComponent"
        );

        try (ScanResult scanResult = new ClassGraph().enableAllInfo().whitelistPackages(componentsPackageName)
            .scan()) {

            camelContext.setStreamCaching(true);
            camelContext.start();

            ClassInfoList componentClasses = scanResult.getClassesImplementing(componentInterfaceName);
            val componentClassNames = componentClasses.getNames();

            for (val componentClass : componentClasses) {
                if(componentClass.isAbstract()) {
                    debug(String.format("scipping abstract class: %s", componentClass.getName()));
                    continue;
                }
                if(filteredCompinents.exists(skipped -> skipped.equals(componentClass.getSimpleName()))) {
                    debug(String.format("scipping filtered class: %s", componentClass.getName()));
                    continue;

                }

                val packageName = componentClass.getPackageName();
                val parentPackageName = componentClass.getPackageInfo().getParent().getName();
                if(componentsPackageName.equals(parentPackageName)) {
                    val simplePackageName = packageName.replace(parentPackageName + ".", "");

                    if(camelContext.hasComponent(simplePackageName) == null) {
                        debug
                            (String.format("adding component. simple package name = %s, class = %s"
                                ,simplePackageName
                                ,componentClass.getName()
                            ));
                        val compLadedClass = componentClass.loadClass();

                        val comp = (Component)componentClass.loadClass().newInstance();
                        camelContext.addComponent(simplePackageName, comp);
                    } else {
                        debug
                            (String.format("context already has component. simple package name = %s, class = %s"
                                , simplePackageName
                                , camelContext.hasComponent(simplePackageName)
                            ));
                    }
                }
            }
        }
    }

//    private void debug(String s) {System.err.println(s);}
    private void debug(String s) {log.fine(s);}
}
