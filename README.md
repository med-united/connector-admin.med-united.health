# connector-admin.med-united.health

A tool where you can maintain all the runtime configurations for all the doctors and their connectors

# Screenshot

![Detail Screen](docs/Detail-Screen.png?raw=true "This is how the tool looks like")
![View with Grafana](docs/Connector-Admin-Tool-With-Metrics.png?raw=true "Shows view with Grafana")


# Video

https://youtu.be/GyHE6LPloTY

# How to run this application?

Prerequisites:

1. Java 11 SDK running on your machine
2. Maven 3 running on your machine
3. Git running on your machine

Now you can start the application:
```
mvn wildfly:provision
mvn wildfly:start
mvn package wildfly:deploy
```
# How to stop the application?
```
mvn wildfly:undeploy
mvn wildfly:shutdown
```

# Basic auth info

The application is secured with basic auth. The specification is in the file src/main/webapp/WEB-INF/web.xml

To access the application you need to create a user in the wildfly server. To do so, you can use this script:
```
target/server/bin/add-user.sh -> Linux
target/server/bin/add-user.bat -> Windows
```

# Grafana
The JSON Model for building the Grafana Graph to view the OpenMetrics is in the /Grafana folder and can be imported via the "Import JSON" function in Grafana

# Example for metrics

