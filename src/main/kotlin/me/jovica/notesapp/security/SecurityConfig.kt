package me.jovica.notesapp.security

import me.jovica.notesapp.security.webauthn.FidoLoginSuccessHandler
import me.jovica.notesapp.security.webauthn.WebAuthnAuthenticationConverter
import me.jovica.notesapp.security.webauthn.WebAuthnAuthenticationManager
import org.springframework.boot.autoconfigure.security.servlet.PathRequest
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.invoke
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.AuthenticationFilter
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.security.web.context.HttpSessionSecurityContextRepository
import org.springframework.security.web.util.matcher.AntPathRequestMatcher
import org.springframework.security.web.util.matcher.AnyRequestMatcher


@Configuration
@EnableWebSecurity
class SecurityConfig {
    @Bean
    fun filterPagesChain(http: HttpSecurity, webAuthnAuthenticationManager: WebAuthnAuthenticationManager): SecurityFilterChain {

        val authenticationFilter =
            AuthenticationFilter(webAuthnAuthenticationManager, WebAuthnAuthenticationConverter())
        authenticationFilter.requestMatcher = AntPathRequestMatcher("/api/webauthn/login/finish")
        authenticationFilter.successHandler = FidoLoginSuccessHandler()
        authenticationFilter.setSecurityContextRepository(HttpSessionSecurityContextRepository())
        http.addFilterBefore(authenticationFilter, UsernamePasswordAuthenticationFilter::class.java)


        http {
            authorizeRequests {
                authorize("/", permitAll)
                authorize("/login", permitAll)
                authorize("/register", permitAll)
                authorize("/logout",permitAll)
                authorize("/ui/update-nav", permitAll)
                authorize("/api/webauthn/login/start", permitAll)
                authorize("/api/webauthn/login/finish", permitAll)
                authorize("/api/webauthn/register/start", permitAll)
                authorize("/api/webauthn/register/finish", permitAll)
                authorize(PathRequest.toStaticResources().atCommonLocations(), permitAll)
                authorize(anyRequest, authenticated)
            }
            requiresChannel {
                secure(AnyRequestMatcher.INSTANCE, "REQUIRES_SECURE_CHANNEL")
            }
            formLogin {
                loginPage = "/login"
            }
        }
        return http.build()
    }
}

