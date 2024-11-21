/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * The OpenSearch Contributors require contributions made to
 * this file be licensed under the Apache-2.0 license or a
 * compatible open source license.
 */

package org.opensearch.grpc.services.search;

import io.grpc.stub.StreamObserver;

import org.opensearch.action.search.SearchAction;
import org.opensearch.client.node.NodeClient;

import opensearch.proto.services.SearchServiceGrpc;


public class SearchServiceImpl extends SearchServiceGrpc.SearchServiceImplBase {
    private final NodeClient client;

    public SearchServiceImpl(NodeClient client) {
        this.client = client;
    }

    @Override
    public void search(opensearch.proto.SearchRequest searchRequestProto, StreamObserver<opensearch.proto.SearchResponse> responseObserver) {
        org.opensearch.action.search.SearchRequest searchReq = new org.opensearch.action.search.SearchRequest(searchRequestProto);
        GprcSearchAction.SearchRequestActionListener listener = new GprcSearchAction.SearchRequestActionListener(responseObserver);
        client.execute(SearchAction.INSTANCE, searchReq, listener);
    }
}
