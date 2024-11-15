/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * The OpenSearch Contributors require contributions made to
 * this file be licensed under the Apache-2.0 license or a
 * compatible open source license.
 */

package org.opensearch.grpc.services;

import io.grpc.stub.StreamObserver;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.opensearch.client.node.NodeClient;

import opensearch.proto.services.SearchServiceGrpc;
import opensearch.protos.ExplainRequest;
import opensearch.protos.ExplainResponse;
import opensearch.protos.IndexSearchRequest;
import opensearch.protos.IndexSearchResponse;
import opensearch.protos.SearchRequest;
import opensearch.protos.SearchResponse;

public class SearchServiceImpl extends SearchServiceGrpc.SearchServiceImplBase {
    private static final Logger logger = LogManager.getLogger(SearchServiceImpl.class);
    private final NodeClient client;

    public SearchServiceImpl(NodeClient client) {
        this.client = client;
    }

    @Override
    public void search(SearchRequest request, StreamObserver<SearchResponse> responseObserver) {
        super.search(request, responseObserver);
    }

    @Override
    public void indexSearch(IndexSearchRequest request, StreamObserver<IndexSearchResponse> responseObserver) {
        super.indexSearch(request, responseObserver);
    }

    @Override
    public void explain(ExplainRequest request, StreamObserver<ExplainResponse> responseObserver) {
        super.explain(request, responseObserver);
    }
}
