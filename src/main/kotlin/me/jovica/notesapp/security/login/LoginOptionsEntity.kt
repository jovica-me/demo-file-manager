package me.jovica.notesapp.security.login

import jakarta.persistence.*
import java.util.*

@Entity
@Table(name = "login_options_entity")
open class LoginOptionsEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    open var id: UUID? = null
}