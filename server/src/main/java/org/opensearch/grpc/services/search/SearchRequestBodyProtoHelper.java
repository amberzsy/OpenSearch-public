/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * The OpenSearch Contributors require contributions made to
 * this file be licensed under the Apache-2.0 license or a
 * compatible open source license.
 */

package org.opensearch.grpc.services.search;

import org.opensearch.index.query.MatchAllQueryBuilder;

public class SearchRequestBodyProtoHelper {
    public static org.opensearch.search.builder.SearchSourceBuilder searchSourceBuilderFromProto(opensearch.proto.SearchRequest searchRequestProto, opensearch.proto.SearchRequestBody searchRequestBodyProto) {
        org.opensearch.search.builder.SearchSourceBuilder sourceBuilder = new org.opensearch.search.builder.SearchSourceBuilder();

        /*
        message SearchRequestBody {
            optional QueryContainer query = 1;
        }
        message QueryContainer {
            optional MatchAllQuery match_all = 1;
        }
        message MatchAllQuery {
            optional float boost = 1;
            optional string name = 2;
        }
        */

        // TODO: Support more queries
        sourceBuilder.query(new MatchAllQueryBuilder());
        return sourceBuilder;
    }
}
