# conductor-prometheus-metrics
This provides a shim to integrate prometheus metrics with Netflix Conductor.

The text here is a quick write up with very little details, I promise to clean it up as sson as I have some time.

The flow between the different components:

```
Conductor
   |
   | (publishes metrics through)
   |
   |-> Spectator
          |
          | (registers metrics into)
          |
          |-> MicrometerRegistry
                    |
                    | (registers metrics into)
                    |
                    |-> PrometheusMeterRegistry
```
The `PrometheusMeterRegistry` gets used in `PrometheusMetricsServlet` that returns the prometheus metrics in plain text format.

## Build
```
mvn pacakge
```

This will also generate an all-in-one JAR file (using `maven-shade-plugin``) that contains all the dependencies.

## How to integrate this with conductor
### Change the code of conductor
and just copy & paste the code of this repo (just two files) into your own fork of Conductor. You'll need to understand 
a bit about conductor server code, but it's not rocket science. In this case, this repo will just give you an idea about
how does it work.
### Plug the all-in-pne JAR into conductor
Use Conductor server "additional module" feature to dynamically load the `MetricsModule' into Conductor server.
For that, you won't have to touch the code of conductor itself (except for a bug mentioned bellow), but you still
need to build your own conductor docker image.

So for that, couple of changed are required:
1. Change the Conductor Docker image to include the metrics jar file.
2. Change the way conductor starts and use 'java -cp' insted of 'java -jar'

Some of this is mentioned in my comment [here](https://github.com/Netflix/conductor/issues/600#issuecomment-462403419).

In our current setup, we're running on Kubernetes so we build a docker image that contains all
what is needed for conductor server to run properly. So we build a Docker Image for metrics just as a wrapper for the
JAR file, and then we use it during building Conductor image to copy the JAR file into `/app/lib` directory in the final
image.

## Known Issue in Conductor Server
If you do all of the above, still it won't work. Because of some limitation in Conductor server dependency setup.
In short, there is some Module (SwaggerModule) that registers a Servlet that basically swallows all the requests because
It has this `serve("/*").with(DefaultServlet.class, params);`.
We have a fix for that, and will push a merge request for it soon.
