package me.jovica.notesapp.security.webauthn

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.yubico.webauthn.AssertionRequest
import com.yubico.webauthn.AssertionResult
import com.yubico.webauthn.data.AuthenticatorAssertionResponse
import com.yubico.webauthn.data.ClientAssertionExtensionOutputs
import com.yubico.webauthn.data.PublicKeyCredential
import com.yubico.webauthn.exception.AssertionFailedException
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import me.jovica.notesapp.controller.START_LOGIN_REQUEST
import me.jovica.notesapp.security.user.UserAccountRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.web.authentication.AuthenticationConverter
import org.springframework.security.web.authentication.AuthenticationSuccessHandler
import org.springframework.stereotype.Service
import java.io.IOException


@Service
class WebAuthnAuthenticationManager(
    val loginService: LoginService,
    val userAccountRepository: UserAccountRepository
) : AuthenticationManager {
    var logger: Logger = LoggerFactory.getLogger(WebAuthnAuthenticationManager::class.java)
    override fun authenticate(authentication: Authentication): Authentication {

        val token = authentication as WebAuthnAuthenticationRequestToken

        val assertionResult = loginService.finish(token.startRequest, token.finishRequest)
        try {
            if (assertionResult.isSuccess) {

                val user = userAccountRepository.findByUsername(assertionResult.username)
                val auto: MutableSet<out GrantedAuthority> =
                    user?.let { it.authorities.map { SimpleGrantedAuthority(it.authority) }.toMutableSet() }
                        ?: mutableSetOf()

                val result = WebAuthnAuthentication(token, assertionResult, auto)
                return result
            }
            throw BadCredentialsException("WebAuthn failed")
        } catch (e: AssertionFailedException) {
            throw BadCredentialsException("unable to login", e)
        }
    }
}

class WebAuthnAuthenticationConverter : AuthenticationConverter {
    override fun convert(request: HttpServletRequest): Authentication {
        val option = request.session.getAttribute(START_LOGIN_REQUEST) as AssertionRequest
        val finishRequest: PublicKeyCredential<AuthenticatorAssertionResponse, ClientAssertionExtensionOutputs>

        try {
            val jsonBody = request.reader.use { it.readText() }
            finishRequest = jacksonObjectMapper().readValue(jsonBody)
            return WebAuthnAuthenticationRequestToken(option.username.toString(), option, finishRequest)
        } catch (e: IOException) {
            throw RuntimeException(e)
        }
    }
}


class WebAuthnAuthenticationRequestToken(
    val username: String,
    val startRequest: AssertionRequest,
    val finishRequest: PublicKeyCredential<AuthenticatorAssertionResponse, ClientAssertionExtensionOutputs>
) : AbstractAuthenticationToken(null) {


    override fun getCredentials(): Any {
        return finishRequest
    }

    override fun getPrincipal(): Any {
        return username
    }

}

class WebAuthnAuthentication(
    val token: WebAuthnAuthenticationRequestToken,
    val assertionResult: AssertionResult,
    val authorities: MutableSet<out GrantedAuthority>,
    val username: String = token.username
) : AbstractAuthenticationToken(authorities) {

    init {
        isAuthenticated = true
    }

    override fun getCredentials(): Any {
        return token
    }

    override fun getPrincipal(): Any {
        return username
    }

}

class FidoLoginSuccessHandler : AuthenticationSuccessHandler {
    @Throws(IOException::class, ServletException::class)
    override fun onAuthenticationSuccess(
        request: HttpServletRequest,
        response: HttpServletResponse,
        chain: FilterChain,
        authentication: Authentication
    ) {
        this.onAuthenticationSuccess(request, response, authentication)
    }

    @Throws(IOException::class, ServletException::class)
    override fun onAuthenticationSuccess(
        request: HttpServletRequest, response: HttpServletResponse, authentication: Authentication
    ) {
        response.contentType = "application/json"
        response.status = 200
        response.writer.println("""{"good":"good"}""")
    }
}

