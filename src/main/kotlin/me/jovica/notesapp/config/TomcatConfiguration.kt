package me.jovica.notesapp.config

import org.apache.catalina.connector.Connector
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory
import org.springframework.boot.web.servlet.server.ServletWebServerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

//@Configuration
//class TomcatConfiguration {
//    @Value("\${http.port}")
//    private val httpPort = 0
//
//    @Value("\${server.port}")
//    private val serverPort = 8443
//
//    private fun createStandardConnector(): Connector {
//        val connector = Connector("org.apache.coyote.http11.Http11NioProtocol")
//        connector.port = httpPort
//        connector.redirectPort = serverPort
//        connector.secure = false
//        return connector
//    }
//
//    @Bean
//    fun servletContainer(): ServletWebServerFactory {
//        val tomcat = TomcatServletWebServerFactory()
//        tomcat.addAdditionalTomcatConnectors(createStandardConnector())
//        return tomcat
//    }
//
//}