package me.jovica.notesapp.domain;

import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface UserEntityRepository : JpaRepository<UserEntity, UUID> {


    fun findByUserAccount_Username(username: String): Optional<UserEntity>
}