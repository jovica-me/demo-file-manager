package me.jovica.notesapp.security.webauthn

import java.io.Serializable
import java.util.*

/**
 * DTO for {@link me.jovica.notesapp.security.webauthn.WebAuthnCredentialEntity}
 */
data class WebAuthnCredential(
    val id: String ,
    val userId: UUID,
    val type: String,
    val publicKey: String
)