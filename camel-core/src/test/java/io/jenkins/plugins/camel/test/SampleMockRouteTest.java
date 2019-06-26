package io.jenkins.plugins.camel.test;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.Exchange;
import org.apache.camel.RoutesBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;
import java.io.File;

public class SampleMockRouteTest extends CamelTestSupport {

    static public class SampleMockRoute extends RouteBuilder {
        public void configure() throws Exception {
            from("direct:sampleInput")
                    .log("Received Message is ${body} and Headers are ${headers}")
                    .to("mock:output");
        }
    }

    @Override
    public RoutesBuilder createRouteBuilder() throws Exception {
        return new SampleMockRoute();
    }
    @Test
    public void sampleMockTest() throws InterruptedException {
        String expected="Hello";
        /**
         * Producer Template.
         */
        MockEndpoint mock = getMockEndpoint("mock:output");
        mock.expectedBodiesReceived(expected);
        String input="Hello";
        template.sendBody("direct:sampleInput",input );
        assertMockEndpointsSatisfied();
    }
}