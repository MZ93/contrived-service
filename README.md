### Import instructions:
```shell
git clone https://github.com/MZ93/contrived-service.git
```
* Import the project in your IDE. Example with IntelliJ, may differ depending on your IDE:

* File -> New -> Project from existing sources ...
* Select the project root directory (contrived-service)
* Import project from external model and select Gradle, click Create

You can now browse the demo code and run tests or run the main method.
Requirements: Java 17, gradle-8.5

### Sample api calls

Calling the /api/job/rank-tasks endpoint e.g.
POST request on a locally running instance of the service:
```shell
curl --location 'http://localhost:8080/api/job/rank-tasks' \
--header 'Content-Type: application/json' \
--data '{
    "tasks": [
        {
            "name": "task-1",
            "command": "touch /tmp/file1"
        },
        {
            "name": "task-2",
            "command": "cat /tmp/file1",
            "requires": [
                "task-3"
            ]
        },
        {
            "name": "task-3",
            "command": "echo '\''Hello World!'\'' > /tmp/file1",
            "requires": [
                "task-1"
            ]
        },
        {
            "name": "task-4",
            "command": "rm /tmp/file1",
            "requires": [
                "task-2",
                "task-3"
            ]
        }
    ]
}'
```

Will give the following response

```json
[
    {
        "name": "task-1",
        "command": "touch /tmp/file1",
        "requires": []
    },
    {
        "name": "task-3",
        "command": "echo 'Hello World!' > /tmp/file1",
        "requires": [
            "task-1"
        ]
    },
    {
        "name": "task-2",
        "command": "cat /tmp/file1",
        "requires": [
            "task-3"
        ]
    },
    {
        "name": "task-4",
        "command": "rm /tmp/file1",
        "requires": [
            "task-2",
            "task-3"
        ]
    }
]
```
And a POST request to /api/job/create-script:

```shell
curl --location 'http://localhost:8080/api/job/create-script' \
--header 'Content-Type: application/json' \
--data '{
    "tasks": [
        {
            "name": "task-1",
            "command": "touch /tmp/file1"
        },
        {
            "name": "task-2",
            "command": "cat /tmp/file1",
            "requires": [
                "task-3"
            ]
        },
        {
            "name": "task-3",
            "command": "echo '\''Hello World!'\'' > /tmp/file1",
            "requires": [
                "task-1"
            ]
        },
        {
            "name": "task-4",
            "command": "rm /tmp/file1",
            "requires": [
                "task-2",
                "task-3"
            ]
        }
    ]
}'
```
Will return:
```shell
touch /tmp/file1
echo 'Hello World!' > /tmp/file1
cat /tmp/file1
rm /tmp/file1
```