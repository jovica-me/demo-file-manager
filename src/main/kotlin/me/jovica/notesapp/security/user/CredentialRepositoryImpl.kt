package me.jovica.notesapp.security.user

import com.yubico.webauthn.CredentialRepository
import com.yubico.webauthn.RegisteredCredential
import com.yubico.webauthn.data.ByteArray
import com.yubico.webauthn.data.PublicKeyCredentialDescriptor
import com.yubico.webauthn.data.PublicKeyCredentialType
import com.yubico.webauthn.data.exception.Base64UrlException
import java.nio.ByteBuffer
import java.util.*
import java.util.stream.Collectors

class CredentialRepositoryImpl(val userService: UserService) : CredentialRepository {
    override fun getCredentialIdsForUsername(p0: String?): MutableSet<PublicKeyCredentialDescriptor> {
        val user = userService.findUserByEmail(p0) ?: return mutableSetOf()
        return user.credentials.stream()
            .map { toPublicKeyCredentialDescriptor(it) }
            .collect(Collectors.toSet())
    }

    override fun getUserHandleForUsername(p0: String?): Optional<ByteArray> {
        val user = userService.findUserByEmail(p0) ?: return Optional.empty()
        return Optional.of(UUIDtoByteArray(user.id));
    }

    override fun getUsernameForUserHandle(p0: ByteArray): Optional<String> {
        if (p0.isEmpty) {
            return Optional.empty()
        }
        val user = userService.findUserById(ByteArrayToUUID(p0)) ?: return Optional.empty()
        return Optional.of(user.email)
    }

    override fun lookup(credentialId: ByteArray, userId: ByteArray): Optional<RegisteredCredential>? {
        return userService.findUserById(ByteArrayToUUID(userId))?.credentials?.stream()
            ?.filter {
                credentialId == ByteArray.fromBase64Url(it.id)
            }?.findFirst()?.map {
                toRegisteredCredential(it)
            }
    }

    override fun lookupAll(p0: ByteArray): MutableSet<RegisteredCredential> {
        val x = userService.findCredentialById(p0.base64Url)?.let { toRegisteredCredential(it) }?.let{ mutableSetOf(it) }
        if (x == null) {
            return mutableSetOf()
        }
        return x;
    }

}

fun UUIDtoByteArray(uuid: UUID): ByteArray {
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
            .userHandle(UUIDtoByteArray(fidoCredential.userId))
            .publicKeyCose(ByteArray.fromBase64Url(fidoCredential.publicKey))
            .build()
    } catch (e: Base64UrlException) {
        throw java.lang.RuntimeException(e)
    }
}

fun toPublicKeyCredentialDescriptor(
    cred: WebAuthnCredential
): PublicKeyCredentialDescriptor {
    val descriptor: PublicKeyCredentialDescriptor? = null
    try {
        return PublicKeyCredentialDescriptor.builder()
            .id(ByteArray.fromBase64Url(cred.id))
            .type(PublicKeyCredentialType.valueOf(cred.type))
            .build()
    } catch (e: Base64UrlException) {
        throw java.lang.RuntimeException(e)
    }
}