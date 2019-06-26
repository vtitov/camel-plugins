package io.jenkins.plugins.camel;

import hudson.model.Action;
import hudson.model.Queue.QueueAction;

import java.util.List;

public class CamelShouldScheduleQueueAction implements QueueAction {

    public Boolean schedule = false;

    public CamelShouldScheduleQueueAction(Boolean schedule) {
        this.schedule = schedule;
    }

    @Override
    public String getIconFileName() {
        return null;
    }

    @Override
    public String getDisplayName() {
        return null;
    }

    @Override
    public String getUrlName() {
        return null;
    }

    @Override
    public boolean shouldSchedule(List<Action> actions) {
         return schedule;
    }

}
