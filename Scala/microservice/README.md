# Project: Runway

Project: Runway exposes data models in an easy to use, high-availability RESTful interface.

## Getting Started

These instructions will get you a copy of the project up and running on your local machine for development and testing purposes. See deployment for notes on how to deploy the project on a live system.

### Prerequisites

A general understanding of RESTFul web services, routing, *functional* Scala development
and data modeling is required to work through this. You need to have a handle on your machine's architecture, 
especially the command-line, CLASSPATHS, proxy information, etc.


### Installing

One the repo is cloned, you can run it (via SBT) from either the project root or from the MicroService
project

```
sbt:Project: Runway >  project MicroService
sbt:Project: Runway - MicroService >  run
```

From a web client (e.g. browser, or CURL) you can check the currently available models

```
$ curl http://localhost:8080/models
```

To add a new model, simply post the pmml/xml file with a project name, a file ID and the path file path 
to the /models directory

```
$ curl -X POST \
-F "name=<your_model_name>" \
-F "project=<your_project_name>" \
-F "file=@/path/to/your_pmml.xml" \
-F "description=<your_model_description>" \
-F "author=<your_name>"  \
http://<hostname>:8080/api/v1/models
```

which should return

```$xslt
uploaded 'your_pmml.xml' and created model 'slugified-model-name'
```
PUT (to update) and DELETE follow the same pattern, with appropriate fields.

To get a estimate, send a JSON file to your model as so:
```
$ curl -X POST \
-H "Content-Type: application/json" \
-d '{"your_json": 0, "goes_here": 1}' \
http://<hostname>:8080/api/v1/models/<your_slugified-model-name>
```

To get a batch of estimates, you can post a CSV file:
```
$ curl -X POST \
-F "csv=@<your_batch_file_path> \
http://<hostname>:8080/api/v1/models/<your_slugified-model-name>/batch
```

CSV missing headers? Just add them to the POST command via a 'fields' entry:
```
$ curl -X POST \
-F "csv=@<your_batch_file_path> \
-F "fields='feat_a, feat_b, feat_c, feat_d'" \
http://<hostname>:8080/api/v1/models/<your_slugified-model-name>/batch
```

*note, incorrect feature names will return an error


## Running the tests

From an sbt console, simply run a test
```
sbt test
```

Or for test in a specific project, switch to that project in sbt and run the test.
Projects can be listed by simply running the projects* command in sbt

```
sbt:Project: Runway >  project MicroService
sbt:Project: Runway - MicroService >   test
```
## Deployment

To deploy, simply compile a 'fat' jar from sbt. First switch to the MicroService project, them compile with an 
'assembly' command
```
sbt:Project: Runway >  project MicroService
sbt:Project: Runway - MicroService >  assembly
```

Then, deploy from your terminal with (or without) a config file. The default settings currently live in the source
at `microservice/src/main/resources/application.conf`. This can be copied to somewhere in your file system.


Deployment with a config is:
```
$ java -Dconfig.file=<path/to/your/>.conf -jar 'Project: Runway - MicroService-assembly-0.0.4.jar'
```


## Built With

* [Akka](https://akka.io/docs/) - Reactive Scala tool kit
* [SBT](https://www.scala-sbt.org/documentation.html) - Dependency Management
* [jPMML](https://github.com/jpmml/jpmml-evaluator) - PMML parser

## Contributing

Please read [CONTRIBUTING.md](https://onestash.verizon.com/users/v603497/repos/runway/browse/CONTRIBUTING.md) for details on our code of conduct, and the process for submitting pull requests to us.

## Versioning

We use [SemVer](http://semver.org/) for versioning. For the versions available, see the [tags on this repository](https://onestash.verizon.com/users/v603497/repos/runway/browse). 

## Authors

* **Alvaro Muir** - *Initial work*

## License

VZ "Highly Confidential" or "Confidential" data should be restricted to authorized personnel only per CPI-810. Users must ensure their information and documents comply with this policy.

## Acknowledgments

* Thanks for trying it out and submitting bug requests

