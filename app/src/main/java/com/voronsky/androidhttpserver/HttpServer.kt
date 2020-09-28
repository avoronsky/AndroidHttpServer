package com.voronsky.androidhttpserver

import io.ktor.application.*
import io.ktor.features.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import org.slf4j.LoggerFactory
import java.io.File
import java.security.KeyStore

class HttpServer(private val sslCredentials: SslCredentials) {

    private val logger = LoggerFactory.getLogger(HttpServer::class.java.simpleName)
    private val server = createServer()

    fun start() {
        server.start()
    }

    fun stop() {
        server.stop(0, 0)
    }

    private fun createServer(): NettyApplicationEngine {
        return GlobalScope.embeddedServer(Netty) {
            // Logs all the requests performed
            install(CallLogging)

            routing {
                get(PATH_GREETING) {
                    call.respond("{\"greeting\":\"Hello World!\"}")
                }
            }
        }
    }

    private fun <TEngine : ApplicationEngine, TConfiguration : ApplicationEngine.Configuration>
            CoroutineScope.embeddedServer(
        factory: ApplicationEngineFactory<TEngine, TConfiguration>,
        module: Application.() -> Unit
    ): TEngine {
        val environment = applicationEngineEnvironment {
            this.parentCoroutineContext = coroutineContext + parentCoroutineContext
            this.log = logger
            this.module(module)

            connector {
                this.port = HTTP_PORT
            }

            sslConnector(
                sslCredentials.getKeyStore(),
                sslCredentials.getKeyAlias(),
                { sslCredentials.getKeyPassword().toCharArray() },
                { sslCredentials.getAliasPassword().toCharArray() }
            ) {
                this.port = HTTPS_PORT
                this.keyStorePath = sslCredentials.getKeyStoreFile()
            }
        }

        return embeddedServer(factory, environment)
    }

    companion object {
        private const val PATH_GREETING = "greeting"
        const val HTTP_PORT = 8080
        const val HTTPS_PORT = 8081
    }
}

interface SslCredentials {

    fun getKeyStoreFile(): File

    fun getKeyStore(): KeyStore

    fun getKeyAlias(): String

    fun getKeyPassword(): String

    fun getAliasPassword(): String
}
