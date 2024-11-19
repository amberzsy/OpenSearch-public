/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * The OpenSearch Contributors require contributions made to
 * this file be licensed under the Apache-2.0 license or a
 * compatible open source license.
 */

package org.opensearch.grpc.services;

import io.grpc.stub.StreamObserver;

import org.opensearch.action.search.SearchAction;
import org.opensearch.client.node.NodeClient;
import org.opensearch.core.action.ActionListener;

import opensearch.proto.services.SearchServiceGrpc;
import opensearch.protos.ExplainRequest;
import opensearch.protos.ExplainResponse;
import opensearch.protos.IndexSearchRequest;
import opensearch.protos.IndexSearchResponse;
import opensearch.protos.SearchRequest;
import opensearch.protos.SearchResponse;

import static org.opensearch.grpc.services.helpers.IndexSearchRequestProtoHelper.searchRequestFromProto;

public class SearchServiceImpl extends SearchServiceGrpc.SearchServiceImplBase {
    private final NodeClient client;

    public SearchServiceImpl(NodeClient client) {
        this.client = client;
    }

    private static class SearchRequestActionListener implements ActionListener<org.opensearch.action.search.SearchResponse> {
        private StreamObserver<IndexSearchResponse> respObserver = null;

        public SearchRequestActionListener(StreamObserver<IndexSearchResponse> responseObserver){
            super();
            respObserver = responseObserver;
        }

        @Override
        public void onResponse(org.opensearch.action.search.SearchResponse response) {
            try {
                IndexSearchResponse protoResponse = respToProto(response);
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
            respObserver.onError(
                new RuntimeException("SearchRequest task failed", e)
            );
        }
    };

    // TODO: Redundant with indexSearch
    @Override
    public void search(SearchRequest proto, StreamObserver<SearchResponse> responseObserver) {
        throw new UnsupportedOperationException("Redundant search - Please use IndexSearch");
    }

    @Override
    public void indexSearch(IndexSearchRequest proto, StreamObserver<IndexSearchResponse> responseObserver) {
        org.opensearch.action.search.SearchRequest searchReq = searchRequestFromProto(proto);
        SearchRequestActionListener listener = new SearchRequestActionListener(responseObserver);
        client.execute(SearchAction.INSTANCE, searchReq, listener);
    }

    @Override
    public void explain(ExplainRequest request, StreamObserver<ExplainResponse> responseObserver) {
        // TODO: IMPL
        super.explain(request, responseObserver);
    }






    // TODO: IMPL
    private static IndexSearchResponse respToProto(org.opensearch.action.search.SearchResponse response) {
        return IndexSearchResponse.newBuilder().build();
    }
}
