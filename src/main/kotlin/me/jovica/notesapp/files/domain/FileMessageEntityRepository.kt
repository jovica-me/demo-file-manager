package me.jovica.notesapp.files.domain;

import me.jovica.notesapp.domain.files.FileEntity
import org.springframework.data.domain.Sort
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.util.*

interface FileMessageEntityRepository : JpaRepository<FileMessageEntity, UUID> {


    @Query("select f from FileMessageEntity f where f.fileEntity.id = ?1 order by f.timestamp")
    fun findByFileEntity_IdOrderByTimestampAsc(id: UUID, sort: Sort): List<FileMessageEntity>


    @Query("select f from FileMessageEntity f where f.fileEntity = ?1 order by f.timestamp asc limit 50")
    fun findByFileEntityOrderByTimestampAsc(fileEntity: FileEntity): List<FileMessageEntity>
}