package io.jenkins.plugins.camel;

import hudson.Extension;
import hudson.model.Action;
import hudson.model.Item;
import hudson.model.Project;
import hudson.model.RootAction;
import io.vavr.control.Option;
import lombok.extern.java.Log;
import org.apache.camel.CamelContext;
import org.apache.camel.Endpoint;
import org.jenkinsci.Symbol;
import org.kohsuke.stapler.StaplerProxy;

import javax.annotation.CheckForNull;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import static io.vavr.API.TODO;

@Log
@Extension @Symbol("camel")
public class CamelAction implements RootAction, StaplerProxy {

    private Project project = null;
    public CamelAction(Project project) {
        this.project = project;
    }

    public CamelAction() {}

    public Map<String, Endpoint> getEndpointMap() {
        return Option.of(CamelConfiguration.get())
                .map(CamelConfiguration::getCamelContext)
                .map(CamelContext::getEndpointMap)
                .getOrElse(Collections::emptyMap);
    }

    public  int getEndpointsCount() {
        int count = 0;
        // TODO
        return count;
    }


    @CheckForNull
    @Override
    public String getIconFileName() {
        return "help.png";
    }

    @CheckForNull
    @Override
    public String getDisplayName() {
        return "Camel";
    }

    @CheckForNull
    @Override
    public String getUrlName() {
        return "camel";
    }

    @Override
    public Object getTarget() {
        if(this.project != null) this.project.checkPermission(Item.CONFIGURE);
        return this;
    }
}
