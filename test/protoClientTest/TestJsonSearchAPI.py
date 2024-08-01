import requests
import json
from concurrent.futures import ThreadPoolExecutor, as_completed
import time
import sys
import random
import concurrent


url = "http://localhost:5436/ecommerce/_search"
headers = {
    "Content-Type": "application/json",
    "RPC-Service":"XX"
}


MOCK_INDEX_NAME = "ecommerce"

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

term_map = {
        MOCK_TERM_FIELD_NAME1: MOCK_TERM_VALUE1,
        MOCK_TERM_FIELD_NAME2: MOCK_TERM_VALUE2,
        MOCK_TERM_FIELD_NAME3: MOCK_TERM_VALUE3
        }

def get_random_item_from_dict(my_dict):
    key = random.choice(list(my_dict.keys()))
    return key, my_dict[key]

def generate_filter_term():
    filter_name, value_name = get_random_item_from_dict(term_map)
    x = {filter_name: value_name}
    return x

def generate_all_filter_terms(count):

    result_dict = {}

    with concurrent.futures.ThreadPoolExecutor() as executor:
        future_to_key = {executor.submit(generate_filter_term, i) for i in range(count)}

        for future in concurrent.futures.as_completed(future_to_key):
            key, value = future.result()
            result_dict[key] = value
    print(result_dict)
    return result_dict

def generate_request(filter_term_count):
    search_request = {
        "query": {
            "bool": {
                "must": [
                    {
                        "match": {
                            "customer_first_name": "Eddie"
                        }
                    },
                    {
                        "match": {
                            "customer_last_name": "Underwood"
                        }
                    },
                    {
                        "match": {
                            "email": "eddie@underwood-family.zzz"
                        }
                    }
                ],
                "filter": [{"term": generate_filter_term()} for i in range(filter_term_count)]
            }
        }
    }
    data = json.dumps(search_request)
    return data

def make_request(filter_term_count):
    data = generate_request(filter_term_count)
    payload_size = sys.getsizeof(data)
    start = time.time()
    response = requests.post(url, headers=headers, data=data)
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

