package me.jovica.notesapp.security.user

import com.yubico.webauthn.RegistrationResult
import me.jovica.notesapp.security.webauthn.WebAuthnCredential
import me.jovica.notesapp.security.webauthn.WebAuthnCredentialEntity
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class UserService(private val userAccountRepository: UserAccountRepository) {

    fun createUser(username: String?, fullName: String?): UserAccountEntity {
        if (fullName.isNullOrBlank())
            throw IllegalArgumentException(" fullName is null or blank")
        if (username.isNullOrBlank())
            throw IllegalArgumentException(" username is null or blank")

        val userAccountEntity = userAccountRepository.findByUsername(username)

        if (userAccountEntity.isPresent) {
            throw IllegalArgumentException("user with username existed")
        }

        val result = UserAccountEntity()
        result.username = username
        result.fullName = fullName
        userAccountRepository.save(result)

        return result
    }

    @Transactional(propagation = Propagation.REQUIRED)
    fun addCredential(registrationResult: RegistrationResult, user: UserAccountEntity) {

        val userAccount = userAccountRepository.findById(user.id!!)
            .orElseThrow({ RuntimeException("Can not add webAuthnCredential to a user who does not exist") })

        val webAuthnCredentialEntity = WebAuthnCredentialEntity()
        webAuthnCredentialEntity.id = registrationResult.keyId.id.base64Url
        webAuthnCredentialEntity.userId = user.id
        webAuthnCredentialEntity.publicKey = registrationResult.publicKeyCose.base64Url
        webAuthnCredentialEntity.type = registrationResult.keyId.type.name

        userAccount.credentials.add(webAuthnCredentialEntity)

    }

    fun findUserById(userId: UUID): Optional<UserAccountEntity> {
        return userAccountRepository.findById(userId)
    }

    fun findUserByUsername(username: String): Optional<UserAccountEntity> {
        return userAccountRepository.findByUsername(username)
    }


    fun findCredentialById(credentialId: String?): Optional<WebAuthnCredentialEntity> {
        return Optional.empty()
    }
}

