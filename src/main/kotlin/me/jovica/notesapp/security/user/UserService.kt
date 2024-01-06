package me.jovica.notesapp.security.user

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class UserService(private val userAccountRepository: UserAccountRepository) {

    fun createOrFindUser(displayName: String?, email: String?): UserAccount? {
        return null
    }

    fun findUserById(userId: UUID): UserAccount? {
        return null;
    }

    fun findUserByEmail(email: String?): UserAccount? {
        return null;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    fun addCredential(webAuthnCredential: WebAuthnCredential) {

        var webAuthnCredentialEntity = WebAuthnCredentialEntity();
        webAuthnCredentialEntity.id = webAuthnCredential.id
        webAuthnCredentialEntity.userId = webAuthnCredential.userId
        webAuthnCredentialEntity.type = webAuthnCredential.type
        webAuthnCredentialEntity.publicKey = webAuthnCredential.publicKey

        val userAccount = userAccountRepository.findById(webAuthnCredential.userId)
            .orElseThrow({ RuntimeException("Can not add webAuthnCredential to a user who does not exist") })

        userAccount.credentials.add(webAuthnCredentialEntity);
    }

    fun findCredentialById(credentialId: String?): WebAuthnCredential? {
        return null
    }

}
