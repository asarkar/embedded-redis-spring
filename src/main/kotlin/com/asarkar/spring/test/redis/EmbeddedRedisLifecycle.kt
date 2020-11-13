package com.asarkar.spring.test.redis

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.SmartLifecycle
import org.springframework.util.ReflectionUtils
import redis.embedded.RedisServer

open class EmbeddedRedisLifecycle : SmartLifecycle {
    private val log = LoggerFactory.getLogger(EmbeddedRedisLifecycle::class.java)

    @Value("\${embedded-redis.server-configurer-class:''}")
    lateinit var serverConfigurerClass: String

    @Value("\${embedded-redis.port:-1}")
    var port: Int = -1

    @Autowired(required = false)
    private var serverConfigurerBean: EmbeddedRedisConfigurer? = null

    private lateinit var redisServer: RedisServer

    override fun start() {
        if (isRunning || port <= 0) return

        val builder = RedisServer.builder()
            .port(port)
        serverConfigurer()?.configure(builder)
        redisServer = builder.build().apply { start() }
        log.info("Started Redis server on port: {}", port)
    }

    @Suppress("UNCHECKED_CAST")
    private fun serverConfigurer(): EmbeddedRedisConfigurer? {
        return if (serverConfigurerClass.isNotEmpty()) {
            val clazz = Class.forName(serverConfigurerClass)
            if (serverConfigurerBean != null) {
                return serverConfigurerBean
            }
            return try {
                ReflectionUtils.accessibleConstructor(clazz as Class<EmbeddedRedisConfigurer>)
                    .newInstance()
            } catch (ex: ReflectiveOperationException) {
                throw IllegalArgumentException("$serverConfigurerClass must have a public no-argument constructor", ex)
            } catch (ex: ClassCastException) {
                throw IllegalArgumentException("$serverConfigurerClass is not an EmbeddedRedisConfigurer", ex)
            }
        } else null
    }

    override fun stop() {
        if (isRunning) redisServer.stop()
    }

    override fun isRunning() = this::redisServer.isInitialized && redisServer.isActive
}
