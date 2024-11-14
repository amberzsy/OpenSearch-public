/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * The OpenSearch Contributors require contributions made to
 * this file be licensed under the Apache-2.0 license or a
 * compatible open source license.
 */

package org.opensearch.grpc.services;

import io.grpc.BindableService;
import java.util.ArrayList;

/*
TODO: Service validation?
TODO: Handle compatibility/errors/dups here before we inject services into gRPC server
 */
public class gRPCServiceRegistry extends ArrayList<BindableService> {

    public gRPCServiceRegistry() { }

    public gRPCServiceRegistry(ArrayList<BindableService> services) {
        this.addAll(services);
    }
}
