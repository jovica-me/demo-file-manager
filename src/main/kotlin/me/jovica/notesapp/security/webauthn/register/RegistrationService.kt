package me.jovica.notesapp.security.webauthn.register

import com.yubico.webauthn.FinishRegistrationOptions
import com.yubico.webauthn.RelyingParty
import com.yubico.webauthn.StartRegistrationOptions
import com.yubico.webauthn.data.*
import me.jovica.notesapp.security.user.UserAccountEntity
import me.jovica.notesapp.security.user.UserAccountRepository
import me.jovica.notesapp.security.user.UserService
import me.jovica.notesapp.security.webauthn.ByteArrayToUUID
import me.jovica.notesapp.security.webauthn.uuidtoByteArray
import org.springframework.stereotype.Service

@Service
class RegistrationService(
    val userService: UserService,
    val relyingParty: RelyingParty,
    val userAccountRepository: UserAccountRepository
) {
    fun start(request: RegistrationStartRequest): PublicKeyCredentialCreationOptions {
        val user = userService.createUser(request.username, request.fullName)

        val userIdentity =
            UserIdentity.builder()
                .name(user.username)
                .displayName(user.fullName)
                .id(uuidtoByteArray(user.id!!))
                .build()

        val authenticatorSelectionCriteria =
            AuthenticatorSelectionCriteria.builder()
                .userVerification(UserVerificationRequirement.DISCOURAGED)
                .build()

        val startRegistrationOptions =
            StartRegistrationOptions.builder()
                .user(userIdentity)
                .timeout(30000)
                .authenticatorSelection(authenticatorSelectionCriteria)
                .build()

        val options =
            relyingParty.startRegistration(startRegistrationOptions)

        return options
    }

    fun finish(
        options: PublicKeyCredentialCreationOptions,
        finishResponse: PublicKeyCredential<AuthenticatorAttestationResponse, ClientRegistrationExtensionOutputs>
    ): Boolean {

        val options = FinishRegistrationOptions.builder().request(options).response(finishResponse).build()
        val registrationResult = relyingParty.finishRegistration(options)

        val user = userAccountRepository.findById(ByteArrayToUUID(options.request.user.id))
            .orElseThrow { RuntimeException("Eroor") }

        userService.addCredential(registrationResult, user)
        return true
    }
}

data class RegistrationStartRequest(
    var fullName: String? = null,
    var username: String? = null
)
