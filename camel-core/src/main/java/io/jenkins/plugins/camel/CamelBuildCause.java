package io.jenkins.plugins.camel;

import hudson.model.Cause;
import org.kohsuke.stapler.export.Exported;

import java.util.Map;

import static com.google.common.base.Strings.isNullOrEmpty;

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
public class CamelBuildCause extends Cause {

	private final Map<String, String> resolvedVariables;
	private final String cause;

	public CamelBuildCause(
			final Map<String, String> resolvedVariables,
			final String cause) {
		this.resolvedVariables = resolvedVariables;
		if (!isNullOrEmpty(cause)) {
			this.cause = cause;
		} else {
			this.cause = "Triggered by Camel message.";
		}
	}

	public Map<String, String> getResolvedVariables() {
		return resolvedVariables;
	}

	@Override
	@Exported(visibility = 3)
	public String getShortDescription() {
		return cause;
	}
}
