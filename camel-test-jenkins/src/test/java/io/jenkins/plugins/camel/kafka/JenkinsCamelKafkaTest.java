package io.jenkins.plugins.camel.kafka;


import io.jenkins.plugins.casc.misc.JenkinsConfiguredWithCodeRule;
import jenkins.model.Jenkins;
import lombok.extern.java.Log;
import lombok.val;
import org.junit.contrib.java.lang.system.EnvironmentVariables;
import org.junit.rules.RuleChain;
import org.jvnet.hudson.test.JenkinsRule;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


@Log
public class JenkinsCamelKafkaTest {

    //@Rule
    //public JenkinsRule jenkinsRule;
    //@Rule
    //public JenkinsRule jenkinsRule = new JenkinsRule();


    @Rule
    public RuleChain chain = RuleChain.outerRule( new EnvironmentVariables()
        .set("CASC_JENKINS_CONFIG", getClass().getResource("JenkinsCamelKafkaTest.yml").toExternalForm()))
        .around(new JenkinsConfiguredWithCodeRule());

    @Test
    public void loadFromCASC_JENKINS_CONFIG() {
        Jenkins j = Jenkins.get();
        assertEquals("configuration as code - JenkinsCamelKafkaTest", j.getSystemMessage());
    }

    @Test
    public void kafkaTriggeredPipeline() throws Exception {
        // TODO
        assertTrue(true); // FIXME
    }
}
