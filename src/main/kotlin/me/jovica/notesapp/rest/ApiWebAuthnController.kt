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
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/api/webauthn/")
class ApiWebAuthnController(var registrationService: RegistrationService) {
    var START_REG_REQUEST = "login_options"


    @PutMapping("register")
    fun loginStart(
        @RequestBody request: RegistrationStartRequest,
        session: HttpSession
    ): PublicKeyCredentialCreationOptions {
        val response = registrationService.start(request)
        session.setAttribute(START_REG_REQUEST, response)

        return response
    }

    @GetMapping("register")
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



