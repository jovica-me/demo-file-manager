package me.jovica.notesapp.rest

import com.yubico.webauthn.data.AuthenticatorAttestationResponse
import com.yubico.webauthn.data.ClientRegistrationExtensionOutputs
import com.yubico.webauthn.data.PublicKeyCredential
import com.yubico.webauthn.data.PublicKeyCredentialCreationOptions
import jakarta.servlet.http.HttpSession
import me.jovica.notesapp.security.webauthn.register.RegistrationService
import me.jovica.notesapp.security.webauthn.register.RegistrationStartRequest
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/webauthn/register")
class ApiWebAuthnController(var registrationService: RegistrationService) {
    var START_REG_REQUEST = "login_options"


    @PostMapping("/start")
    fun loginStart(
        @RequestBody request: RegistrationStartRequest,
        session: HttpSession
    ): PublicKeyCredentialCreationOptions {
        val response = registrationService.start(request)
        session.setAttribute(START_REG_REQUEST, response)

        return response
    }

    @PostMapping("/finish")
    fun loginFinish(
        @RequestBody request: PublicKeyCredential<AuthenticatorAttestationResponse, ClientRegistrationExtensionOutputs>,
        session: HttpSession
    ): Boolean {
        val options = session.getAttribute(START_REG_REQUEST) as PublicKeyCredentialCreationOptions
        if(options == null) {
            throw RuntimeException("No start found")
        }

        return registrationService.finish(options,request)
    }
}



