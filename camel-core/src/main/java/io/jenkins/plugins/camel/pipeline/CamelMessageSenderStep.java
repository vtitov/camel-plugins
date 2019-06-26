package io.jenkins.plugins.camel.pipeline;

import com.google.common.collect.ImmutableSet;
import hudson.Extension;
import hudson.Launcher;
import hudson.model.Run;
import hudson.model.TaskListener;
import io.jenkins.plugins.camel.CamelConfiguration;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.java.Log;
import org.apache.camel.FluentProducerTemplate;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.impl.DefaultProducerTemplate;
import org.jenkinsci.plugins.workflow.steps.*;
import org.kohsuke.stapler.DataBoundConstructor;

import javax.annotation.Nonnull;
import java.util.Set;
import java.util.logging.Level;

/*
 * The MIT License
 *
 * Copyright (c) Red Hat, Inc.
 * Copyright (c) Valentin Titov
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
@Log
public class CamelMessageSenderStep extends Step {

    //static private FluentProducerTemplate template;

    @Setter @Getter
    private String to;
    @Setter @Getter
    private String messageBody;


    @DataBoundConstructor
    public CamelMessageSenderStep(final String to,
                                  final String messageBody
    ) {
        super();
        this.to = to;
        this.messageBody = messageBody;
    }


    @Override
    public StepExecution start(StepContext context) throws Exception {
        return new CamelMessageSenderStep.Execution(this, context);
    }

    /**
     * Executes the sendCIMessage step.
     */
    public static final class Execution extends SynchronousNonBlockingStepExecution<Void> { // TODO consider async implementation from jms-messaging

        private transient final CamelMessageSenderStep step;

        Execution(CamelMessageSenderStep step, StepContext context) {
            super(context);
            this.step = step;
        }

        @Override protected Void run() throws Exception {
            //if(template == null)
            //new DefaultProducerTemplate(CamelConfiguration.get().getCamelContext())
            //        .sendBody(step.getTo(),step.getMessageBody());
            //FluentProducerTemplate template = CamelConfiguration.get().getCamelContext().createFluentProducerTemplate();

            try {
                // TODO cache ProducerTemplate
                log.fine(String.format("sending to [%s] message [%s]", step.getTo(), step.getMessageBody()));
                CamelConfiguration.get().getCamelContext().createFluentProducerTemplate()
                    .to(step.getTo())
                    .withBody(step.getMessageBody())
                    .send();
                return null;
            } catch (Exception e) {
                log.log(Level.WARNING, "error while sending to []", e);
                throw e;
            }
        }

        private static final long serialVersionUID = 1L;
    }


    /**
     * Adds the step as a workflow extension.
     */
    @Extension(optional = true)
    public static class DescriptorImpl extends StepDescriptor {

        @Override public Set<? extends Class<?>> getRequiredContext() {
            return ImmutableSet.of(Run.class, Launcher.class, TaskListener.class);
        }

        @Override
        public String getFunctionName() {
            return "sendCamelMessage";
        }

        @Override
        @Nonnull
        public String getDisplayName() {
            return "Send Camel Message";
        }
    }
}
