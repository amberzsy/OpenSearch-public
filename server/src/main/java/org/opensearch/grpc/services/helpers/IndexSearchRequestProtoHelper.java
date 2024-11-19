/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * The OpenSearch Contributors require contributions made to
 * this file be licensed under the Apache-2.0 license or a
 * compatible open source license.
 */

package org.opensearch.grpc.services.helpers;

import opensearch.protos.IndexSearchRequest;
import opensearch.protos.IndexSearchRequestBody;
import org.opensearch.action.support.IndicesOptions;
import org.opensearch.common.unit.TimeValue;
import org.opensearch.search.Scroll;
import org.opensearch.search.builder.SearchSourceBuilder;
import org.opensearch.search.internal.SearchContext;

import static org.opensearch.action.search.SearchRequest.DEFAULT_INDICES_OPTIONS;
import static org.opensearch.common.unit.TimeValue.parseTimeValue;
import static org.opensearch.search.builder.SearchSourceBuilder.TIMEOUT_FIELD;

public class IndexSearchRequestProtoHelper {

    public static org.opensearch.action.search.SearchRequest searchRequestFromProto(IndexSearchRequest proto) {
        org.opensearch.action.search.SearchRequest searchReq = new org.opensearch.action.search.SearchRequest();
        if (searchReq.source() == null) {
            searchReq.source(new SearchSourceBuilder());
        }

        /*
        [required] A list of indices to search for documents. Allowing targeted searches within one or more specified indices.
        repeated string index = 1;
         */
        String[] indexList = proto.getIndexList().toArray(new String[0]);
        searchReq.indices(indexList);


        /*
        [optional] Specifies the type of index that wildcard expressions can match. Supports list of values. Default is open.
        repeated ExpandWildcard expand_wildcards = 15;

        [optional] Specifies whether to include missing or closed indexes in the response and ignores unavailable shards during the search request. Default is false.
        .google.protobuf.BoolValue ignore_unavailable = 19;

        [optional] Whether to ignore wildcards that don't match any indexes. Default is true.
        .google.protobuf.BoolValue allow_no_indices = 5;

        [optional] Whether to ignore concrete, expanded, or indexes with aliases if indexes are frozen. Default is true.
        .google.protobuf.BoolValue ignore_throttled = 18;
        */
        IndicesOptions indicesOptions = IndicesOptions.fromParameters(
            proto.getExpandWildcardsList(),
            proto.getIgnoreUnavailable(),
            proto.getAllowNoIndices(),
            proto.getIgnoreThrottled(),
            DEFAULT_INDICES_OPTIONS);
        searchReq.indicesOptions(indicesOptions);

        /*
        [optional] Period to keep the search context open.
        .google.protobuf.StringValue scroll = 30;
         */
        Scroll scroll = new Scroll(parseTimeValue(proto.getScroll().getValue(), null, "scroll"));
        searchReq.scroll(scroll);

        /*
        [optional] The time after which the search request will be canceled. Request-level parameter takes precedence over cancel_after_time_interval cluster setting. Default is -1.
        .google.protobuf.StringValue cancel_after_time_interval = 10;
        */
        TimeValue cancelAfter = new TimeValue(Long.parseLong(proto.getCancelAfterTimeInterval().getValue()));
        searchReq.setCancelAfterTimeInterval(cancelAfter);

        /*
        [optional] How many shard results to reduce on a node. Default is 512.
        .google.protobuf.Int32Value batched_reduce_size = 9;
         */
        searchReq.setBatchedReduceSize(proto.getBatchedReduceSize().getValue());

        /*
        [optional] Whether OpenSearch should use global term and document frequencies when calculating relevance scores. Default is SEARCH_TYPE_QUERY_THEN_FETCH.
        SearchType search_type = 32;
         */
        String searchType = proto.getSearchType().name();
        if ("query_and_fetch".equals(searchType) || "dfs_query_and_fetch".equals(searchType)) {
            throw new IllegalArgumentException("Unsupported search type [" + searchType + "]");
        }
        searchReq.searchType(searchType);

        /*
        [optional] A prefilter size threshold that triggers a prefilter operation if the request exceeds the threshold. Default is 128 shards.
        .google.protobuf.Int32Value pre_filter_shard_size = 24;
         */
        searchReq.setPreFilterShardSize(proto.getPreFilterShardSize().getValue());

        /*
        [optional] Numbers of concurrent shard requests this request should execute on each node. Default is 5.
        .google.protobuf.Int32Value max_concurrent_shard_requests = 22;
         */
        searchReq.setMaxConcurrentShardRequests(proto.getMaxConcurrentShardRequests().getValue());

        /*
        [optional] Whether to return partial results if the request runs into an error or times out. Default is true.
        .google.protobuf.BoolValue allow_partial_search_results = 6;
         */
        searchReq.allowPartialSearchResults(proto.getAllowPartialSearchResults().getValue());

        /*
        [optional] Whether to return phase-level took time values in the response. Default is false.
        .google.protobuf.BoolValue phase_took = 23;
         */
        searchReq.setPhaseTook(proto.getPhaseTook().getValue());

        /*
        [optional] Specifies whether OpenSearch should use the request cache. Default is whether it's enabled in the index's settings.
        .google.protobuf.BoolValue request_cache = 27;
         */
        searchReq.requestCache(proto.getRequestCache().getValue());

        /*
        [optional] Value used to route the update by query operation to a specific shard.
        repeated string routing = 29;
         */
        searchReq.routing(proto.getRoutingList().toArray(new String[0]));

        /*
        [optional] Specifies the shards or nodes on which OpenSearch should perform the search. For valid values see "https://opensearch.org/docs/latest/api-reference/search/#the-preference-query-parameter"
        .google.protobuf.StringValue preference = 25;
         */
        searchReq.preference(proto.getPreference().getValue());

        /*
        [optional] Customizable sequence of processing stages applied to search queries.
        .google.protobuf.StringValue search_pipeline = 31;
         */
        searchReq.pipeline(proto.getSearchPipeline().getValue());

        /*
        [optional] Indicates whether to return hits.total as an integer. Returns an object otherwise. Default is false.
        .google.protobuf.BoolValue rest_total_hits_as_int = 28;
         */
        checkRestTotalHits(proto.getRestTotalHitsAsInt().getValue(), searchReq); // set trackTotalHits

        /*
        [optional] Whether to minimize round-trips between a node and remote clusters. Default is true.
        .google.protobuf.BoolValue ccs_minimize_roundtrips = 11;
         */
        searchReq.setCcsMinimizeRoundtrips(proto.getCcsMinimizeRoundtrips().getValue());


        // TODO: FOR THIS ONE MAKE SURE YOU TRACK WHAT TOP LEVEL REQUEST ITEMS ARE SET HERE
        // TODO: PROBABLY JUST PASS IN ANY INDEXSEARCHREQUEST MEMBERS YOU NEED IN THE SOURCE
        /*
        [optional] Search Request body
        IndexSearchRequestBody request_body = 48;
         */
        searchReq.source(searchSourceBuilderFromProto(proto.getRequestBody(), proto));

        // [optional] Whether to include the _source field in the response.
        //SourceConfigParam source = 2 [json_name = "_source"];

        // [optional] A list of source fields to exclude from the response. You can also use this parameter to exclude fields from the subset specified in `source_includes` query parameter. If the `source` parameter is `false`, this parameter is ignored.
        //repeated string source_excludes = 3;

        // [optional] A list of source fields to include in the response. If this parameter is specified, only these source fields are returned. You can exclude fields from this subset using the `source_excludes` query parameter. If the `source` parameter is `false`, this parameter is ignored.
        //repeated string source_includes = 4 ;

        // [optional] Whether the update operation should include wildcard and prefix queries in the analysis. Default is false.
        //.google.protobuf.BoolValue analyze_wildcard = 7;

        // [optional] Analyzer to use for the query string. This parameter can only be used when the q query .google.protobuf.StringValue parameter is specified.
        //.google.protobuf.StringValue analyzer = 8;

        // [optional] Indicates whether the default operator for a string query should be AND or OR. Default is OR.
        //Operator default_operator = 12;

        // [optional] The default field in case a field prefix is not provided in the query string.
        //.google.protobuf.StringValue df = 13;

        // [optional] The fields that OpenSearch should return using their docvalue forms.
        //repeated string docvalue_fields = 14;

        // [optional] Whether to return details about how OpenSearch computed the document's score. Default is false.
        //.google.protobuf.BoolValue explain = 16;

        // [optional] The starting index to search from. Default is 0.
        //.google.protobuf.Int32Value from = 17;

        // [optional] Whether to return scores with named queries. Default is false.
        //.google.protobuf.BoolValue include_named_queries_score = 20;

        // [optional] Specifies whether OpenSearch should accept requests if queries have format errors (for example, querying a text field for an integer). Default is false.
        //.google.protobuf.BoolValue lenient = 21;

        // [optional] Query in the Lucene query string syntax using query parameter search.
        //.google.protobuf.StringValue q = 26;

        // [optional] Whether to return sequence number and primary term of the last operation of each document hit.
        //.google.protobuf.BoolValue seq_no_primary_term = 33;

        // [optional] Number of results to include in the response.
        //.google.protobuf.Int32Value size = 34;

        // [optional] A list of <field> : <direction> pairs to sort by.
        //repeated string sort = 35;

        // [optional] Value to associate with the request for additional logging.
        //repeated string stats = 36;

        // [optional] Whether the get operation should retrieve fields stored in the index. Default is false.
        //repeated string stored_fields = 37;

        // [optional] Fields OpenSearch can use to look for similar terms.
        //.google.protobuf.StringValue suggest_field = 38;

        // [optional] The mode to use when searching. This parameter can only be used when the `suggest_field` and `suggest_text` query .google.protobuf.StringValue parameters are specified.
        //SuggestMode suggest_mode = 39;

        // [optional] Number of suggestions to return.
        //.google.protobuf.Int32Value suggest_size = 40;

        // [optional] The source that suggestions should be based off of.
        //.google.protobuf.StringValue suggest_text = 41;

        // [optional] The maximum number of documents OpenSearch should process before terminating the request. Default is 0.
        //.google.protobuf.Int32Value terminate_after = 42;

        // [optional] Period of time to wait for a response from active shards. Default is 1m.
        //.google.protobuf.StringValue timeout = 43;

        // [optional] Whether to return document scores. Default is false.
        //.google.protobuf.BoolValue track_scores = 44;

        //// [optional] Whether to return how many documents matched the query.
        //TrackHits track_total_hits = 45;

        // [optional] Whether returned aggregations and suggested terms should include their types in the response. Default is true.
        //.google.protobuf.BoolValue typed_keys = 46;

        // [optional] Whether to include the document version as a match. Default is false
        //.google.protobuf.BoolValue version = 47;

        return searchReq;
    }

