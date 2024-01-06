package me.jovica.notesapp.security.user

import java.io.Serializable
import java.util.*

/**
 * DTO for {@link me.jovica.notesapp.security.user.UserAccountEntity}
 */
data class UserAccount(val id: UUID, val fullName: String, val email: String, val credentials:Set<WebAuthnCredential>)