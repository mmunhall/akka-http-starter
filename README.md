akka-http-starter
=================

Overview
--------

akka-http-starter is a simple Akka HTTP application intended to be used as a starting point for building out a more sophisticated application.

Packaging
---------

akka-http-starter is packaged as a fat executable JAR. To build the JAR:

    mvn clean package

The packaged JAR will be located at `server/target/akka-http-starter-server-[version].jar`.

Running
-------

To run the application from the command line:

    java -Dconfig.file=[path/to/application.conf] -Dlogback.configurationFile=path/to/logback.xml] -jar [path/to/executable/jar.jar]

There are two properties that should be specified for non-local development:

* _config.file_: The path to the application configuration file.
* _logback.configurationFile_: The path to the logging configuration file.

If either of these properties are not defined, configurations suitable for local development (`server/src/main/resources/{application.conf, logback.xml}`) will be loaded by default.

Usage
-----

The application contains a single test route: `/api`. The route requires a parameter `message`. The route will simply
echo the value of the `message` parameter unless the value is "fail", in which case an HTTP status code of 500 is returned.

### Examples ###

    http://localhost:8080/api?message=helloworld
    http://localhost:8080/api?message=fail