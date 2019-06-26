package io.jenkins.plugins.camel.processor;

import lombok.extern.java.Log;
import lombok.val;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.apache.commons.io.FileUtils;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.junit.Assert.*;

@Log
public class CamelProcessorTest extends CamelTestSupport {

    private static final long DURATION_MILIS = 5000;
    //private static final long DURATION_MILIS = 1000000000;
    private static final String SOURCE_FOLDER = "target/source-folder";
    private static final String DESTINATION_FOLDER
            = "target/destination-folder";

    static public class FileProcessor implements Processor {
        public void process(Exchange exchange) throws Exception {
            String originalFileName = (String) exchange.getIn().getHeader(
                    Exchange.FILE_NAME, String.class);

            Date date = new Date();
            SimpleDateFormat dateFormat = new SimpleDateFormat(
                    "yyyy-MM-dd HH-mm-ss");
            String changedFileName = dateFormat.format(date) + originalFileName;
            exchange.getIn().setHeader(Exchange.FILE_NAME, changedFileName);
        }
    }

    @Ignore // FIXME FileNotFoundException
    public void moveFolderContentJavaDSLTest() throws Exception {
        val camelContext = new DefaultCamelContext();
        camelContext.addRoutes(new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("file://" + SOURCE_FOLDER + "?delete=true").process(
                        new FileProcessor()).to("file://" + DESTINATION_FOLDER);
            }
        });
        new File(SOURCE_FOLDER).mkdirs();
        new File(DESTINATION_FOLDER).mkdirs();
        val fileName = "test.txt";
        val fileContent = "Hello File";
        FileUtils.writeStringToFile(new File(SOURCE_FOLDER, fileName), fileContent);
        camelContext.start();
        Thread.sleep(DURATION_MILIS);
        camelContext.stop();

        assertEquals(
                "file content should match for %s".format(fileName),
                fileContent,
                FileUtils.readFileToString(new File(DESTINATION_FOLDER, fileName)));
    }
}
