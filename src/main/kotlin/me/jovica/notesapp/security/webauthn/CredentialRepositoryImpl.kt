package me.jovica.notesapp.security.webauthn

import com.yubico.webauthn.CredentialRepository
import com.yubico.webauthn.RegisteredCredential
import com.yubico.webauthn.data.ByteArray
import com.yubico.webauthn.data.PublicKeyCredentialDescriptor
import com.yubico.webauthn.data.PublicKeyCredentialType
import com.yubico.webauthn.data.exception.Base64UrlException
import me.jovica.notesapp.security.user.UserService
import org.springframework.stereotype.Repository
import java.nio.ByteBuffer
import java.util.*
import java.util.stream.Collectors
import kotlin.jvm.optionals.toCollection

@Repository
class CredentialRepositoryImpl(val userService: UserService) : CredentialRepository {
    override fun getCredentialIdsForUsername(username: String?): MutableSet<PublicKeyCredentialDescriptor> {
        return userService
            .findUserByUsername(username!!)
            .map { user ->
                user.credentials.stream()
                    .map { toPublicKeyCredentialDescriptor(it) }
                    .collect(Collectors.toSet())
            }.orElse(mutableSetOf())
    }

    override fun getUserHandleForUsername(username: String?): Optional<ByteArray> {
        return userService.findUserByUsername(username!!).map { user -> user.id?.let { uuidtoByteArray(it) } }
    }

    override fun getUsernameForUserHandle(userHandler: ByteArray): Optional<String> {
        if (userHandler.isEmpty) {
            return Optional.empty()
        }
        return userService
            .findUserById(ByteArrayToUUID(userHandler))
            .map { userAccount -> userAccount.username }

    }

    override fun lookup(credentialId: ByteArray, userHandle: ByteArray): Optional<RegisteredCredential>? {


        return userService
            .findUserById(ByteArrayToUUID(userHandle))
            .map { user -> user.credentials }
            .orElse(mutableSetOf())
            .stream()
            .filter { cred ->
                try {
                    return@filter credentialId == cred.id?.let { ByteArray.fromBase64Url(it) }
                } catch (e: Base64UrlException) {
                    throw RuntimeException(e)
                }
            }
            .findFirst()
            .map { toRegisteredCredential(it) }


    }

    override fun lookupAll(credentialId: ByteArray): MutableSet<RegisteredCredential> {
        return userService.findCredentialById(credentialId.base64Url).map { toRegisteredCredential(it) }.toCollection(
            mutableSetOf()
        )

    }

    private fun toRegisteredCredential(fidoCredential: WebAuthnCredentialEntity): RegisteredCredential {
        try {
            return RegisteredCredential.builder()
                .credentialId(fidoCredential.id?.let { ByteArray.fromBase64Url(it) })
                .userHandle(fidoCredential.userId?.let { uuidtoByteArray(it) })
                .publicKeyCose(fidoCredential.publicKey?.let { ByteArray.fromBase64Url(it) })
                .build()
        } catch (e: Base64UrlException) {
            throw java.lang.RuntimeException(e)
        }
    }

}

fun uuidtoByteArray(uuid: UUID): ByteArray {
    val buffer = ByteBuffer.wrap(ByteArray(16))
    buffer.putLong(uuid.mostSignificantBits)
    buffer.putLong(uuid.leastSignificantBits)
    return ByteArray(buffer.array())
}

fun ByteArrayToUUID(byteArray: ByteArray): UUID {
    val byteBuffer = ByteBuffer.wrap(byteArray.bytes)
    val high = byteBuffer.getLong()
    val low = byteBuffer.getLong()
    return UUID(high, low)
}

fun toRegisteredCredential(fidoCredential: WebAuthnCredential): RegisteredCredential {
    try {
        return RegisteredCredential.builder()
            .credentialId(ByteArray.fromBase64Url(fidoCredential.id))
            .userHandle(uuidtoByteArray(fidoCredential.userId))
            .publicKeyCose(ByteArray.fromBase64Url(fidoCredential.publicKey))
            .build()
    } catch (e: Base64UrlException) {
        throw java.lang.RuntimeException(e)
    }
}

fun toPublicKeyCredentialDescriptor(
    cred: WebAuthnCredentialEntity
): PublicKeyCredentialDescriptor {
    val descriptor: PublicKeyCredentialDescriptor? = null
    try {
        return PublicKeyCredentialDescriptor.builder()
            .id(cred.id?.let { ByteArray.fromBase64Url(it) })
            .type(PublicKeyCredentialType.valueOf(cred.type!!))
            .build()
    } catch (e: Base64UrlException) {
        throw java.lang.RuntimeException(e)
    }
}