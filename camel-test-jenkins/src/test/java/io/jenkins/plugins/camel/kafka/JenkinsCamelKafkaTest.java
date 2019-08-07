package io.jenkins.plugins.camel.kafka;


import lombok.extern.java.Log;
import lombok.val;
import org.jvnet.hudson.test.JenkinsRule;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


@Log
public class JenkinsCamelKafaTest {

    @Rule
    public JenkinsRule jenkinsRule;

    @Test
    public void kafkaTriggeredPipelin() throws Exception {
        // TODO
        assertTrue(true); // FIXME
    }
}
