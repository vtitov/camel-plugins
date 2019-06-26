package io.jenkins.plugins.camel;

import hudson.Extension;
import hudson.model.Action;
import hudson.model.Project;
import jenkins.model.TransientActionFactory;
import lombok.extern.java.Log;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Collections;

@Log
@Extension
public class CamelActionFactory extends TransientActionFactory<Project> {
    @Override
    public Class<Project> type() {
        return Project.class;
    }

    @Nonnull
    @Override
    public Collection<? extends Action> createFor(@Nonnull Project project) {
        return Collections.singleton(new CamelAction(project));
    }
}
