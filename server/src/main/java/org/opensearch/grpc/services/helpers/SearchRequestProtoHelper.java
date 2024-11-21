/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * The OpenSearch Contributors require contributions made to
 * this file be licensed under the Apache-2.0 license or a
 * compatible open source license.
 */

package org.opensearch.grpc.services.helpers;

import org.opensearch.action.support.IndicesOptions;
import org.opensearch.common.unit.TimeValue;
import org.opensearch.search.Scroll;
import org.opensearch.search.builder.SearchSourceBuilder;
import org.opensearch.search.internal.SearchContext;

import static org.opensearch.action.search.SearchRequest.DEFAULT_INDICES_OPTIONS;
import static org.opensearch.common.unit.TimeValue.parseTimeValue;

public class SearchRequestProtoHelper {

    public static org.opensearch.action.search.SearchRequest searchRequestFromProto(opensearch.proto.SearchRequest proto) {
        org.opensearch.action.search.SearchRequest searchReq = new org.opensearch.action.search.SearchRequest();
        if (searchReq.source() == null) {
            searchReq.source(new SearchSourceBuilder());
        }

        //[optional] Whether to include the _source field in the response.
        //optional SourceConfigParam source = 1;

        //[optional] A list of source fields to exclude from the response. You can also use this parameter to exclude fields from the subset specified in `source_includes` query parameter. If the `source` parameter is `false`, this parameter is ignored.
        //repeated string source_excludes = 2;

        //[optional] A list of source fields to include in the response. If this parameter is specified, only these source fields are returned. You can exclude fields from this subset using the `source_excludes` query parameter. If the `source` parameter is `false`, this parameter is ignored.
        //repeated string source_includes = 3 ;

        /*
        [optional] Whether to ignore wildcards that don't match any indexes. Default is true.
        optional bool allow_no_indices = 4;

        [optional] Specifies the type of index that wildcard expressions can match. Supports list of values. Default is open.
        repeated ExpandWildcard expand_wildcards = 14;

        [optional] Specifies whether to include missing or closed indexes in the response and ignores unavailable shards during the search request. Default is false.
        optional bool ignore_unavailable = 18;

        [optional] Whether to ignore concrete, expanded, or indexes with aliases if indexes are frozen. Default is true.
        optional bool ignore_throttled = 17;
         */
        IndicesOptions indicesOptions = IndicesOptions.fromParameters(
            proto.getExpandWildcardsList(),
            proto.getIgnoreUnavailable(),
            proto.getAllowNoIndices(),
            proto.getIgnoreThrottled(),
            DEFAULT_INDICES_OPTIONS);
        searchReq.indicesOptions(indicesOptions);

        //[optional] Whether to return partial results if the request runs into an error or times out. Default is true.
        //optional bool allow_partial_search_results = 5;
        searchReq.allowPartialSearchResults(proto.getAllowPartialSearchResults());

        //[optional] Whether the update operation should include wildcard and prefix queries in the analysis. Default is false.
        //optional bool analyze_wildcard = 6;

        //[optional] Analyzer to use for the query string. This parameter can only be used when the q query optional string parameter is specified.
        //optional string analyzer = 7;

        //[optional] How many shard results to reduce on a node. Default is 512.
        //optional int32 batched_reduce_size = 8;
        searchReq.setBatchedReduceSize(proto.getBatchedReduceSize());

        //[optional] The time after which the search request will be canceled. Request-level parameter takes precedence over cancel_after_time_interval cluster setting. Default is -1.
        //optional string cancel_after_time_interval = 9;
        TimeValue cancelAfter = new TimeValue(Long.parseLong(proto.getCancelAfterTimeInterval()));
        searchReq.setCancelAfterTimeInterval(cancelAfter);

        //[optional] Whether to minimize round-trips between a node and remote clusters. Default is true.
        //optional bool ccs_minimize_roundtrips = 10;
        searchReq.setCcsMinimizeRoundtrips(proto.getCcsMinimizeRoundtrips());

        //[optional] Indicates whether the default operator for a string query should be AND or OR. Default is OR.
        //optional Operator default_operator = 11;

        //[optional] The default field in case a field prefix is not provided in the query string.
        //optional string df = 12;

        //[optional] The fields that OpenSearch should return using their docvalue forms.
        //repeated string docvalue_fields = 13;

        //[optional] Whether to return details about how OpenSearch computed the document's score. Default is false.
        //optional bool explain = 15;

        //[optional] The starting index to search from. Default is 0.
        //optional int32 from = 16;

        //[optional] Whether to return scores with named queries. Default is false.
        //optional bool include_named_queries_score = 19;

        //[optional] Specifies whether OpenSearch should accept requests if queries have format errors (for example, querying a text field for an integer). Default is false.
        //optional bool lenient = 20;

        //[optional] Numbers of concurrent shard requests this request should execute on each node. Default is 5.
        //optional int32 max_concurrent_shard_requests = 21;
        searchReq.setMaxConcurrentShardRequests(proto.getMaxConcurrentShardRequests());

        //[optional] Whether to return phase-level took time values in the response. Default is false.
        //optional bool phase_took = 22;
        searchReq.setPhaseTook(proto.getPhaseTook());

        //[optional] A prefilter size threshold that triggers a prefilter operation if the request exceeds the threshold. Default is 128 shards.
        //optional int32 pre_filter_shard_size = 23;
        searchReq.setPreFilterShardSize(proto.getPreFilterShardSize());

        //[optional] Specifies the shards or nodes on which OpenSearch should perform the search.
        //optional string preference = 24;
        searchReq.preference(proto.getPreference());

        //[optional] Query in the Lucene query string syntax using query parameter search.
        //optional string q = 25;

        //[optional] Specifies whether OpenSearch should use the request cache. Default is whether it's enabled in the index's settings.
        //optional bool request_cache = 26;
        searchReq.requestCache(proto.getRequestCache());

        //[optional] Indicates whether to return hits.total as an integer. Returns an object otherwise. Default is false.
        //optional bool rest_total_hits_as_int = 27;

        //[optional] Value used to route the update by query operation to a specific shard.
        //repeated string routing = 28;
        searchReq.routing(proto.getRoutingList().toArray(new String[0]));

        //[optional] Period to keep the search context open.
        //optional string scroll = 29;
//        Scroll scroll = new Scroll(parseTimeValue(proto.getScroll(), null, "scroll"));
//        searchReq.scroll(scroll);

        //[optional] Customizable sequence of processing stages applied to search queries.
        //optional string search_pipeline = 30;
        searchReq.pipeline(proto.getSearchPipeline());

        //[optional] Whether OpenSearch should use global term and document frequencies when calculating relevance scores. Default is SEARCH_TYPE_QUERY_THEN_FETCH.
        //optional SearchType search_type = 31;
        String searchType = proto.getSearchType().name();
        if ("query_and_fetch".equals(searchType) || "dfs_query_and_fetch".equals(searchType)) {
            throw new IllegalArgumentException("Unsupported search type [" + searchType + "]");
        }
        searchReq.searchType(searchType);

        //[optional] Whether to return sequence number and primary term of the last operation of each document hit.
        //optional bool seq_no_primary_term = 32;

        //[optional] Number of results to include in the response.
        //optional int32 size = 33;

        //[optional] A list of <field> : <direction> pairs to sort by.
        //repeated string sort = 34;

        //[optional] Value to associate with the request for additional logging.
        //repeated string stats = 35;

        //[optional] Whether the get operation should retrieve fields stored in the index. Default is false.
        //repeated string stored_fields = 36;

        //[optional] Fields OpenSearch can use to look for similar terms.
        //optional string suggest_field = 37;

        //[optional] The mode to use when searching. This parameter can only be used when the `suggest_field` and `suggest_text` query optional string parameters are specified.
        //optional SuggestMode suggest_mode = 38;

        //[optional] Number of suggestions to return.
        //optional int32 suggest_size = 39;

        //[optional] The source that suggestions should be based off of.
        //optional string suggest_text = 40;

        //[optional] The maximum number of documents OpenSearch should process before terminating the request. Default is 0.
        //optional int32 terminate_after = 41;

        //[optional] Period of time to wait for a response from active shards. Default is 1m.
        //optional string timeout = 42;

        //[optional] Whether to return document scores. Default is false.
        //optional bool track_scores = 43;

        //[optional] Whether to return how many documents matched the query.
        //optional TrackHits track_total_hits = 44;
        checkRestTotalHits(proto.getRestTotalHitsAsInt(), searchReq); // set trackTotalHits

        //[optional] Whether returned aggregations and suggested terms should include their types in the response. Default is true.
        //optional bool typed_keys = 45;

        //[optional] Whether to include the document version as a match. Default is false
        //optional bool version = 46;

        //[optional] Search Request body
        //optional SearchRequestBody request_body = 47;

        return searchReq;
    }

    public static opensearch.proto.SearchResponse searchResponseToProto(org.opensearch.action.search.SearchResponse response) {
        return opensearch.proto.SearchResponse.newBuilder().build();
    }

    // TODO: Refactor RestSearchAction::checkRestTotalHits to this
    private static void checkRestTotalHits(boolean totalHitsAsInt, org.opensearch.action.search.SearchRequest searchRequest) {
        if (totalHitsAsInt == false) {
            return;
        }
        if (searchRequest.source() == null) {
            searchRequest.source(new SearchSourceBuilder());
        }
        Integer trackTotalHitsUpTo = searchRequest.source().trackTotalHitsUpTo();
        if (trackTotalHitsUpTo == null) {
            searchRequest.source().trackTotalHits(true);
        } else if (trackTotalHitsUpTo != SearchContext.TRACK_TOTAL_HITS_ACCURATE
            && trackTotalHitsUpTo != SearchContext.TRACK_TOTAL_HITS_DISABLED) {
            throw new IllegalArgumentException(
                "["
                    + "TOTAL_HITS_AS_INT_PARAM"
                    + "] cannot be used "
                    + "if the tracking of total hits is not accurate, got "
                    + trackTotalHitsUpTo
            );
        }
    }
}
