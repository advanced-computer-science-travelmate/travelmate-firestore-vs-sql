import asyncio
import aiohttp
import time
import json

# Target your local or cloud Spring Boot routing path
URL = "http://localhost:8080/api/travel/trips/create"

# Define the payload template structure
payload_template = {
    "destinationName": "Sweden",
    "startDate": "2027-06-07",
    "endDate": "2027-06-08",
    "maxTravelers": 2,
    "adults": 1,
    "children": 0,
    "rooms": 1
}

async def send_single_record(session, record_index):
    # Alter payload slightly per request if desired
    payload = payload_template.copy()
    
    start_time = time.time()
    try:
        async with session.post(URL, json=payload) as response:
            status = response.status
            # Read response body text
            await response.text()
            end_time = time.time()
            
            duration_ms = (end_time - start_time) * 1000
            return {"record": record_index, "status": status, "latency_ms": duration_ms, "success": status == 200}
    except Exception as e:
        end_time = time.time()
        return {"record": record_index, "status": "CRASHED", "latency_ms": (end_time - start_time) * 1000, "success": False}

async def generate_traffic_pool(total_records, concurrent_batch_size):
    print(f"🚀 Firing traffic pool: Sending {total_records} records in batches of {concurrent_batch_size}...")
    
    # Configure connection pool limits
    connector = aiohttp.TCPConnector(limit=concurrent_batch_size)
    async with aiohttp.ClientSession(connector=connector) as session:
        tasks = []
        for i in range(1, total_records + 1):
            tasks.append(send_single_record(session, i))
        
        # Gather metrics over all network handshakes
        results = await asyncio.gather(*tasks)
        return results

def process_benchmark_metrics(results):
    successful_writes = [r for r in results if r["success"]]
    total_records = len(results)
    
    print("\n" + "="*40)
    print("📊 BENCHMARK PERFORMANCE INSIGHTS")
    print("="*40)
    print(f"Total Records Transmitted : {total_records}")
    print(f"Successful Operations    : {len(successful_writes)} / {total_records}")
    
    if successful_writes:
        latencies = [r["latency_ms"] for r in successful_writes]
        avg_latency = sum(latencies) / len(latencies)
        max_latency = max(latencies)
        min_latency = min(latencies)
        
        print(f"Average Response Time    : {avg_latency:.2f} ms")
        print(f"Fastest Write Response   : {min_latency:.2f} ms")
        print(f"Slowest Write Response   : {max_latency:.2f} ms")
        print("-" * 40)
        print("Detailed Log Split (Records vs Latency):")
        for r in results[:15]: # Show first 15 for console summary scannability
            print(f" -> Record #{r['record']:03d} | Status: {r['status']} | Response Time: {r['latency_ms']:.2f} ms")
        if total_records > 15:
            print(f" ... [{total_records - 15} more records logged successfully]")
    print("="*40)

if __name__ == "__main__":
    # Settings: Send 50 trips altogether, making 10 requests concurrently at a time
    TOTAL_RECORDS = 50
    CONCURRENCY_LIMIT = 10
    
    metrics_pool = asyncio.run(generate_traffic_pool(TOTAL_RECORDS, CONCURRENCY_LIMIT))
    process_benchmark_metrics(metrics_pool)