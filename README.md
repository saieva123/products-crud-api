# Service

The project provides CRUD restful API example to show that how a backend server implements product management.

# App

A templated Finatra application. See [forthy/finatra.g8](https://github.com/forthy/finatra.g8).


# Build

To compile and run tests:

```
sbt compile test
```
* latest test coverage: 96%


## Elsewhere

Finatra is a standlone Java application. You can build and package this application in any way you choose (e.g. sbt-assembly).

To make this process as simple as possible, the project template includes [sbt/sbt-native-packager](https://github.com/sbt/sbt-native-packager). This allows you to generate:

* A docker image
* A linux package
* A Redhat package (`rpm`)
* A Debian package
* A Windows installer (`msi`)
* A native package (via `javapackager`)

For full details, see [sbt-native-packger's docs](http://www.scala-sbt.org/sbt-native-packager/formats/index.html)

## Docker

As an example, publishing this application to Docker locally is a cinch.

```
sbt docker:publishLocal
```

Check out the full sbt-native-packager documentation for all configuration options.

When this completes, run the image:

```
docker run $(docker images -a -q | head -1)
```

<kbd>CTRL</kbd>+<kbd>C</kbd> to kill the container.

The native packager's docker plugin gets you up and running in seconds. For longer term development, you'll probably find a `Dockerfile` to be a more maintainable solution.
