//package io.jenkins.plugins.camel;
//
//import hudson.EnvVars;
//import hudson.Extension;
//import hudson.model.*;
//import hudson.triggers.Trigger;
//import hudson.triggers.TriggerDescriptor;
//import hudson.util.FormValidation;
//import io.vavr.collection.HashMap;
//import io.vavr.collection.List;
//import io.vavr.control.Option;
//import jenkins.model.ParameterizedJobMixIn;
//import lombok.*;
//import lombok.extern.java.Log;
//import org.apache.camel.*;
//import org.apache.camel.builder.RouteBuilder;
//import org.apache.camel.model.RouteDefinition;
//import org.apache.camel.model.RoutesDefinition;
//import org.jenkinsci.Symbol;
//import org.jenkinsci.plugins.envinjectapi.util.EnvVarsResolver;
//import org.kohsuke.stapler.DataBoundConstructor;
//import org.kohsuke.stapler.DataBoundSetter;
//import org.kohsuke.stapler.QueryParameter;
//
//import javax.annotation.CheckForNull;
//import java.io.ObjectStreamException;
//import java.net.URI;
//import java.net.URISyntaxException;
//import java.util.logging.Level;
//
//@Log
//@ToString
//@EqualsAndHashCode(callSuper=true)
////@NoArgsConstructor(onConstructor=@__({@DataBoundConstructor}))
//public class CamelTrigger0 extends Trigger<Job<?, ?>> {
//
//    @Log
//    @RequiredArgsConstructor
//    static public class MessageHandler {
//
//
//        final private CamelTrigger0 camelTrigger;
//
//        //        //@Consume(uri = "activemq:my.queue")
//        //        @Handler
//        //        public void onMessage(@Headers MappingChange.Map<String,Object> headers, @Body String body) {
//        //            log.fine(String.format("message arrived: [%s] from trigger [%sfinal]", body, camelTrigger));
//        //            // TODO process the inbound message here
//        //        }
//        @Handler
//        public void onBody(@Body String body) {
//            log.fine(String.format("message arrived: [%s] from trigger [%s]", body, camelTrigger));
//            val build = camelTrigger.scheduleBuild2(HashMap
//                    .of(
//                            "CAMEL_MESSAGE_BODY", body
//                    ).toJavaMap());
//            log.fine(String.format("build scheduled: [%s]", build));
//        }
//    }
//
//    static public class CamelTriggerCause extends Cause {
//
//        public static final String NAME = "CamelTrigger";
//        public static final String CAUSE = "A message from Camel route";
//
//        @Override
//        public String getShortDescription() {
//            return String.format("[%s] - %s", NAME, CAUSE);
//        }
//    }
//
//
//
//
////    /**
////     * Route source
////     */
////    @Getter
////    @Setter(onMethod=@__({@DataBoundSetter}))
////    @Deprecated
////    String source; // TODO change to List<String> sources
////    public FormValidation doCheckSource(@QueryParameter String value) {
////        if (StringUtils.isEmpty(value)) {
////            return FormValidation.warning("Please specify a source.");
////        }
////        try {
////            new URI(getSource());
////        } catch (URISyntaxException e) {
////            return FormValidation.warning("Invalid source syntax.");
////        }
////        // TODO add source checks: valid scheme, authority...
////        return FormValidation.ok();
////    }
//
////    @Getter
////    @Setter(onMethod=@__({@DataBoundSetter}))
////    java.util.List<String> from; // TODO change to List<String> sources
////    public FormValidation doCheckFrom(@QueryParameter java.util.List<String> values) {
////        if (values.size()>0) {
////            return FormValidation.warning("Please specify a least one originator.");
////        }
////        for(val v:values) {
////            try {
////                new URI(v);
////            } catch (URISyntaxException e) {
////                return FormValidation.warning(String.format("Invalid originator syntax for '%s'", v));
////            }
////        }
////        // TODO add source checks: e.g. valid scheme, authority, path, parameters...
////        return FormValidation.ok();
////    }
//
//    /**
//     * Route origins
//     */
//    @Getter
//    @Setter(onMethod=@__({@DataBoundSetter}))
//    java.util.List<EndpointUri> from; // TODO change to List<String> sources
//    public FormValidation doCheckFrom(@QueryParameter java.util.List<EndpointUri> values) {
//        if (values.size()>0) {
//            return FormValidation.warning("Please specify a least one originator.");
//        }
//        for(val v:values) {
//            try {
//                new URI(v.getUri());
//                // TODO add uri checks: e.g. valid scheme, authority, path, parameters...
//            } catch (URISyntaxException e) {
//                return FormValidation.warning(String.format("Invalid originator syntax for '%s'", v));
//            }
//        }
//
//        return FormValidation.ok();
//    }
//
//
//    private static Boolean noSquash = true; // TODO convert to a trigger parameter
//
//
//    /** A value of -1 will make sure the quiet period of the job will be used. */
//    private static final int RESPECT_JOBS_QUIET_PERIOD = -1;
//
//
//    transient private CamelContext camelContext;
//    transient private java.util.List<Endpoint> sourceEndpoints = null;// = new java.util.LinkedList<>();
//    //transient private Endpoint sinkEndpoint = ;
//    transient private MessageHandler messageHandler = null;// = new MessageHandler(this);
//    //transient private RoutesDefinition routesDefinition = new RoutesDefinition();
//    //transient private Collection<RouteDefinition> routesDefinitions = Collections.emptyList();
//    transient private java.util.Collection<RouteDefinition> routesDefinitions = new java.util.LinkedList<>();
//
//
//    @DataBoundConstructor
//    public CamelTrigger0() {
//        log.fine("CamelTrigger ctor");
//        //        messageHandler = new MessageHandler(this);
//        //        if(routesDefinitions == null) { routesDefinitions = new java.util.LinkedList<>(); }
//        //        sourceEndpoints = new java.util.LinkedList<>();
//        //messageHandler = null;
//        //routesDefinitions = null;
//        //sourceEndpoints = null;
//        log.fine("CamelTrigger ctor " + this);
//    }
//
//    @Symbol("camelTrigger")
//    public static class GenericDescriptor extends TriggerDescriptor {
//
//        @Override
//        public boolean isApplicable(final Item item) {
//            return Job.class.isAssignableFrom(item.getClass());
//        }
//
//        @Override
//        public String getDisplayName() {
//            return "Camel Trigger";
//        }
//    }
//    @Extension
//    public static final GenericDescriptor DESCRIPTOR = new GenericDescriptor();
//
//
//    @Override
//    public void start(Job<?, ?> project, boolean newInstance) {
//        super.start(project, newInstance);
//        startTriggerContext(project, newInstance);
//        if(newInstance) {
//            log.fine(String.format("CamelTrigger [%s] start new instance for job [%s]", this, project));
//            //log.atInfo().log(String.format("CamelTrigger [%s] start new instance for job [%s]", this, project);
//        } else {
//            log.fine(String.format("CamelTrigger [%s] start old instance for job [%s]", this, project));
//        }
//    }
//
//    @Override
//    public void run() {
//        super.run();
//        log.fine(String.format("run trigger %s for job %s", this, job) );
//    }
//
//    @Override
//    public void stop() {
//        stopTriggerContext();
//        super.stop();
//    }
//
//    @Override
//    public java.util.Collection<? extends Action> getProjectActions() {
//        return super.getProjectActions();
//    }
//
//    @Override
//    public TriggerDescriptor getDescriptor() {
//        return super.getDescriptor();
//    }
//
//    @Override
//    protected Object readResolve() throws ObjectStreamException {
//        return super.readResolve();
//    }
//
//    private void startTriggerContext(Job<?, ?> project, boolean newInstance) {
//        log.fine(String.format("starting trigger %s [%s] for project [%s], job [%s]",
//                newInstance ? "again" : "as a new instance",
//                this, project, job) );
//
//        for(int attempt = 0; attempt < 30; attempt++) {
//            //ctx = CamelConfiguration.get().getCamelContext();
//            log.fine(String.format("get camel context attempt %d", attempt));
//            if(CamelConfiguration.get().getCamelContext() != null) {
//                log.fine(String.format("got camel context after %d attempt", attempt));
//                break;
//            }
//            try {
//                // wait to avoid race condition with context initialization
//                Thread.sleep(1000);
//            }catch (Exception e) {
//                log.log(Level.WARNING, "Exception while sleeping: ", e);
//            }
//        }
//        val ctx = CamelConfiguration.get().getCamelContext();
//        log.fine(String.format("got camel context [%s]", ctx));
//
//        val trigger = this;
//        val rb = new RouteBuilder() {
//            @Override
//            public void configure() throws Exception {
//                val messageHandler = new MessageHandler(trigger);
//                log.debug(String.format("message handler: %s", messageHandler));
//
//                val evs = new EnvVars(EnvVarsResolver.getPollingEnvVars(project,null));
//                if(getFrom() != null) {
//                    from(List.ofAll(getFrom()).map(f->evs.expand(f.getUri())).toJavaArray(String[]::new))
//                        .bean(messageHandler, "onBody")
//                        .log("Message received from Kafka : ${body}")
//                        .log("    with header ${headers}")
//                    ;
//                } else {
//                    log.info("trigger has no origin urls");
//                }
//
//                //                val resolvedSource = new EnvVars(EnvVarsResolver.getPollingEnvVars(project,null))
//                //                        .expand(getSource());
//                //                log.debug(String.format("resolved source: %s", resolvedSource));
//                //                from(resolvedSource)
//                //                        //.log(LoggingLevel.DEBUG,"Received Message is ${body} and Headers are ${headers}")
//                //                        //.bean(messageHandler)
//                //                        .bean(messageHandler, "onBody")
//                //                ;
//            }
//        };
//
//
//        {
//            Thread t = Thread.currentThread();
//            ClassLoader orig = t.getContextClassLoader();
//            t.setContextClassLoader(CamelTrigger0.class.getClassLoader());
//            try {
//                HashMap.ofAll(ctx.getRegistry().findByTypeWithName(Component.class))
//                    .forEach((k,v) -> {
//                        log.fine(String.format("found in context registered with name [%s] component [%s]", k, v));
//                    });
//                rb.addRoutesToCamelContext(ctx);
//                log.fine(String.format("routes [%s] added to context [%s]", rb, ctx));
//            } catch (Exception e) {
//                log.log(Level.WARNING, "Can't add route to Camel context: ", e);
//                return;
//            } finally {
//                t.setContextClassLoader(orig);
//            }
//        }
//        try {
//            Option.of(rb).toTry()
//                    //.map(it -> {log.fine("RouteBuilder for source" + it); return it;})
//                    .map(RouteBuilder::getRouteCollection)
//                    //.map(it -> {log.fine("RoutesDefinition " + it); return it;})
//                    .map(RoutesDefinition::getRoutes)
//                    .map(it -> {
//                        //log.fine("opt route " + Option.of(it));
//                        //log.fine("Routes " + it);
//                        //log.fine("Routes size " + it.size());
//                        //log.fine("routesDefinitions " + routesDefinitions);
//                        if(routesDefinitions == null) { routesDefinitions = new java.util.LinkedList<>();} // FIXME fix ctor
//                        if(it.size()>0) routesDefinitions.addAll(it);
//                        return it;
//                    })
//                    .onFailure(e-> log.log(Level.WARNING, "Can't populate routesDefinitions: ", e))
//            ;
//
//            ctx.addRouteDefinitions(routesDefinitions);
//            log.fine(String.format("routes definitions [%s] added to context [%s]", routesDefinitions, ctx));
//        } catch (Exception e) {
//            log.log(Level.WARNING, "Can't add route definitions: ", e);
//        }
//    }
//
//    private void stopTriggerContext() {
//        log.fine(String.format("stopping trigger %s for job %s", this, job) );
//        val ctx = CamelConfiguration.get().getCamelContext();
//        try {
//            if(ctx!=null && routesDefinitions !=null && routesDefinitions.size()>0) {
//                ctx.removeRouteDefinitions(routesDefinitions);
//                routesDefinitions.clear();
//                log.fine(String.format("stopped trigger %s for job %s", this, job) );
//            }
//        } catch (Exception e) {
//            log.log(Level.WARNING, "Can't remove route definitions: ", e);
//        }
//    }
//
//
//    private @CheckForNull
//    Queue.Item scheduleBuild2(java.util.Map<String, String> messageParameters) {
//        val definedParameters = getDefinedParameters(job);
//        val buildParameters = getUpdatedParameters(messageParameters, definedParameters);
//        val parametersAction = new ParametersAction(buildParameters);
//        //log.fine("messageParams: " + io.vavr.collection.HashMap.ofAll(messageParameters).toString());
//        //log.fine("definedParameters: " + io.vavr.collection.List.ofAll(definedParameters).toString());
//        //log.fine("buildParameters: " + io.vavr.collection.List.ofAll(buildParameters).toString());
//        return ParameterizedJobMixIn.scheduleBuild2(
//                job,
//                RESPECT_JOBS_QUIET_PERIOD,
//                new CauseAction(new CamelBuildCause(messageParameters,null)),
//                parametersAction,
//                new CamelEnvironmentContributingAction(messageParameters, buildParameters),
//                new CamelShouldScheduleQueueAction(true)
//        );
//    }
//
//
//    // parameters processing from jms-messging plugin
//
//    private java.util.List<ParameterValue> getUpdatedParameters(java.util.Map<String, String> messageParams, java.util.List<ParameterValue> definedParams) {
//        // Update any build parameters that may have values from the triggering message.
//        java.util.HashMap<String, ParameterValue> newParams = new java.util.HashMap<String, ParameterValue>();
//        for (ParameterValue def : definedParams) {
//            newParams.put(def.getName(), def);
//        }
//        for (String key : messageParams.keySet()) {
//            if (newParams.containsKey(key)) {
//                StringParameterValue spv = new StringParameterValue(key, messageParams.get(key));
//                newParams.put(key, spv);
//            }
//        }
//        return new java.util.ArrayList<ParameterValue>(newParams.values());
//    }
//
//
//    private java.util.List<ParameterValue> getDefinedParameters(Job<?, ?> project) {
//        java.util.List<ParameterValue> parameters = new java.util.ArrayList<ParameterValue>();
//        ParametersDefinitionProperty properties = ((Job<?, ?>)project).getProperty(ParametersDefinitionProperty.class);
//
//        if (properties != null  && properties.getParameterDefinitions() != null) {
//            for (ParameterDefinition paramDef : properties.getParameterDefinitions()) {
//                ParameterValue param = paramDef.getDefaultParameterValue();
//                if (param != null) {
//                    parameters.add(param);
//                }
//            }
//        }
//        return parameters;
//    }
//
//}
