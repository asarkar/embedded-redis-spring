package com.asarkar.spring.test.redis

import redis.embedded.RedisServerBuilder

/**
 * Class that gets called with a `RedisServerBuilder` giving the user a chance to configure the server before it's
 * started. If a Spring bean of this type exists, it'll be used; otherwise, a new instance will be created using
 * the public no-argument constructor.
 *
 * @author Abhijit Sarkar
 * @since 1.0.0
 */
interface EmbeddedRedisConfigurer {
    fun configure(builder: RedisServerBuilder)
}
