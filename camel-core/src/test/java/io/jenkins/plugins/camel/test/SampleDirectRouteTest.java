package io.jenkins.plugins.camel.test;

import org.apache.camel.Exchange;
import org.apache.camel.Route;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;
import java.io.File;


public class SampleDirectRouteTest extends CamelTestSupport {

    static public class SampleDirectRoute extends RouteBuilder{
        public void configure() throws Exception
        {
            from("direct:sampleInput")
                    .log("Received Message is ${body} and Headers are ${headers}")
                    .to("file:sampleOutput?fileName=output.txt");
        }
    }

    @Override
    public RouteBuilder createRouteBuilder() throws Exception
    {
        return new SampleDirectRoute();
    }
    @Test
    public void SampleTestRoute() throws InterruptedException{
        template.sendBody("direct:sampleInput","Hello");
        Thread.sleep(5000);
        File file=new File("sampleOutput");
        assertTrue(file.isDirectory());
        Exchange exchange = consumer.receive("file:sampleOutput");
        System.out.println("Received body is :" + exchange.getIn().getBody());
        System.out.println("Received headers is :" + exchange.getIn().getHeaders());
        System.out.println("File Name is :" + exchange.getIn().getHeader("CamelFileName"));
        assertEquals("output.txt", exchange.getIn().getHeader("CamelFileName"));
    }
}