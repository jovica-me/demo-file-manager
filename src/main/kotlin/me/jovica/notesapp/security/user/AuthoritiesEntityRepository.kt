package me.jovica.notesapp.security.user;

import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface AuthoritiesEntityRepository : JpaRepository<AuthoritiesEntity, UUID> {


    fun findByAuthority(authority: String): Optional<AuthoritiesEntity>
}