package me.jovica.notesapp.security.register

import com.yubico.webauthn.RelyingParty
import me.jovica.notesapp.security.user.UserAccount
import org.springframework.stereotype.Service

@Service
class RegistrationService(
    val relyingParty: RelyingParty
) {
    fun start() {
    }

    fun finish() {

    }
}