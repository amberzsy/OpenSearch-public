/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * The OpenSearch Contributors require contributions made to
 * this file be licensed under the Apache-2.0 license or a
 * compatible open source license.
 */

package org.opensearch.grpc.services;

import io.grpc.stub.StreamObserver;
import opensearch.protos.BulkRequest;
import opensearch.protos.BulkResponse;
import opensearch.protos.DeleteDocumentRequest;
import opensearch.protos.DeleteDocumentResponse;
import opensearch.protos.GetDocumentRequest;
import opensearch.protos.GetDocumentResponse;
import opensearch.protos.GetDocumentSourceRequest;
import opensearch.protos.GetDocumentSourceResponse;
import opensearch.protos.IndexBulkRequest;
import opensearch.protos.IndexBulkResponse;
import opensearch.protos.IndexDocumentCreateIdRequest;
import opensearch.protos.IndexDocumentCreateIdResponse;
import opensearch.protos.IndexDocumentIdRequest;
import opensearch.protos.IndexDocumentIdResponse;
import opensearch.protos.IndexDocumentRequest;
import opensearch.protos.IndexDocumentResponse;
import opensearch.protos.UpdateDocumentRequest;
import opensearch.protos.UpdateDocumentResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.opensearch.client.node.NodeClient;

import opensearch.proto.services.DocumentServiceGrpc;

public class DocumentServiceImpl extends DocumentServiceGrpc.DocumentServiceImplBase {
    private static final Logger logger = LogManager.getLogger(DocumentServiceImpl.class);
    private final NodeClient client;

    public DocumentServiceImpl(NodeClient client) {
        this.client = client;
    }

    @Override
    public void indexDocument(IndexDocumentRequest request, StreamObserver<IndexDocumentResponse> responseObserver) {
        super.indexDocument(request, responseObserver);
    }

    @Override
    public void indexDocumentId(IndexDocumentIdRequest request, StreamObserver<IndexDocumentIdResponse> responseObserver) {
        super.indexDocumentId(request, responseObserver);
    }

    @Override
    public void indexDocumentCreateId(IndexDocumentCreateIdRequest request, StreamObserver<IndexDocumentCreateIdResponse> responseObserver) {
        super.indexDocumentCreateId(request, responseObserver);
    }

    @Override
    public void bulk(BulkRequest request, StreamObserver<BulkResponse> responseObserver) {
        super.bulk(request, responseObserver);
    }

    @Override
    public void indexBulk(IndexBulkRequest request, StreamObserver<IndexBulkResponse> responseObserver) {
        super.indexBulk(request, responseObserver);
    }

    @Override
    public void deleteDocument(DeleteDocumentRequest request, StreamObserver<DeleteDocumentResponse> responseObserver) {
        super.deleteDocument(request, responseObserver);
    }

    @Override
    public void updateDocument(UpdateDocumentRequest request, StreamObserver<UpdateDocumentResponse> responseObserver) {
        super.updateDocument(request, responseObserver);
    }

    @Override
    public void getDocument(GetDocumentRequest request, StreamObserver<GetDocumentResponse> responseObserver) {
        super.getDocument(request, responseObserver);
    }

    @Override
    public void getDocumentSource(GetDocumentSourceRequest request, StreamObserver<GetDocumentSourceResponse> responseObserver) {
        super.getDocumentSource(request, responseObserver);
    }
}
