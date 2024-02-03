package me.jovica.notesapp.files

import me.jovica.notesapp.domain.UserEntityRepository
import me.jovica.notesapp.domain.files.FolderEntity
import me.jovica.notesapp.domain.files.FolderEntityRepository
import me.jovica.notesapp.files.domain.FolderPermissionsEntity
import me.jovica.notesapp.files.domain.FolderPermissionsEntityRepository
import me.jovica.notesapp.security.webauthn.WebAuthnAuthentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class FolderService(private val folderEntityRepository: FolderEntityRepository,
                    private val folderPermissionsEntityRepository: FolderPermissionsEntityRepository, private val userEntityRepository: UserEntityRepository
) {
    fun hasAccsesToFolder(folder: FolderEntity): Pair<Boolean,Boolean>  {
        val auth = SecurityContextHolder.getContext().authentication as WebAuthnAuthentication

        if(folder.owner?.userAccount?.username == auth.username) {
            return Pair(true,true)
        }
        val x = folderPermissionsEntityRepository.findByFolderEntity_IdAndReadTrueAndUserEntity_Id(folder.id!!, auth.id)
        if(x.isPresent) {
            return Pair(true,false)
        }

        return Pair(false,false)
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
        newFolder.parentFolder = folder

        folder.childrenFolders.add(newFolder)

        folderEntityRepository.save(newFolder)
        folderEntityRepository.save(folder)
    }
    fun getAllShredWithMe(): List<FolderEntity> {
        val auth = SecurityContextHolder.getContext().authentication as WebAuthnAuthentication
        return folderEntityRepository.findByFolderPermissionsEntities_UserEntity_IdAndFolderPermissionsEntities_ReadTrue(auth.id)
    }

    fun shareFolderWithUser(folderUUID:UUID,username:String, write:Boolean = false) {
        val auth = SecurityContextHolder.getContext().authentication as WebAuthnAuthentication
        val folder = folderEntityRepository.findById(folderUUID).orElseThrow { throw IllegalArgumentException("Invalid folder") }
        val userToShare = userEntityRepository.findByUserAccount_Username(username).orElseThrow { throw IllegalArgumentException("Invalid username") }
        if(folder.owner?.userAccount?.username != auth.username) {
            throw  IllegalStateException("User can not change promotions if he is not the owner")
        }

        val permission = FolderPermissionsEntity();
        permission.folderEntity = folder
        permission.userEntity = userToShare
        folderPermissionsEntityRepository.save(permission)
    }

    fun deleteFolder(folderUUID: UUID) {
        val auth = SecurityContextHolder.getContext().authentication as WebAuthnAuthentication
        val folder = folderEntityRepository.findById(folderUUID).orElseThrow { throw IllegalArgumentException("Invalid folder") }
        if(folder.owner?.userAccount?.username != auth.username) {
            throw  IllegalStateException("User can not change promotions if he is not the owner")
        }

//        val parent = folder.parentFolder?: throw IllegalArgumentException("Invalid folder")
//        parent.childrenFolders.remove(folder);
//        folderEntityRepository.save(parent)
//        folder.parentFolder = null;
//        folderEntityRepository.saveAndFlush(folder)
        folderEntityRepository.delete(folder);

    }
}

