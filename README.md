# java-grpc-example

**java-grpc-example** is a simple example project demonstarting use of [gRPC](https://grpc.io/) in Java + Gradle environment.

The project consists of several modules:

* `math-service-api`, a common library with gRPC-generated classes and some additional helper classes;
* `math-service-impl`, a calculator-like micro-service which implements service APIs defined in `math-service-api`;
* `math-service-client`, a simple client able to connect to a micro-service implementing `math-service-api` and submit mathematical expressions.

## Prerequisites

Only Java SDK is required (11 or higher).

## Build & Run

Run `./gradlew clean build` from the root of the project.
Once the project is built, run the server:
```
$ java -jar math-service-impl/build/libs/server.jar
Starting the server...
Server started!
```

In a separate shell session, run:
```
$ java -jar math-service-client/build/libs/client.jar
>
```

You can submit mathematical expressions via the client to the server, which will queue and compute them.
Currently, the supported operations are only `+`, `-`, `*`, `/` and the supported functions are `sin`, `cos`, `tan`, and `prime`:
```
> 1+2
3.0
> prime(10)
29.0
```

If another computation is currently in progress (all computations are done in one thread) or if computation takes too much time, the client will print `PENDING...` or `RUNNING...`:
```
> prime(20000)
PENDING...
PENDING...
RUNNING...
RUNNING...
224737.0
```

The client is also capable of subscribing to all server events via `events`:
```
$ java -jar math-service-client/build/libs/client.jar
> events
Listening to the server events. Type quit to terminate.
f0062b43-db95-4577-8793-f42a886b7d7e PENDING
f0062b43-db95-4577-8793-f42a886b7d7e RUNNING
f0062b43-db95-4577-8793-f42a886b7d7e COMPUTED 11.0
99255b04-b421-4a28-b573-156a911b8797 PENDING
99255b04-b421-4a28-b573-156a911b8797 RUNNING
99255b04-b421-4a28-b573-156a911b8797 COMPUTED 7919.0
a6153533-0b5c-42a9-8709-1736c92abed2 PENDING
a6153533-0b5c-42a9-8709-1736c92abed2 RUNNING
a6153533-0b5c-42a9-8709-1736c92abed2 ERROR Expected a positive integer number.
```

To emit events, run another instance of the client and submit an expression to the server.
