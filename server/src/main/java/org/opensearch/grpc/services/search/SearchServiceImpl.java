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
import org.opensearch.core.action.ActionListener;

import opensearch.proto.services.SearchServiceGrpc;
import org.opensearch.wlm.QueryGroupTask;
import org.opensearch.wlm.WorkloadManagementTransportInterceptor;

import static org.opensearch.grpc.services.search.SearchRequestProtoHelper.searchRequestFromProto;
import static org.opensearch.grpc.services.search.SearchRequestProtoHelper.searchResponseToProto;

public class SearchServiceImpl extends SearchServiceGrpc.SearchServiceImplBase {
    private final NodeClient client;

    public SearchServiceImpl(NodeClient client) {
        this.client = client;
    }

    private static class SearchRequestActionListener implements ActionListener<org.opensearch.action.search.SearchResponse> {
        private StreamObserver<opensearch.proto.SearchResponse> respObserver = null;

        public SearchRequestActionListener(StreamObserver<opensearch.proto.SearchResponse> responseObserver){
            super();
            respObserver = responseObserver;
        }

        @Override
        public void onResponse(org.opensearch.action.search.SearchResponse response) {
            try {
                opensearch.proto.SearchResponse protoResponse = searchResponseToProto(response);
                respObserver.onNext(protoResponse);
                respObserver.onCompleted();
            } catch (Exception e) {
                respObserver.onError(
                    new RuntimeException("Failed to process SearchResponse", e)
                );
            }
        }

        @Override
        public void onFailure(Exception e) {
            // DEBUG PRINT
            System.out.println(e.getMessage());

            respObserver.onError(
                new RuntimeException("SearchRequest task failed", e)
            );
        }
    };

    @Override
    public void search(opensearch.proto.SearchRequest searchRequestProto, StreamObserver<opensearch.proto.SearchResponse> responseObserver) {
        org.opensearch.action.search.SearchRequest searchReq = searchRequestFromProto(searchRequestProto);
        SearchRequestActionListener listener = new SearchRequestActionListener(responseObserver);
        client.execute(SearchAction.INSTANCE, searchReq, listener);
    }
}
