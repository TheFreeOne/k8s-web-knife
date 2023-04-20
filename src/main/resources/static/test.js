var qwe = {
    "code": 10000,
    "msg": "success",
    "data": [
    {
        "uid": "fbf4b7cd-18db-4108-bf18-f6cdf34b3e48",
        "namespace": "default",
        "name": "mytomcat-deployment",
        "replicas": 1,
        "creationTimestamp": "2023-02-01T14:57:45+08:00",
        "containers": []
    },
    {
        "uid": "bb2ff211-0820-4e99-a1c2-7c5cd710f4d5",
        "namespace": "default",
        "name": "tomcat-deployment",
        "replicas": 1,
        "creationTimestamp": "2023-01-31T18:24:31+08:00",
        "containers": []
    }
]
}


var asd = {
    "code": 10000,
    "msg": "success",
    "data": {
    "uid": "fbf4b7cd-18db-4108-bf18-f6cdf34b3e48",
        "namespace": "default",
        "name": "mytomcat-deployment",
        "replicas": 1,
        "creationTimestamp": "2023-02-01T14:57:45+08:00",
        "containers": [],
        "podVoList": [
        {
            "name": "mytomcat-deployment-5b6dc9b76c-lrr4h",
            "phase": "Running",
            "hostIP": "192.168.110.130",
            "podIP": "10.244.0.91",
            "restartCount": 10,
            "startedAt": "2023-02-16T17:47:04+08:00"
        },
        {
            "name": "tomcat-deployment-7b7d9f6fdb-fj8qc",
            "phase": "Running",
            "hostIP": "192.168.110.130",
            "podIP": "10.244.0.88",
            "restartCount": 10,
            "startedAt": "2023-02-16T17:46:59+08:00"
        },
        {
            "name": "tomcat-deployment-7c455d5ccd-9fkrg",
            "phase": "Running",
            "hostIP": "192.168.110.130",
            "podIP": "10.244.0.93",
            "restartCount": 269,
            "startedAt": "2023-02-16T18:03:08+08:00"
        }
    ]
}
}
