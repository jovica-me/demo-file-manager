package me.jovica.notesapp.security.webauthn;

import org.springframework.data.jpa.repository.JpaRepository

interface WebAuthnCredentialEntityRepository: JpaRepository<WebAuthnCredentialEntity, String> {
}