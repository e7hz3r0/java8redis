java8redis
==========

An asynchronous Redis client written for Java 8. It is built on top of Netty and utilizes Java 8's lambdas
to provide the asynchronous callbacks.

This currently just supports GET, SET, and DEL commands, but should be extensible with little effort.

This project served as a way to learn Java 8's lambdas, Redis, Netty, and JMockit.

Building
==========

Clone this repo, then:

    mvn compile

To create a jar:

    mvn package

Testing
==========

There are unit and integration tests. To run the unit tests:

    mvn test

To run unit and integration tests:

    mvn verify


Usage
==========

    import com.e7hz3r0.j8redis.J8Redis;
    ...
    J8Redis redis = new J8Redis("127.0.0.1");
    redis.connect({channel -> {
        ...
    });
    ...
    redis.set(myKey, myValue, (response, error) -> {
        //error is an Exception if an error occurred otherwise null
        //response should be "OK"
    });
    ...
    // NOTE: as written, this block may be called BEFORE the above set is complete
    redis.get(myKey, (response, error) -> {
        //response should be the value of myValue that was set above
    });
    ...
    // NOTE: as written, this block may be called BEFORE the above set or get is complete
    redis.del(myKey, (response, error) -> {
        ...
    });
    ...
    redis.disconnect();
