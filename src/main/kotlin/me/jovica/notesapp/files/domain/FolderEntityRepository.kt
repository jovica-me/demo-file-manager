package me.jovica.notesapp.domain.files;

import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface FolderEntityRepository : JpaRepository<FolderEntity, UUID> {

    override fun findById(id: UUID): Optional<FolderEntity>

    fun findByFolderPermissionsEntities_UserEntity_IdAndFolderPermissionsEntities_ReadTrue(id: UUID): List<FolderEntity>


    override fun deleteById(id: UUID)
}