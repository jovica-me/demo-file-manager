package me.jovica.notesapp.files

import me.jovica.notesapp.domain.files.FolderEntity
import me.jovica.notesapp.domain.files.FolderEntityRepository
import me.jovica.notesapp.security.webauthn.WebAuthnAuthentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class FolderService(private val folderEntityRepository: FolderEntityRepository) {
    fun hasAccsesToFolder(folder: FolderEntity): Boolean {
        val auth = SecurityContextHolder.getContext().authentication as WebAuthnAuthentication

        if(folder.owner?.userAccount?.username == auth.username) {
            return true
        }
        // TODO ADD CHECK FOR SHARED FOLDERS
        return false
    }

    @Transactional(propagation = Propagation.REQUIRED)
    fun createFolder(parentUUID: UUID, title: String) {
        val auth = SecurityContextHolder.getContext().authentication as WebAuthnAuthentication

        val folder = folderEntityRepository.findById(parentUUID)
            .orElseThrow { throw IllegalStateException("File dose not exist") }

        if( folder.owner?.userAccount?.username != auth.username) {
            return ;
        }

        val newFolder = FolderEntity()
        newFolder.owner = folder.owner
        newFolder.name = title

        folder.childrenFolders.add(newFolder)

        folderEntityRepository.save(folder)


    }

//    fun getAllShredWithMe(): MutableList<FolderEntity> {
//
//        val auth = SecurityContextHolder.getContext().authentication as WebAuthnAuthentication
//
//    }
}