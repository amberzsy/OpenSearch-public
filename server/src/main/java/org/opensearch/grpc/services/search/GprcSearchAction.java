package org.opensearch.grpc.services.search;

import io.grpc.stub.StreamObserver;
import opensearch.proto.*;
import org.opensearch.action.search.SearchRequest;
import org.opensearch.action.search.SearchResponse;
import org.opensearch.action.search.SearchType;
import org.opensearch.action.search.ShardSearchFailure;
import org.opensearch.action.support.IndicesOptions;
import org.opensearch.common.unit.TimeValue;
import static org.opensearch.common.unit.TimeValue.parseTimeValue;
import org.opensearch.core.action.ActionListener;
import org.opensearch.core.common.Strings;
import org.opensearch.index.query.MatchAllQueryBuilder;
import org.opensearch.index.query.QueryBuilders;
import org.opensearch.index.query.QueryStringQueryBuilder;
import org.opensearch.index.query.TermQueryBuilder;
import org.opensearch.index.shard.IndexShard;
import org.opensearch.rest.RestRequest;
import org.opensearch.search.Scroll;
import org.opensearch.search.SearchService;
import org.opensearch.search.builder.SearchSourceBuilder;
import org.opensearch.search.internal.SearchContext;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.function.IntConsumer;
import java.util.stream.Collectors;

import static org.opensearch.rest.action.search.RestSearchAction.TOTAL_HITS_AS_INT_PARAM;


public class GprcSearchAction {
//    public static SearchRequest fromProto(opensearch.proto.SearchRequest proto) {
//        SearchRequest searchRequest = new SearchRequest();
//        IntConsumer setSize = size -> searchRequest.source().size(size);
//        if (searchRequest.source() == null) {
//            searchRequest.source(new SearchSourceBuilder());
//        }
//
//        searchRequest.setBatchedReduceSize(proto.getBatchedReduceSize());
//        if (proto.hasPreFilterShardSize()) {
//            searchRequest.setPreFilterShardSize(proto.getPreFilterShardSize());
//        }
//        if (proto.hasMaxConcurrentShardRequests()) {
//            searchRequest.setMaxConcurrentShardRequests(proto.getMaxConcurrentShardRequests());
//        }
//        if(proto.hasAllowPartialSearchResults()) {
//            searchRequest.allowPartialSearchResults(proto.getAllowPartialSearchResults());
//        } else {
//            searchRequest.allowPartialSearchResults(false);
//        }
//        if (proto.hasPhaseTook()) {
//            searchRequest.setPhaseTook(proto.getPhaseTook());
//        } else {
//            searchRequest.setPhaseTook(true);
//        }
//
//        switch (proto.getSearchType()) {
//            case SEARCH_TYPE_QUERY_THEN_FETCH:
//                searchRequest.searchType(SearchType.QUERY_THEN_FETCH);
//                break;
//            case SEARCH_TYPE_DFS_QUERY_THEN_FETCH:
//                searchRequest.searchType(SearchType.DFS_QUERY_THEN_FETCH);
//                break;
//            default:
//                searchRequest.searchType(SearchType.DEFAULT);
//        }
//        if (proto.hasRequestCache()) {
//           searchRequest.requestCache(proto.getRequestCache());
//        }
//        searchRequest.routing(String.join(",", proto.getRoutingList()));
//        searchRequest.preference(proto.getPreference());
//
//        EnumSet<IndicesOptions.Option> indicesoOptions = EnumSet.noneOf(IndicesOptions.Option.class);
//        EnumSet<IndicesOptions.WildcardStates> wildcardStates = EnumSet.noneOf(IndicesOptions.WildcardStates.class);
//
//        if (!proto.hasAllowNoIndices()) { // add option by default
//            indicesoOptions.add(IndicesOptions.Option.ALLOW_NO_INDICES);
//        } else if (proto.getAllowNoIndices()) {
//            indicesoOptions.add(IndicesOptions.Option.ALLOW_NO_INDICES);
//        }
//
//        if (proto.getIgnoreUnavailable()) {
//            indicesoOptions.add(IndicesOptions.Option.IGNORE_UNAVAILABLE);
//        }
//
//        if (!proto.hasIgnoreThrottled()) { // add option by default
//            indicesoOptions.add(IndicesOptions.Option.IGNORE_THROTTLED);
//        } else if (proto.getIgnoreThrottled()) {
//            indicesoOptions.add(IndicesOptions.Option.IGNORE_THROTTLED);
//        }
//
//        for (opensearch.proto.SearchRequest.ExpandWildcard wc : proto.getExpandWildcardsList()) {
//            switch (wc) {
//                case EXPAND_WILDCARD_OPEN:
//                    wildcardStates.add(IndicesOptions.WildcardStates.OPEN);
//                    break;
//                case EXPAND_WILDCARD_CLOSED:
//                    wildcardStates.add(IndicesOptions.WildcardStates.CLOSED);
//                    break;
//                case EXPAND_WILDCARD_HIDDEN:
//                    wildcardStates.add(IndicesOptions.WildcardStates.HIDDEN);
//                    break;
//                case EXPAND_WILDCARD_NONE:
//                    wildcardStates.clear();
//                    break;
//                case EXPAND_WILDCARD_ALL:
//                    wildcardStates.addAll(EnumSet.allOf(IndicesOptions.WildcardStates.class));
//                    break;
//            }
//        }
//
//        searchRequest.indicesOptions(new IndicesOptions(indicesoOptions, wildcardStates));
//
//        if (proto.hasSearchPipeline()) {
//            searchRequest.pipeline(proto.getSearchPipeline());
//        }
//        searchRequest.allowPartialSearchResults(proto.getAllowPartialSearchResults());
//
//        if (proto.hasCancelAfterTimeInterval()) {
//            TimeValue cancelAfter = new TimeValue(Long.parseLong(proto.getCancelAfterTimeInterval()));
//            searchRequest.setCancelAfterTimeInterval(cancelAfter);
//        }
//
//        searchRequest.setCcsMinimizeRoundtrips(true);
//        if (proto.hasCcsMinimizeRoundtrips()) {
//            searchRequest.setCcsMinimizeRoundtrips(proto.getCcsMinimizeRoundtrips());
//        }
//        searchSourceFromProto(searchRequest.source(), proto, setSize);
//        checkRestTotalHits(proto, searchRequest);
//
////        searchRequest.source(new org.opensearch.search.builder.SearchSourceBuilder().query(new MatchAllQueryBuilder()));
//        System.out.println("GRPC: " + searchRequest.toString());
//
//        return searchRequest;
//    }

