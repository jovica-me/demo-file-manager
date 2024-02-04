package me.jovica.notesapp.domain.files;

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.util.*

interface FolderEntityRepository : JpaRepository<FolderEntity, UUID> {

    override fun findById(id: UUID): Optional<FolderEntity>

    fun findByFolderPermissionsEntities_UserEntity_IdAndFolderPermissionsEntities_ReadTrue(id: UUID): List<FolderEntity>


    override fun deleteById(id: UUID)


    @Query(
        """select f from FolderEntity f inner join f.folderPermissionsEntities folderPermissionsEntities
where folderPermissionsEntities.userEntity.id = ?1"""
    )
    fun findByFolderPermissionsEntities_UserEntity_Id(id: UUID): List<FolderEntity>
}