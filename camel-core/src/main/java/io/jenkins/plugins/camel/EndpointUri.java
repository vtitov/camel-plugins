package io.jenkins.plugins.camel;

import hudson.Extension;
import hudson.model.AbstractDescribableImpl;
import hudson.model.Descriptor;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.kohsuke.stapler.DataBoundConstructor;

@AllArgsConstructor(onConstructor=@__({@DataBoundConstructor}))
public class EndpointUri extends AbstractDescribableImpl<EndpointUri> {
    @Getter
    private String uri;

    @Extension
    public static class DescriptorImpl extends Descriptor<EndpointUri> {
        public String getDisplayName() { return "Endpoint Uri"; }
    }
}
