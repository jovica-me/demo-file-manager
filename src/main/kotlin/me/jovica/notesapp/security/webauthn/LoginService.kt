package me.jovica.notesapp.security.webauthn

import com.yubico.webauthn.*

import com.yubico.webauthn.data.*
import org.springframework.stereotype.Service

@Service
class LoginService(val relyingParty: RelyingParty) {
    fun start(request: LogInStartRequest): AssertionRequest {
        val o = StartAssertionOptions.builder()
            .timeout(60000)
            .userVerification(UserVerificationRequirement.PREFERRED);
        if(!request.username.isNullOrBlank()) {
            o.username(request.username)
        }
        val assertionRequest = relyingParty.startAssertion(o.build())


        return assertionRequest;
    }

    fun finish(
        options: AssertionRequest,
        request: PublicKeyCredential<AuthenticatorAssertionResponse, ClientAssertionExtensionOutputs>
    ): AssertionResult {

        val option = FinishAssertionOptions.builder()
            .request(options)
            .response(request)
            .build()

        val finish = relyingParty.finishAssertion(option)

        return finish
    }
}

data class LogInStartRequest (
    var username:String?
)
