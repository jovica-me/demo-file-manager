package me.jovica.notesapp.domain.files;

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.transaction.annotation.Transactional
import java.util.*

interface FileEntityRepository : JpaRepository<FileEntity, UUID> {


    @Transactional
    @Modifying
    @Query("update FileEntity f set f.name = ?1, f.text = ?2 where f.id = ?3")
    fun updateNameAndTextById(name: String, text: String, id: UUID): Int


    fun findByHasAccess_Id(id: UUID): List<FileEntity>
}