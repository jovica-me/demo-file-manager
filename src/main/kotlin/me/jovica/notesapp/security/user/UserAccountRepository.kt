package me.jovica.notesapp.security.user

import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface UserAccountRepository : JpaRepository<UserAccountEntity, UUID> {
    fun findByUsername(username: String): UserAccountEntity?
}