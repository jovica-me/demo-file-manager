package me.jovica.notesapp.security.user

import jakarta.persistence.*
import java.util.*

@Entity
@Table(name = "authorities_entity")
open class AuthoritiesEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    open var id: UUID? = null

    @Column(name = "authority")
    open var authority: String? = null
}