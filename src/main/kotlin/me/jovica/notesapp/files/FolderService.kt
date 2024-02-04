package me.jovica.notesapp.files

import me.jovica.notesapp.domain.UserEntityRepository
import me.jovica.notesapp.domain.files.FileEntityRepository
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
class FolderService(
    private val folderEntityRepository: FolderEntityRepository,
    private val folderPermissionsEntityRepository: FolderPermissionsEntityRepository,
    private val userEntityRepository: UserEntityRepository, private val fileEntityRepository: FileEntityRepository
) {
    private fun checkForPermission(folderUUID: UUID): Pair<FolderEntity, WebAuthnAuthentication> {
        val auth = SecurityContextHolder.getContext().authentication as WebAuthnAuthentication
        val folder =
            folderEntityRepository.findById(folderUUID).orElseThrow { throw IllegalArgumentException("Invalid folder") }
        if (folder.owner?.userAccount?.username != auth.username) {
            throw IllegalStateException("User can not change promotions if he is not the owner")
        }
        return Pair(folder, auth)
    }

    fun hasAccsesToFolder(folderUUID: UUID): Triple<FolderEntity, Boolean, Boolean> {
        val folder = folderEntityRepository.findById(folderUUID)
            .orElseThrow { throw IllegalStateException("File dose not exist") }
        val auth = SecurityContextHolder.getContext().authentication as WebAuthnAuthentication

        if (folder.owner?.userAccount?.username == auth.username) {
            return Triple(folder, true, true)
        }
        val x = folderPermissionsEntityRepository.findByFolderEntity_IdAndUserEntity_Id(folder.id!!, auth.id)
        if (x.isPresent) {
            return Triple(folder, true, false)
        }

        return Triple(folder, false, false)
    }

    @Transactional(propagation = Propagation.REQUIRED)
    fun createFolder(parentUUID: UUID, title: String) {
        val auth = SecurityContextHolder.getContext().authentication as WebAuthnAuthentication

        val folder = folderEntityRepository.findById(parentUUID)
            .orElseThrow { throw IllegalStateException("File dose not exist") }

        if (folder.owner?.userAccount?.username != auth.username) {
            return;
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
        return folderEntityRepository.findByFolderPermissionsEntities_UserEntity_Id(auth.id)
    }


    @Transactional(propagation = Propagation.REQUIRED)
    fun deleteFolder(folderUUID: UUID) {
        val auth = SecurityContextHolder.getContext().authentication as WebAuthnAuthentication
        var folder =
            folderEntityRepository.findById(folderUUID).orElseThrow { throw IllegalArgumentException("Invalid folder") }
        if (folder.owner?.userAccount?.username != auth.username) {
            throw IllegalStateException("User can not change promotions if he is not the owner")
        }
        deleteFolderRecursively(folder)
        folder.parentFolder?.childrenFolders?.remove(folder)
        folderEntityRepository.delete(folder);
    }

    private fun deleteFolderRecursively(folder: FolderEntity) {
        val subFoldersCopy = ArrayList(folder.childrenFolders)
        for (subFolder in subFoldersCopy) {
            deleteFolderRecursively(subFolder)
        }
        
        folder.parentFolder?.childrenFolders?.remove(folder)

        val filesCopy = ArrayList(folder.fileEntities)
        for(file in filesCopy) {
            file.folderEntity?.fileEntities?.remove(file);
            fileEntityRepository.delete(file)
        }

        folderEntityRepository.delete(folder)
    }

    fun addPermission(folderUUID: UUID, username: String) {
        val (folder, auth) = checkForPermission(folderUUID)
        val userToShare = userEntityRepository.findByUserAccount_Username(username)
            .orElseThrow { throw IllegalArgumentException("Invalid username") }
        if (auth.id == userToShare.id) {
            throw IllegalStateException("Can not be both owner and edited user")
        }
        val userAlreadyHasPermission = userToShare.folderPermissionsEntities.find {
            it.userEntity?.id == userToShare.id
        }
        if (userAlreadyHasPermission != null) {
            throw IllegalStateException("User has permission")
        }
        val permission = FolderPermissionsEntity();
        permission.folderEntity = folder
        permission.userEntity = userToShare
        folderPermissionsEntityRepository.save(permission)
    }


    fun deletePermission(permissionUUID: UUID) {
        val permission = folderPermissionsEntityRepository.findById(permissionUUID)
            .orElseThrow { throw IllegalArgumentException("Invalid Permission request") }
        val (folder, auth) = checkForPermission(permission.folderEntity?.id!!)

        folderPermissionsEntityRepository.delete(permission)
    }

//    fun star(folderUUID: UUID) {
//        val auth = SecurityContextHolder.getContext().authentication as WebAuthnAuthentication
//        val folder =
//            folderEntityRepository.findById(folderUUID).orElseThrow { throw IllegalArgumentException("Invalid folder") }
//        val user = userEntityRepository.findById(auth.id).orElseThrow {
//            throw IllegalStateException("A BUG")
//        }
//
//        folder.staredUsers.add(user)
//        folderEntityRepository.save(folder)
//
//    }
}