    public static opensearch.proto.SearchResponse searchResponseToProto(org.opensearch.action.search.SearchResponse transportResponse) {
        ResponseBody.Builder responseBodyBuilder = ResponseBody.newBuilder();

        // [required] Milliseconds it took Elasticsearch to execute the request.
//        optional int64 took = 1;
        responseBodyBuilder.setTook(transportResponse.getTook().getMillis());
//
//        // [required]  If true, the request timed out before completion; returned results may be partial or empty.
//        optional bool timed_out = 2;
        responseBodyBuilder.setTimedOut(transportResponse.isTimedOut());
//
//        // [required] Contains a count of shards used for the request.
//        ShardStatistics shards = 3;
        responseBodyBuilder.setShards(ShardStatistics.newBuilder()
                .setFailed(transportResponse.getFailedShards())
                .setSuccessful(transportResponse.getSuccessfulShards())
                .setTotal(transportResponse.getTotalShards())
            .addAllFailures(getShardFails(transportResponse))
            .build());
//
//        // [optional] Phase-level took time values in the response.
//        optional PhaseTook phase_took = 4;
//
//        // [required] Contains returned documents and metadata.
//        HitsMetadata hits = 5;
        TotalHits.TotalHitsRelation totalHitsRelation = TotalHits.TotalHitsRelation.TOTAL_HITS_RELATION_INVALID;
        switch (transportResponse.getHits().getTotalHits().relation.name()) {
            case "EQUAL_TO":
                totalHitsRelation = TotalHits.TotalHitsRelation.TOTAL_HITS_RELATION_EQ;
                break;
            case "GREATER_THAN_OR_EQUAL_TO":
                totalHitsRelation = TotalHits.TotalHitsRelation.TOTAL_HITS_RELATION_GTE;
                break;
        }
//        transportResponse.getHits().getHits();
        responseBodyBuilder.setMaxScore(transportResponse.getHits().getMaxScore()).setHits(HitsMetadata.newBuilder()
                .setTotal(opensearch.proto.HitsMetadata.Total.newBuilder()
                    .setTotalHits(TotalHits.newBuilder()
                        .setValue(transportResponse.getHits().getTotalHits().value)
                        .setRelation(totalHitsRelation)
                        .build())
                    .build())
                .build())
            .build();

//
//        // [optional] When you search one or more remote clusters, a `_clusters` section is included to provide information about the search on each cluster.
//        optional ClusterStatistics clusters = 6;
//
//        // [optional] Retrieved specific fields in the search response
//        optional .google.protobuf.Struct fields = 7;
//
//        // [optional] Highest returned document _score.
//        optional float max_score = 8;
//
//        // [optional] The number of times that the coordinating node aggregates results from batches of shard responses
//        optional int32 num_reduce_phases = 9;
//
//        // [optional] Contains profiling information.
//        Profile profile = 10;
//
//        // [optional] The PIT ID.
//        optional string pit_id = 11;
//
//        // [optional] Identifier for the search and its search context.
//        optional string scroll_id = 12;
//
//        // [optional] If the query was terminated early, the terminated_early flag will be set to true in the response
//        optional bool terminated_early = 13;

        opensearch.proto.SearchResponse response = opensearch.proto.SearchResponse.newBuilder()
            .setResponseBody(responseBodyBuilder.build())
            .build();
        return response;
    }

    public static List<ShardFailure> getShardFails(org.opensearch.action.search.SearchResponse transportResponse) {
        ShardSearchFailure[] failures = transportResponse.getShardFailures();
        return Arrays.stream(failures)
            .map(failure -> ShardFailure.newBuilder()
                .setIndex(failure.index())
                .setShard(failure.shardId())
                .setStatus(failure.status().name())
                .build())
            .collect(Collectors.toList());

    }

    static class SearchRequestActionListener implements ActionListener<SearchResponse> {
        private StreamObserver<opensearch.proto.SearchResponse> respObserver = null;

        public SearchRequestActionListener(StreamObserver<opensearch.proto.SearchResponse> responseObserver){
            super();
            respObserver = responseObserver;
        }

        @Override
        public void onResponse(org.opensearch.action.search.SearchResponse response) {
            try {
                System.out.println("GRPC: " + response.toString());
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
            respObserver.onError(
                new RuntimeException("SearchRequest task failed", e)
            );
        }
    };

}

