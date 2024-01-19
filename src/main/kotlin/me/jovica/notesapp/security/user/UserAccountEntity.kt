package me.jovica.notesapp.security.user

import jakarta.persistence.*
import me.jovica.notesapp.security.webauthn.WebAuthnCredentialEntity
import java.util.*

@Entity
@Table(name = "user_account")
open class UserAccountEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    open var id: UUID? = null


    @Column(name = "full_name")
    open var fullName: String? = null


    @Column(name = "username")
    open var username: String = ""

    @OneToMany(fetch = FetchType.EAGER, cascade = [CascadeType.ALL])
    @JoinColumn(name = "user_id")
    open var credentials: MutableSet<WebAuthnCredentialEntity> = mutableSetOf()

    @OneToMany(targetEntity = AuthoritiesEntity::class, cascade = [CascadeType.ALL], fetch = FetchType.EAGER)
    open var authorities: MutableList<AuthoritiesEntity> = mutableListOf()

}