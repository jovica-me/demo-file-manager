package me.jovica.notesapp.security.user

import jakarta.persistence.*
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


    @Column(name = "email")
    open var email: String? = null

    @OneToMany(fetch = FetchType.EAGER, cascade = [CascadeType.ALL])
    @JoinColumn(name = "user_id")
    open var credentials: MutableSet<WebAuthnCredentialEntity> = mutableSetOf()


//  @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
//  @JoinColumn(name = "user_id")
//  private Set<FidoCredentialEntity> credentials = Set.of();


}