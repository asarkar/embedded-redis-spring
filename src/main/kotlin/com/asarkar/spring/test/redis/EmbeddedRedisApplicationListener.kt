package com.asarkar.spring.test.redis

import org.springframework.boot.context.event.ApplicationPreparedEvent
import org.springframework.context.ApplicationListener
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.core.env.MapPropertySource
import org.springframework.util.SocketUtils

@Order(Ordered.LOWEST_PRECEDENCE)
open class EmbeddedRedisApplicationListener : ApplicationListener<ApplicationPreparedEvent> {
    private val prefix = "embedded-redis"

    override fun onApplicationEvent(event: ApplicationPreparedEvent) {
        val env = event.applicationContext.environment
        var port = env.getProperty("$prefix.port", Int::class.java) ?: return

        if (port <= 0) {
            port = SocketUtils.findAvailableTcpPort()
        }

        with(env.propertySources) {
            remove(prefix)
            addFirst(MapPropertySource(prefix, mapOf("$prefix.port" to port)))
        }
    }
}