```
curl --user admin:admin -H "Accept: application/json" http://localhost:9990/metrics

{
    "base": {
        "gc.total;name=G1 Young Generation1": 12,
        "gc.total;name=G1 Old Generation1": 0,
        "cpu.systemLoadAverage": 4.21,
        "thread.count": 83,
        "classloader.loadedClasses.count": 26777,
        "gc.time;name=G1 Old Generation1": 0,
        "gc.time;name=G1 Young Generation1": 325,
        "classloader.unloadedClasses.total": 10,
        "cpu.processCpuTime": 49590000000,
        "jvm.uptime": 110200,
        "memory.committedNonHeap": 208232448,
        "thread.max.count": 133,
        "memory.committedHeap": 406847488,
        "classloader.loadedClasses.total": 26787,
        "cpu.availableProcessors": 8,
        "thread.daemon.count": 24,
        "memory.usedNonHeap": 188995216,
        "memory.maxHeap": 4123000832,
        "cpu.processCpuLoad": 0.037115470352206865,
        "memory.usedHeap": 100582400,
        "memory.maxNonHeap": -1
    },
    "vendor": {
        "wildfly_io_connection_count;server=/127.0.0.1:8444;worker=default": 0.0,
        "wildfly_ejb3_task_count;thread_pool=default": 0.0,
        "wildfly_request_controller_active_requests": 0.0,
        "wildfly_jca_largest_thread_count;short_running_threads=default;workmanager=default": 0.0,
        "memoryPool.CodeHeap 'non-profiled nmethods'.usage": 7944320,
        "wildfly_jca_local_work_successful;workmanager=default": 0.0,
        "wildfly_ee_current_queue_size;managed_scheduled_executor_service=default": 0.0,
        "wildfly_datasources_pool_total_creation_time;data_source=ExampleDS": 0.0,
        "wildfly_io_connection_count;server=/127.0.0.1:8080;worker=default": 0.0,
        "memoryPool.Compressed Class Space.usage.max": 18214376,
        "wildfly_datasources_pool_xastart_total_time;data_source=ExampleDS": 0.0,
        "wildfly_transactions_number_of_resource_rollbacks": 0,
        "wildfly_transactions_number_of_application_rollbacks": 0,
        "wildfly_ee_thread_count;managed_executor_service=default": 0.0,
        "memoryPool.CodeHeap 'non-nmethods'.usage": 1624448,
        "memoryPool.G1 Eden Space.usage": 18874368,
        "wildfly_datasources_pool_xacommit_average_time;data_source=ExampleDS": 0.0,
        "wildfly_transactions_number_of_heuristics": 0,
        "memoryPool.CodeHeap 'profiled nmethods'.usage": 24618112,
        "wildfly_jca_local_schedulework_accepted;workmanager=default": 0.0,
        "wildfly_undertow_bytes_received;http_listener=default;server=default-server": 0,
        "BufferPool_used_memory_mapped": 0,
        "wildfly_io_busy_task_thread_count;worker=default": 0.0,
        "wildfly_datasources_pool_xacommit_max_time;data_source=ExampleDS": 0.0,
        "wildfly_ee_max_thread_count;managed_scheduled_executor_service=default": 0.0,
        "wildfly_batch_jberet_task_count;thread_pool=batch": 0.0,
        "wildfly_datasources_pool_destroyed_count;data_source=ExampleDS": 0.0,
        "wildfly_undertow_max_processing_time;https_listener=https;server=default-server": 0.0,
        "wildfly_ejb3_current_thread_count;thread_pool=default": 1.0,
        "wildfly_datasources_pool_average_creation_time;data_source=ExampleDS": 0.0,
        "wildfly_datasources_pool_xaend_max_time;data_source=ExampleDS": 0.0,
        "wildfly_jca_queue_size;short_running_threads=default;workmanager=default": 0.0,
        "wildfly_ejb3_largest_thread_count;thread_pool=default": 1.0,
        "wildfly_datasources_pool_total_blocking_time;data_source=ExampleDS": 0.0,
        "wildfly_datasources_pool_xaforget_average_time;data_source=ExampleDS": 0.0,
        "wildfly_jca_local_startwork_rejected;workmanager=default": 0.0,
        "wildfly_undertow_bytes_sent;http_listener=default;server=default-server": 0,
        "wildfly_io_queue_size;worker=default": 0.0,
        "memoryPool.G1 Old Gen.usage.max": 62833664,
        "wildfly_datasources_pool_xastart_count;data_source=ExampleDS": 0.0,
        "wildfly_batch_jberet_completed_task_count;thread_pool=batch": 0.0,
        "wildfly_datasources_jdbc_prepared_statement_cache_current_size;data_source=ExampleDS": 0.0,
        "wildfly_jca_largest_thread_count;long_running_threads=default;workmanager=default": 0.0,
        "wildfly_batch_jberet_active_count;thread_pool=batch": 0.0,
        "wildfly_transactions_number_of_timed_out_transactions": 0,
        "wildfly_datasources_pool_xarollback_max_time;data_source=ExampleDS": 0.0,
        "wildfly_ejb3_queue_size;thread_pool=default": 0.0,
        "wildfly_ee_hung_thread_count;managed_scheduled_executor_service=default": 0.0,
        "wildfly_datasources_pool_xarollback_average_time;data_source=ExampleDS": 0.0,
        "wildfly_jca_rejected_count;short_running_threads=default;workmanager=default": 0.0,
        "memoryPool.CodeHeap 'non-profiled nmethods'.usage.max": 7947520,
        "wildfly_transactions_average_commit_time": 0.0,
        "wildfly_datasources_pool_average_blocking_time;data_source=ExampleDS": 0.0,
        "wildfly_undertow_bytes_sent;https_listener=https;server=default-server": 0,
        "wildfly_datasources_jdbc_prepared_statement_cache_delete_count;data_source=ExampleDS": 0.0,
        "wildfly_undertow_error_count;http_listener=default;server=default-server": 0,
        "wildfly_datasources_pool_average_get_time;data_source=ExampleDS": 0.0,
        "wildfly_datasources_pool_xarecover_max_time;data_source=ExampleDS": 0.0,
        "wildfly_datasources_pool_xaforget_max_time;data_source=ExampleDS": 0.0,
        "wildfly_ee_completed_task_count;managed_executor_service=default": 0.0,
        "memoryPool.G1 Old Gen.usage": 62833664,
        "wildfly_datasources_pool_max_pool_time;data_source=ExampleDS": 0.0,
        "wildfly_ee_task_count;managed_executor_service=default": 0.0,
        "wildfly_datasources_pool_total_get_time;data_source=ExampleDS": 0.0,
        "wildfly_ee_completed_task_count;managed_scheduled_executor_service=default": 0.0,
        "wildfly_datasources_pool_idle_count;data_source=ExampleDS": 0.0,
        "wildfly_datasources_pool_total_pool_time;data_source=ExampleDS": 0.0,
        "wildfly_jca_local_startwork_accepted;workmanager=default": 0.0,
        "wildfly_jca_rejected_count;long_running_threads=default;workmanager=default": 0.0,
        "wildfly_transactions_number_of_transactions": 0,
        "wildfly_datasources_pool_total_usage_time;data_source=ExampleDS": 0.0,
        "wildfly_datasources_pool_xastart_average_time;data_source=ExampleDS": 0.0,
        "memoryPool.Metaspace.usage": 136664576,
        "wildfly_batch_jberet_rejected_count;thread_pool=batch": 0.0,
        "wildfly_ee_task_count;managed_scheduled_executor_service=default": 0.0,
        "wildfly_transactions_number_of_nested_transactions": 0,
        "wildfly_datasources_pool_xaprepare_count;data_source=ExampleDS": 0.0,
        "wildfly_undertow_request_count;http_listener=default;server=default-server": 0,
        "memoryPool.CodeHeap 'profiled nmethods'.usage.max": 24688000,
        "wildfly_datasources_pool_average_usage_time;data_source=ExampleDS": 0.0,
        "wildfly_jca_queue_size;long_running_threads=default;workmanager=default": 0.0,
        "memoryPool.CodeHeap 'non-nmethods'.usage.max": 1688960,
        "wildfly_ejb3_active_count;thread_pool=default": 0.0,
        "wildfly_datasources_pool_xarollback_total_time;data_source=ExampleDS": 0.0,
        "memoryPool.Compressed Class Space.usage": 18214376,
        "wildfly_undertow_processing_time;https_listener=https;server=default-server": 0,
        "wildfly_datasources_pool_max_wait_count;data_source=ExampleDS": 0.0,
        "wildfly_ee_active_thread_count;managed_scheduled_executor_service=default": 0.0,
        "wildfly_batch_jberet_queue_size;thread_pool=batch": 0.0,
        "wildfly_jca_local_work_active;workmanager=default": 0.0,
        "wildfly_undertow_max_processing_time;http_listener=default;server=default-server": 0.0,
        "wildfly_ee_thread_count;managed_scheduled_executor_service=default": 0.0,
        "wildfly_datasources_pool_xaprepare_max_time;data_source=ExampleDS": 0.0,
        "wildfly_datasources_jdbc_prepared_statement_cache_miss_count;data_source=ExampleDS": 0.0,
        "wildfly_datasources_pool_xaforget_total_time;data_source=ExampleDS": 0.0,
        "wildfly_datasources_pool_max_used_count;data_source=ExampleDS": 0.0,
        "wildfly_ee_max_thread_count;managed_executor_service=default": 0.0,
        "wildfly_datasources_jdbc_prepared_statement_cache_hit_count;data_source=ExampleDS": 0.0,
        "memoryPool.G1 Survivor Space.usage.max": 22020096,
        "wildfly_undertow_error_count;https_listener=https;server=default-server": 0,
        "wildfly_ejb3_rejected_count;thread_pool=default": 0.0,
        "wildfly_jca_local_dowork_accepted;workmanager=default": 0.0,
        "wildfly_datasources_pool_xaprepare_average_time;data_source=ExampleDS": 0.0,
        "wildfly_datasources_pool_xarollback_count;data_source=ExampleDS": 0.0,
        "wildfly_transactions_number_of_committed_transactions": 0,
        "wildfly_transactions_number_of_aborted_transactions": 0,
        "wildfly_datasources_pool_xarecover_average_time;data_source=ExampleDS": 0.0,
        "wildfly_datasources_jdbc_prepared_statement_cache_access_count;data_source=ExampleDS": 0.0,
        "wildfly_datasources_pool_xaforget_count;data_source=ExampleDS": 0.0,
        "wildfly_datasources_pool_created_count;data_source=ExampleDS": 0.0,
        "wildfly_jca_local_work_failed;workmanager=default": 0.0,
        "wildfly_io_max_pool_size;worker=default": 128.0,
        "wildfly_datasources_pool_timed_out;data_source=ExampleDS": 0.0,
        "wildfly_datasources_pool_wait_count;data_source=ExampleDS": 0.0,
        "wildfly_io_io_thread_count;worker=default": 16.0,
        "wildfly_jca_current_thread_count;short_running_threads=default;workmanager=default": 0.0,
        "wildfly_ee_current_queue_size;managed_executor_service=default": 0.0,
        "wildfly_datasources_pool_max_usage_time;data_source=ExampleDS": 0.0,
        "wildfly_datasources_pool_xacommit_total_time;data_source=ExampleDS": 0.0,
        "wildfly_datasources_pool_in_use_count;data_source=ExampleDS": 0.0,
        "wildfly_transactions_number_of_system_rollbacks": 0,
        "wildfly_datasources_pool_xaend_average_time;data_source=ExampleDS": 0.0,
        "memoryPool.G1 Survivor Space.usage": 18874368,
        "BufferPool_used_memory_direct": 1513049,
        "wildfly_datasources_pool_average_pool_time;data_source=ExampleDS": 0.0,
        "wildfly_datasources_pool_xacommit_count;data_source=ExampleDS": 0.0,
        "wildfly_ee_hung_thread_count;managed_executor_service=default": 0.0,
        "wildfly_datasources_pool_blocking_failure_count;data_source=ExampleDS": 0.0,
        "wildfly_undertow_request_count;https_listener=https;server=default-server": 0,
        "wildfly_transactions_number_of_inflight_transactions": 0.0,
        "wildfly_jca_local_schedulework_rejected;workmanager=default": 0.0,
        "wildfly_batch_jberet_largest_thread_count;thread_pool=batch": 0.0,
        "wildfly_datasources_pool_xastart_max_time;data_source=ExampleDS": 0.0,
        "wildfly_jca_local_dowork_rejected;workmanager=default": 0.0,
        "wildfly_datasources_pool_xaend_total_time;data_source=ExampleDS": 0.0,
        "memoryPool.Metaspace.usage.max": 136678544,
        "wildfly_datasources_pool_max_wait_time;data_source=ExampleDS": 0.0,
        "wildfly_datasources_pool_xaprepare_total_time;data_source=ExampleDS": 0.0,
        "wildfly_undertow_bytes_received;https_listener=https;server=default-server": 0,
        "wildfly_datasources_pool_max_creation_time;data_source=ExampleDS": 0.0,
        "memoryPool.G1 Eden Space.usage.max": 221249536,
        "wildfly_batch_jberet_current_thread_count;thread_pool=batch": 0.0,
        "wildfly_datasources_jdbc_prepared_statement_cache_add_count;data_source=ExampleDS": 0.0,
        "wildfly_ejb3_completed_task_count;thread_pool=default": 1.0,
        "wildfly_datasources_pool_available_count;data_source=ExampleDS": 0.0,
        "wildfly_datasources_pool_xarecover_total_time;data_source=ExampleDS": 0.0,
        "wildfly_ee_active_thread_count;managed_executor_service=default": 0.0,
        "wildfly_undertow_processing_time;http_listener=default;server=default-server": 0,
        "wildfly_datasources_pool_active_count;data_source=ExampleDS": 0.0,
        "wildfly_jca_current_thread_count;long_running_threads=default;workmanager=default": 0.0,
        "wildfly_io_core_pool_size;worker=default": 2.0,
        "wildfly_datasources_pool_xaend_count;data_source=ExampleDS": 0.0,
        "wildfly_datasources_pool_xarecover_count;data_source=ExampleDS": 0.0,
        "wildfly_datasources_pool_max_get_time;data_source=ExampleDS": 0.0
    },
    "application": {
        "connectorResponseTime_192.168.178.42": {
            "p99": 326870371,
            "min": 326870371,
            "max": 326870371,
            "mean": 326870371,
            "p50": 326870371,
            "p999": 326870371,
            "stddev": 0.0,
            "p95": 326870371,
            "p98": 326870371,
            "p75": 326870371,
            "fiveMinRate": 0.0,
            "fifteenMinRate": 0.0,
            "meanRate": 0.35160030144114307,
            "count": 1,
            "oneMinRate": 0.0,
            "elapsedTime": 326870371
        },
        "currentlyConnectedCards192.168.178.42": 4
    }
}
```
