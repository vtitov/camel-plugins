package io.jenkins.plugins.camel;

import hudson.EnvVars;
import hudson.model.AbstractBuild;
import hudson.model.EnvironmentContributingAction;
import hudson.model.ParameterValue;
import hudson.model.Run;
import lombok.extern.java.Log;
import lombok.val;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

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
public class CamelEnvironmentContributingAction implements EnvironmentContributingAction {


    private transient Map<String, String> messageParams;
    private transient Set<String> jobParams = new HashSet<String>();

    public CamelEnvironmentContributingAction(Map<String, String> messageParams) {
        this(messageParams, null);
    }

    public CamelEnvironmentContributingAction(Map<String, String> mParams, List<ParameterValue> jParams) {
        this.messageParams = mParams;
        if (jParams != null) {
            for (ParameterValue pv : jParams) {
                this.jobParams.add(pv.getName());
            }
        }
    }

    public String getIconFileName() {
        return null;
    }

    public String getDisplayName() {
         return null;
    }

    public String getUrlName() {
        return null;
    }

    @Override
    public void buildEnvironment(Run<?, ?> run, EnvVars env) {

        if (env == null || messageParams == null) {
            return;
        }

        // Only include variables in environment that are not defined as job parameters. And
        // do not overwrite any existing environment variables (like PATH).
        //for (String key : messageParams.keySet()) {
        //    //log.fine("message param: " + key);
        //    if (!jobParams.contains(key) && !env.containsKey(key)) {
        //        log.fine(String.format("putting to env message param: [%s] -> [%s]", key, messageParams.get(key)));
        //        env.put(key, messageParams.get(key));
        //    }
        //}
        for (val entry: messageParams.entrySet()) {
            val key = entry.getKey();
            val value = entry.getValue();
            //log.fine("message param: " + key);
            if (!jobParams.contains(key) && !env.containsKey(key)) {
                log.fine(String.format("putting to env message param: [%s] -> [%s]", key, value));
                env.put(key, key);
            }
        }
    }
}
