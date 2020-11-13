package com.asarkar.spring.test.redis

import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Tags
import org.springframework.boot.test.autoconfigure.properties.PropertyMapping
import java.lang.annotation.Inherited

/**
 * Annotation for test classes that want to start a Redis server as part of the Spring application Context.
 *
 * @property serverConfigurerClass [EmbeddedRedisConfigurer] implementation class. Defaults to empty string.
 * @property port Redis server port. Defaults to 6379. Set 0 to use a random port.
 *
 * @author Abhijit Sarkar
 * @since 1.0.0
 */
@MustBeDocumented
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
@Tags(
    Tag("spring"),
    Tag("spring-boot"),
    Tag("redis"),
    Tag("embedded-redis"),
    Tag("test"),
    Tag("integration-test")
)
@Inherited
@PropertyMapping("embedded-redis")
annotation class AutoConfigureEmbeddedRedis(
    val serverConfigurerClass: String = "",
    val port: Int = 6379
)
