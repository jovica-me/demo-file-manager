package me.jovica.notesapp.controller

import com.yubico.webauthn.AssertionRequest
import com.yubico.webauthn.data.*
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpSession
import me.jovica.notesapp.security.webauthn.LogInStartRequest
import me.jovica.notesapp.security.webauthn.LoginService
import me.jovica.notesapp.security.webauthn.RegistrationService
import me.jovica.notesapp.security.webauthn.RegistrationStartRequest
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping("/api/webauthn")
class ApiWebAuthnController(var registrationService: RegistrationService, var loginService: LoginService) {

    @PostMapping("/register/start")
    fun registerStart(
        @RequestBody request: RegistrationStartRequest,
        session: HttpSession
    ): PublicKeyCredentialCreationOptions {
        val response = registrationService.start(request)
        session.setAttribute(START_REG_REQUEST, response)

        return response
    }

    @PostMapping("/register/finish")
    fun registerFinish(
        @RequestBody request: PublicKeyCredential<AuthenticatorAttestationResponse, ClientRegistrationExtensionOutputs>,
        session: HttpSession
    ): Boolean {
        val options = session.getAttribute(START_REG_REQUEST) as PublicKeyCredentialCreationOptions
        if (options == null) {
            throw RuntimeException("No start found")
        }

        return registrationService.finish(options, request)
    }


    @PostMapping("/login/start")
    fun loginStart(
        @RequestBody request: LogInStartRequest, session: HttpSession
    ): PublicKeyCredentialRequestOptions {
        val response = loginService.start(request)
        session.setAttribute(START_LOGIN_REQUEST, response)
        return response.publicKeyCredentialRequestOptions
    }

    @PostMapping("/login/finish")
    fun loginFinish(
        @RequestBody request: PublicKeyCredential<AuthenticatorAssertionResponse, ClientAssertionExtensionOutputs>,
        session: HttpSession
    ): Boolean {
        val options = session.getAttribute(START_LOGIN_REQUEST) as AssertionRequest
        if (options == null) {
            throw RuntimeException("No start found")
        }

        val finish = loginService.finish(options, request)

        return finish.isSuccess
    }

    @GetMapping("/login/test")
    fun testRole(request:HttpServletRequest): String {
        val auth = SecurityContextHolder.getContext().authentication
        var s = ""
        auth.authorities.forEach { s+=it }
        return s
    }
}

var START_REG_REQUEST = "register_options"

var START_LOGIN_REQUEST = "login_options"

