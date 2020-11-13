# embedded-redis-spring

Starts a Redis server and makes the port available as Spring Boot environment property. Stops the server when the
Spring context is destroyed.

Requires Java 8 or later. Uses [embedded-redis](https://github.com/ozimov/embedded-redis) and [Spring Boot](https://spring.io/projects/spring-boot). 

## Installation

You can find the latest version on Bintray. [ ![Download](https://api.bintray.com/packages/asarkar/mvn/com.asarkar.spring%3Aembedded-redis-spring/images/download.svg) ](https://bintray.com/asarkar/mvn/com.asarkar.spring%3Aembedded-redis-spring/_latestVersion)

It is also on Maven Central and jcenter.

## Usage

The only thing you need is the `AutoConfigureEmbeddedRedis` annotation:

```
@SpringBootTest
@AutoConfigureEmbeddedRedis
public class AutoConfigureWithKnownPortsTest {
    @Test
    public void testConnection() {
        RedisClient redisClient = RedisClient
            .create("redis://localhost:6379/");
        ...
    }
}
```
To use random port:
```
@SpringBootTest
@AutoConfigureEmbeddedRedis(port = 0)
public class AutoConfigureWithRandomPortsTest {
    @Value("${embedded-redis.port:-1}")
    private int port;

    @Test
    public void testConnection() {
        RedisClient redisClient = RedisClient
            .create(String.format("redis://localhost:%d/", port));
        ...
    }
}
```

If you want to configure the `RedisServer` before it's started, provide an implementation for
[EmbeddedRedisConfigurer](src/main/kotlin/com/asarkar/spring/test/redis/EmbeddedRedisConfigurer.kt), and set the 
class name in the annotation element `serverConfigurerClass`. If a Spring bean of this type exists, it'll be used; 
otherwise, a new instance will be created using the public no-argument constructor.

See KDoc for more details.

## Contribute

This project is a volunteer effort. You are welcome to send pull requests, ask questions, or create issues.
If you like it, you can help by spreading the word!

## License

Copyright 2020 Abhijit Sarkar - Released under [Apache License v2.0](LICENSE).
