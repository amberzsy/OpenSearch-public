/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * The OpenSearch Contributors require contributions made to
 * this file be licensed under the Apache-2.0 license or a
 * compatible open source license.
 */

package org.opensearch.grpc.services.document;

import io.grpc.stub.StreamObserver;
import opensearch.proto.BulkRequest;
import opensearch.proto.BulkResponse;
import opensearch.proto.CreateOperation;
import opensearch.proto.DeleteOperation;
import opensearch.proto.IndexOperation;
import opensearch.proto.ResponseItem;
import opensearch.proto.UpdateOperation;
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
    public void bulk(BulkRequest request, StreamObserver<BulkResponse> responseObserver) {
        super.bulk(request, responseObserver);
    }

    @Override
    public void index(IndexOperation request, StreamObserver<ResponseItem> responseObserver) {
        super.index(request, responseObserver);
    }

    @Override
    public void create(CreateOperation request, StreamObserver<ResponseItem> responseObserver) {
        super.create(request, responseObserver);
    }

    @Override
    public void update(UpdateOperation request, StreamObserver<ResponseItem> responseObserver) {
        super.update(request, responseObserver);
    }

    @Override
    public void delete(DeleteOperation request, StreamObserver<ResponseItem> responseObserver) {
        super.delete(request, responseObserver);
    }
}
