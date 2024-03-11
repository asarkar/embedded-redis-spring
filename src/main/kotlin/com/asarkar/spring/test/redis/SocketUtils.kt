/*
 * Copyright 2002-2020 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.asarkar.spring.test.redis

import org.springframework.util.Assert
import java.net.DatagramSocket
import java.net.InetAddress
import java.net.ServerSocket
import java.util.Random
import javax.net.ServerSocketFactory

/**
 * Simple utility methods for working with network sockets  for example,
 * for finding available ports on `localhost`.
 *
 * Within this class, a TCP port refers to a port for a [ServerSocket];
 * whereas, a UDP port refers to a port for a [DatagramSocket].
 *
 * org.springframework.util.SocketUtils was removed in Spring 6.
 * From Springboot 3.2.3 version, SocketUtils cannot be used.
 * org.springframework.test.util.TestSocketUtils can be used as an alternative, but embedded-redis-spring has to support older versions, so org.springframework.util.SocketUtils was copied.
 *
 * @author Sam Brannen
 * @author Ben Hale
 * @author Arjen Poutsma
 * @author Gunnar Hillert
 * @author Gary Russell
 * @author fennec-fox
 * @since 4.0
 */
object SocketUtils {
    /**
     * The default minimum value for port ranges used when finding an available
     * socket port.
     */
    private const val PORT_RANGE_MIN: Int = 1024

    /**
     * The default maximum value for port ranges used when finding an available
     * socket port.
     */
    const val PORT_RANGE_MAX: Int = 65535

    private val random = Random(System.nanoTime())

    /**
     * Find an available TCP port randomly selected from the range
     * [`minPort`, `maxPort`].
     * @param minPort the minimum port number
     * @param maxPort the maximum port number
     * @return an available TCP port number
     * @throws IllegalStateException if no available port could be found
     */
    /**
     * Find an available TCP port randomly selected from the range
     * [`minPort`, {@value #PORT_RANGE_MAX}].
     * @param minPort the minimum port number
     * @return an available TCP port number
     * @throws IllegalStateException if no available port could be found
     */
    /**
     * Find an available TCP port randomly selected from the range
     * [{@value #PORT_RANGE_MIN}, {@value #PORT_RANGE_MAX}].
     * @return an available TCP port number
     * @throws IllegalStateException if no available port could be found
     */
    @JvmOverloads
    fun findAvailableTcpPort(minPort: Int = PORT_RANGE_MIN, maxPort: Int = PORT_RANGE_MAX): Int {
        return SocketType.TCP.findAvailablePort(minPort, maxPort)
    }

    private enum class SocketType {
        TCP {
            override fun isPortAvailable(port: Int): Boolean {
                try {
                    val serverSocket = ServerSocketFactory.getDefault().createServerSocket(
                        port,
                        1,
                        InetAddress.getByName("localhost")
                    )
                    serverSocket.close()
                    return true
                } catch (ex: Exception) {
                    return false
                }
            }
        };

        /**
         * Determine if the specified port for this `SocketType` is
         * currently available on `localhost`.
         */
        protected abstract fun isPortAvailable(port: Int): Boolean

        /**
         * Find a pseudo-random port number within the range
         * [`minPort`, `maxPort`].
         * @param minPort the minimum port number
         * @param maxPort the maximum port number
         * @return a random port number within the specified range
         */
        private fun findRandomPort(minPort: Int, maxPort: Int): Int {
            val portRange = maxPort - minPort
            return minPort + random.nextInt(portRange + 1)
        }

        /**
         * Find an available port for this `SocketType`, randomly selected
         * from the range [`minPort`, `maxPort`].
         * @param minPort the minimum port number
         * @param maxPort the maximum port number
         * @return an available port number for this socket type
         * @throws IllegalStateException if no available port could be found
         */
        fun findAvailablePort(minPort: Int, maxPort: Int): Int {
            Assert.isTrue(minPort > 0, "'minPort' must be greater than 0")
            Assert.isTrue(maxPort >= minPort, "'maxPort' must be greater than or equal to 'minPort'")
            Assert.isTrue(maxPort <= PORT_RANGE_MAX, "'maxPort' must be less than or equal to $PORT_RANGE_MAX")

            val portRange = maxPort - minPort
            var candidatePort: Int
            var searchCounter = 0
            do {
                check(searchCounter <= portRange) {
                    String.format(
                        "Could not find an available %s port in the range [%d, %d] after %d attempts",
                        name,
                        minPort,
                        maxPort,
                        searchCounter
                    )
                }
                candidatePort = findRandomPort(minPort, maxPort)
                searchCounter++
            } while (!isPortAvailable(candidatePort))

            return candidatePort
        }
    }
}
