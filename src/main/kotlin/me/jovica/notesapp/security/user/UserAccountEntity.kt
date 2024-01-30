package me.jovica.notesapp.security.user

import jakarta.persistence.*
import me.jovica.notesapp.domain.UserEntity
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

    @OneToOne(mappedBy = "userAccount", cascade = [CascadeType.ALL], optional = false)
    @PrimaryKeyJoinColumn
    open var userEntity: UserEntity? = null


    @OneToMany(fetch = FetchType.EAGER, cascade = [CascadeType.ALL])
    @JoinColumn(name = "user_id")
    open var credentials: MutableSet<WebAuthnCredentialEntity> = mutableSetOf()

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "user_account_authorities_entities",
        joinColumns = [JoinColumn(name = "user_account_entity_id")],
        inverseJoinColumns = [JoinColumn(name = "authorities_entities_id")]
    )
    open var authorities: MutableSet<AuthoritiesEntity> = mutableSetOf()
}