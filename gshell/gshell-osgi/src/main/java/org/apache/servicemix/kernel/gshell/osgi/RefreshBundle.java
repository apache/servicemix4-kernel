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

import org.apache.geronimo.gshell.clp.Argument;
import org.apache.servicemix.kernel.gshell.core.OsgiCommandSupport;
import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceReference;
import org.osgi.service.packageadmin.PackageAdmin;

public class RefreshBundle extends OsgiCommandSupport {

    @Argument(required = false)
    Long id;

    protected Object doExecute() throws Exception {
        // Get package admin service.
        ServiceReference ref = getBundleContext().getServiceReference(PackageAdmin.class.getName());
        if (ref == null) {
            io.out.println("PackageAdmin service is unavailable.");
            return Result.FAILURE;
        }
        try {
            PackageAdmin pa = (PackageAdmin) getBundleContext().getService(ref);
            if (pa == null) {
                io.out.println("PackageAdmin service is unavailable.");
                return Result.FAILURE;
            }
            if (id == null) {
                pa.refreshPackages(null);
            }
            else {
                Bundle bundle = getBundleContext().getBundle(id);
                if (bundle == null) {
                    io.out.println("Bundle " + id + " not found");
                    return Result.FAILURE;
                }
                pa.refreshPackages(new Bundle[] { bundle });
            }
        }
        finally {
            getBundleContext().ungetService(ref);
        }
        return Result.SUCCESS;
    }
}