package me.jovica.notesapp.security.webauthn

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.hibernate.proxy.HibernateProxy
import java.util.*

@Entity
@Table(name = "web_authn_credential_entity")
open class WebAuthnCredentialEntity {
    @Id
    @Column(name = "id", nullable = false)
    open var id: String? = null

    @Column(name = "user_id")
    open var userId: UUID? = null

    @Column(name = "type")
    open var type: String? = null

    @Column(name = "public_key")
    open var publicKey: String? = null




    final override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null) return false
        val oEffectiveClass =
            if (other is HibernateProxy) other.hibernateLazyInitializer.persistentClass else other.javaClass
        val thisEffectiveClass =
            if (this is HibernateProxy) this.hibernateLazyInitializer.persistentClass else this.javaClass
        if (thisEffectiveClass != oEffectiveClass) return false
        other as WebAuthnCredentialEntity

        return id != null && id == other.id
    }

    final override fun hashCode(): Int =
        if (this is HibernateProxy) this.hibernateLazyInitializer.persistentClass.hashCode() else javaClass.hashCode()
}