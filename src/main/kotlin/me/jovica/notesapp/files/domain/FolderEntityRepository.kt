package me.jovica.notesapp.domain.files;

import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface FolderEntityRepository : JpaRepository<FolderEntity, UUID> {

    override fun findById(id: UUID): Optional<FolderEntity>
}