    private static SearchSourceBuilder searchSourceBuilderFromProto(IndexSearchRequestBody requestBodyProto, IndexSearchRequest requestProto) {
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();

        /*
        [optional] The starting index to search from. Default is 0.
        .google.protobuf.Int32Value from = 5;
        */
        sourceBuilder.from(requestBodyProto.getFrom().getValue());

        /*
        [optional] The number of results to return. Default is 10.
        google.protobuf.Int32Value size = 18;
        */
        sourceBuilder.size(requestBodyProto.getSize().getValue());

        /*
        [optional] The period of time to wait for a response. Default is no timeout. If no response is received before the timeout expires, the request fails and returns an error. Defaults to no timeout.
        .google.protobuf.StringValue timeout = 25;
        */
        sourceBuilder.timeout(TimeValue.parseTimeValue(requestBodyProto.getTimeout().toString(), TIMEOUT_FIELD.getPreferredName()))

        /*
        [optional] The maximum number of documents OpenSearch should process before terminating the request. If a query reaches this limit, OpenSearch terminates the query early. OpenSearch collects documents before sorting. Use with caution. OpenSearch applies this parameter to each shard handling the request. When possible, let OpenSearch perform early termination automatically. Avoid specifying this parameter for requests that target data streams with backing indices across multiple data tiers. If set to `0` (default), the query does not terminate early. Default is 0.
        .google.protobuf.Int32Value terminate_after = 24;
        */
        sourceBuilder.terminateAfter(requestBodyProto.getTerminateAfter().getValue());

        /*
        [optional] In the optional aggs parameter, you can define any number of aggregations. Each aggregation is defined by its name and one of the types of aggregations that OpenSearch supports.
        map<string, AggregationContainer> aggregations = 1;
        */

        /*
        [optional] The collapse parameter groups search results by a particular field value. This returns only the top document within each group, which helps reduce redundancy by eliminating duplicates.
        FieldCollapse collapse = 2;
        */

        /*
        [optional] Whether to return details about how OpenSearch computed the document's score. Default is false.
        .google.protobuf.BoolValue explain = 3;
        */

        /*
        [optional] ext object is to contain plugin-specific response fields. For example, in conversational search, the result of Retrieval Augmented Generation (RAG) is a single “hit” (answer). Plugin authors can include this answer in the search response as part of the ext object so that it is separate from the search hits.
        ObjectMap ext = 4;
        */


        /*
        [optional] Highlighting emphasizes the search term(s) in the results so you can emphasize the query matches.
        Highlight highlight = 6;
        */

        /*
        [optional] Whether to return how many documents matched the query.
        TrackHits track_total_hits = 7;
        */

        /*
        [optional] Values used to boost the score of specified indexes. Specify in the format of <index> : <boost-multiplier>
        repeated NumberMap indices_boost = 8;
        */

        /*
        [optional] The fields that OpenSearch should return using their docvalue forms. Specify a format to return results in a certain format, such as date and time.
        repeated FieldAndFormat docvalue_fields = 9;
        */

        /*
        RankContainer rank = 10;
        */

        /*
        [optional] Specify a score threshold to return only documents above the threshold.
        .google.protobuf.FloatValue min_score = 11;
        */

        /*
        [optional] Use post_filter to refine search hits based on user selections while preserving all aggregation options.
        QueryContainer post_filter = 12;
        */

        /*
        [optional] Profile provides timing information about the execution of individual components of a search request. Using the Profile API, you can debug slow requests and understand how to improve their performance.
        .google.protobuf.BoolValue profile = 13;
        */

        /*
        [optional] The DSL query to use in the request.
        QueryContainer query = 14;
        */

        /*
        [optional] Can be used to improve precision by reordering just the top (for example 100 - 500) documents returned by the `query` and `post_filter` phases.
        repeated Rescore rescore = 15;
        */

        /*
        [optional] The script_fields parameter allows you to include custom fields whose values are computed using scripts in your search results. This can be useful for calculating values dynamically based on the document data. You can also retrieve derived fields by using a similar approach.
        map<string, ScriptField> script_fields = 16;
        */

        /*
        [optional] The search_after parameter provides a live cursor that uses the previous page's results to obtain the next page's results. It is similar to the scroll operation in that it is meant to scroll many queries in parallel. You can use search_after only when sorting is applied.
        repeated FieldValue search_after = 17;
        */

        /*
        [optional] You can use the scroll operation to retrieve a large number of results. For example, for machine learning jobs, you can request an unlimited number of results in batches.
        SlicedScroll slice = 19;
        */

        /*
        [optional] Sorting allows your users to sort results in a way that's most meaningful to them. By default, full-text queries sort results by the relevance score. You can choose to sort the results by any field value in either ascending or descending order by setting the order parameter to asc or desc.
        repeated SortCombinations sort = 20;
        */

        /*
        [optional] Whether to include the _source field in the response.
        SourceConfig source = 21 [json_name = "_source"];
        */

        /*
        [optional] The fields to search for in the request. Specify a format to return results in a certain format, such as date and time.
        repeated FieldAndFormat fields = 22;
        */

        /*
        [optional] The suggest feature suggests similar looking terms based on a provided text by using a suggester. The suggest request part is defined alongside the query part in a _search request. If the query part is left out, only suggestions are returned.
        Suggester suggest = 23;
        */

        /*
        [optional] Whether to return document scores. Default is false.
        .google.protobuf.BoolValue track_scores = 26;
        */

        /*
        [optional] Whether to include the document version in the response.
        .google.protobuf.BoolValue version = 27;
        */

        /*
        [optional] Whether to return sequence number and primary term of the last operation of each document hit.
        .google.protobuf.BoolValue seq_no_primary_term = 28;
        */

        /*
        [optional] A list of stored fields to return as part of a hit. If no fields are specified, no stored fields are included in the response. If this option is specified, the _source parameter defaults to false. You can pass _source: true to return both source fields and stored fields in the search response.
        repeated string stored_fields = 29;
        */

        /*
        [optional] Point in Time (PIT) lets you run different queries against a dataset that is fixed in time.
        PointInTimeReference pit = 30;
        */
        if (requestBodyProto.hasPit()) {
            throw new UnsupportedOperationException("PIT not supported.");
        }

        /*
        [optional] Value to associate with the request for additional logging.
        repeated string stats = 31;
        */

        return sourceBuilder;
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
