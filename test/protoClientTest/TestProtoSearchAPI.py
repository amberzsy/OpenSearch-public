import sys
import requests
import base64
import json
from concurrent.futures import ThreadPoolExecutor, as_completed
import time
import random
import concurrent

sys.path.append('/home/user/IdeaProjects/searchProto/generated')

from generated import MatchQueryProto_pb2 as MatchQueryProto
from generated import TermQueryProto_pb2 as TermQueryProto
from generated import BoolQueryProto_pb2 as BoolQueryProto
from generated import SearchRequestProto_pb2 as SearchRequestProto

url = "http://localhost:5436/_search_proto"
headers = {
    "Content-Type": "application/json",
    "RPC-Service":"XX"
}

MOCK_INDEX_NAME = "ecommerce"
MOCK_MATCH_FIELD_NAME1 = "customer_first_name"
MOCK_MATCH_VALUE1 = "Eddie"
MOCK_MATCH_FIELD_NAME2 = "customer_last_name"
MOCK_MATCH_VALUE2 = "Underwood"
MOCK_MATCH_FIELD_NAME3 = "email"
MOCK_MATCH_VALUE3 = "eddie@underwood-family.zzz"
MOCK_TERM_FIELD_NAME1 = "day_of_week"
MOCK_TERM_VALUE1 = "Monday"
MOCK_TERM_FIELD_NAME2 = "customer_gender"
MOCK_TERM_VALUE2 = "MALE"
MOCK_TERM_FIELD_NAME3 = "currency"
MOCK_TERM_VALUE3 = "EUR"

def get_random_item_from_dict(my_dict):
    key = random.choice(list(my_dict.keys()))
    return key, my_dict[key]

def generate_filter_term(i):
    term_map = {
        MOCK_TERM_FIELD_NAME1: MOCK_TERM_VALUE1,
        MOCK_TERM_FIELD_NAME2: MOCK_TERM_VALUE2,
        MOCK_TERM_FIELD_NAME3: MOCK_TERM_VALUE3
        }
    filter_name, value_name = get_random_item_from_dict(term_map)
    term_value = value_name
    term_query = TermQueryProto.TermQuery(
        fieldName=filter_name,
        value=term_value
    )
    return term_query

def generate_all_filter_terms(count):

    with concurrent.futures.ThreadPoolExecutor() as executor:
        futures = [executor.submit(generate_filter_term, i) for i in range(count)]
        results = [future.result() for future in concurrent.futures.as_completed(futures)]
    return results

def create_search_request(filter_term_count):
    mock_match_query1 = MatchQueryProto.MatchQuery(
        fieldName=MOCK_MATCH_FIELD_NAME1,
        value=MOCK_MATCH_VALUE1
    )

    mock_match_query2 = MatchQueryProto.MatchQuery(
        fieldName=MOCK_MATCH_FIELD_NAME2,
        value=MOCK_MATCH_VALUE2
    )

    mock_match_query3 = MatchQueryProto.MatchQuery(
        fieldName=MOCK_MATCH_FIELD_NAME3,
        value=MOCK_MATCH_VALUE3
    )

    filters = generate_all_filter_terms(filter_term_count)

    bool_query_builder = BoolQueryProto.BoolQuery(
        must=[mock_match_query1, mock_match_query2, mock_match_query3],
        filter=filters
    )

    query_builder = SearchRequestProto.SearchRequest.SourceBuilder.QueryBuilder(
        bool=bool_query_builder
    )

    source_builder = SearchRequestProto.SearchRequest.SourceBuilder(
        query=query_builder
    )

    search_request_builder = SearchRequestProto.SearchRequest(
        indices=[MOCK_INDEX_NAME],
        sourceBuilder=source_builder,
        routing="null",
        preference="null"
    )

    search_request_bytes = search_request_builder.SerializeToString()
    return search_request_bytes


def make_request(filter_term_count):
    data = create_search_request()
    payload_size = sys.getsizeof(data)
    start = time.time()
    response = requests.get(url, headers=headers, data=data)
    end = time.time()
    latency = end - start
    if response.status_code == 200:
        response_json = response.json()
        took = response_json.get('took', 0)
        print(response_json)
        return took, latency
    else:
        print(f"Error: {response.status_code}, Content: {response.content}")
        return 0, latency

def run_multithreaded_requests(num_threads, num_calls):
    filter_term_count = 200
    total_took = 0
    total_latency = 0

    with ThreadPoolExecutor(max_workers=num_threads) as executor:

        futures = [executor.submit(make_request, filter_term_count) for _ in range(num_calls)]

        for future in as_completed(futures):
            took, latency = future.result()
            total_latency += latency
            total_took += took

    return total_took, total_latency


if __name__ == "__main__":
    num_threads = 3
    num_calls = 1000
    run_multithreaded_requests(num_threads, num_calls)

