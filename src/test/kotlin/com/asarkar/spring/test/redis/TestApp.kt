package com.asarkar.spring.test.redis

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
open class TestApp

fun main(args: Array<String>) {
    runApplication<TestApp>(*args)
}
