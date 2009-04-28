/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.servicemix.kernel.gshell.osgi;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.BundleListener;
import org.osgi.service.blueprint.context.BlueprintContextListener;

/**
 *
 * TODO: use event admin to receive WAIT topics notifications from blueprint extender
 *
 */
public class GShellBlueprintContextListener implements BlueprintContextListener, BundleListener {

    public static enum BlueprintState {
        Unknown,
        Waiting,
        Started,
        Failed,
    }

    private static final Log LOG = LogFactory.getLog(GShellBlueprintContextListener.class);

    private final Map<Long, BlueprintState> states;
    private BundleContext bundleContext;

    public GShellBlueprintContextListener() {
        this.states = new ConcurrentHashMap<Long, BlueprintState>();
    }

    public BlueprintState getBlueprintState(Bundle bundle) {
        BlueprintState state = states.get(bundle.getBundleId());
        if (state == null || bundle.getState() != Bundle.ACTIVE) {
            state = BlueprintState.Unknown;
        }
        return state;
    }

    public void contextCreated(Bundle bundle) {
        LOG.debug("Blueprint app state changed to " + BlueprintState.Started + " for bundle " + bundle.getBundleId());
        states.put(bundle.getBundleId(), BlueprintState.Started);
    }

    public void contextCreationFailed(Bundle bundle, Throwable rootCause) {
        LOG.debug("Blueprint app state changed to " + BlueprintState.Failed + " for bundle " + bundle.getBundleId());
        states.put(bundle.getBundleId(), BlueprintState.Failed);
    }

    public void bundleChanged(BundleEvent event) {
        if (event.getType() == BundleEvent.UNINSTALLED) {
            states.remove(event.getBundle().getBundleId());
        }
    }

    public void setBundleContext(BundleContext bundleContext) {
        this.bundleContext = bundleContext;
    }

    public void afterPropertiesSet() throws Exception {
        bundleContext.addBundleListener(this);
    }

    public void destroy() throws Exception {
        bundleContext.removeBundleListener(this);
    }
}
