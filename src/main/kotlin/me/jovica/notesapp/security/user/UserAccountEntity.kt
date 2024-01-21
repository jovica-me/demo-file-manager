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


    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "user_account_authoritiesEntities",
        joinColumns = [JoinColumn(name = "userAccountEntity_id")],
        inverseJoinColumns = [JoinColumn(name = "authoritiesEntities_id")]
    )
    open var authorities: MutableSet<AuthoritiesEntity> = mutableSetOf()
}