package me.jovica.notesapp.security.user

import me.jovica.notesapp.security.webauthn.WebAuthnCredential
import java.util.*

/**
 * DTO for {@link me.jovica.notesapp.security.user.UserAccountEntity}
 */
data class UserAccount(val id: UUID, val fullName: String, val username: String, val credentials:Set<WebAuthnCredential>)