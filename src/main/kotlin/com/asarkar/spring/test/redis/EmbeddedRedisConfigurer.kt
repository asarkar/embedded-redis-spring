package com.asarkar.spring.test.redis

import redis.embedded.RedisServerBuilder

/**
 * Class that gets called with a `RedisServerBuilder` giving the user a chance to configure the server before it's
 * started. Implementations must have a public no-argument constructor.
 *
 * @author Abhijit Sarkar
 * @since 1.0.0
 */
interface EmbeddedRedisConfigurer {
    fun configure(builder: RedisServerBuilder)
}
