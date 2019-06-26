package io.jenkins.plugins.camel.test;

import java.io.File;
import java.io.IOException;
import java.util.UUID;
import java.util.logging.Level;

import lombok.extern.java.Log;
import org.apache.camel.RoutesBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

@Log
public class CopyFileRouteTest extends CamelTestSupport {

    static String dataId = "target/data/" + UUID.randomUUID().toString();

    static String fromPath = dataId + "/input";
    static String toPath   = dataId + "/output";

    //@Rule public TemporaryFolder temporaryFolder = new TemporaryFolder();
    //@Rule public TemporaryFolder temporaryFolder = new TemporaryFolder(new File("./target/data"));

    //    @Before
    //    public void createTempDirs() {
    //        try {
    //            temporaryFolder.newFolder("input");
    //            temporaryFolder.newFolder("output");
    //        } catch (Exception e) {
    //            log.log(Level.FINE,"temp dir create error", e);
    //            System.err.println("temp dir create error" + e.getMessage());
    //        }
    //        System.out.println("temporaryFolder: " + temporaryFolder.getRoot());
    //    }

    @Before
    public void createDirs() {
        new File(fromPath).mkdirs();
        new File(toPath).mkdirs();
    }


    static public class CopyFilesRoute extends RouteBuilder {
        final private String fromUri;
        final private String toUri;


        CopyFilesRoute() {
            this.fromUri = String.format("file:%s?noop=true", fromPath);
            this.toUri = String.format("file:%s", toPath);
        }
        public void configure() throws Exception{
            from(fromUri)
                    //.multicast()
                    //.recipientList()
                    .to(toUri);
        }
    }

    @Override
    protected RoutesBuilder createRouteBuilder() throws Exception {
        return new CopyFilesRoute();
    }
    @Test
    public void checkFileExistsInOutputDirectory() throws InterruptedException, IOException
    {
        FileUtils.writeStringToFile(new File(fromPath, UUID.randomUUID().toString() + ".txt"), UUID.randomUUID().toString());
        FileUtils.writeStringToFile(new File(fromPath, UUID.randomUUID().toString() + ".txt"), UUID.randomUUID().toString());
        Thread.sleep(5000);
        File file = new File(toPath);
        assertTrue(file.isDirectory());
        assertEquals(2,file.listFiles().length);
    }
}