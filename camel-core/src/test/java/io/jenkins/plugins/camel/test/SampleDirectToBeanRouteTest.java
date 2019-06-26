package io.jenkins.plugins.camel.test;

import io.jenkins.plugins.camel.CamelTrigger;
import lombok.RequiredArgsConstructor;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Ignore;
import org.junit.Test;

import java.util.UUID;


public class SampleDirectToBeanRouteTest extends CamelTestSupport {

    private String resultBody = null;
    private java.util.Map<String,Object> resultHeaders = null;

    @RequiredArgsConstructor
    static public class SampleDirectToBeanRoute extends RouteBuilder{
        final private CamelTrigger.MessageHandler messageHandler;

        public void configure() throws Exception
        {
            from("direct:sampleInput")
                    .log("Received Message is ${body} and Headers are ${headers}")
                    .bean(messageHandler, "onMessage");
        }
    }

    @Override
    public RouteBuilder createRouteBuilder() throws Exception
    {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("direct:sampleInput")
                        .log(LoggingLevel.DEBUG,"Received Message is ${body} and Headers are ${headers}")
                        .bean(
                                new CamelTrigger.MessageHandler(new CamelTrigger()) {
                                    @Override
                                    public void onMessage(java.util.Map<String,Object> headers, String body) {
                                        resultBody = body;
                                        resultHeaders = headers;
                                    }
                                },
                                "onMessage");
            }
        };
    }


    @Ignore // FIXME IllegalAccessException
    public void SampleTestRoute() throws InterruptedException{
        String bodyToSend = UUID.randomUUID().toString();
        template.sendBody("direct:sampleInput",bodyToSend);
        //Thread.sleep(1000);
        assertEquals(bodyToSend, resultBody);
        assertEquals(java.util.Collections.emptyMap(), resultHeaders);
    }
}
