package me.jovica.notesapp.files.domain;

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.util.*

interface FolderPermissionsEntityRepository : JpaRepository<FolderPermissionsEntity, UUID> {


    fun findFirstByUserEntity_IdAndFolderEntity_IdAndRead(id: UUID, id1: UUID, read: Boolean): FolderPermissionsEntity


    @Query(
        """select f from FolderPermissionsEntity f
where f.folderEntity.id = ?1 and f.userEntity.id = ?2"""
    )
    fun findByFolderEntity_IdAndUserEntity_Id(id: UUID, id1: UUID): Optional<FolderPermissionsEntity>
}