package me.jovica.notesapp.files

import me.jovica.notesapp.domain.UserEntity
import me.jovica.notesapp.domain.UserEntityRepository
import me.jovica.notesapp.domain.files.FileEntity
import me.jovica.notesapp.domain.files.FileEntityRepository
import me.jovica.notesapp.domain.files.FolderEntity
import me.jovica.notesapp.domain.files.FolderEntityRepository
import me.jovica.notesapp.security.user.UserAccountRepository
import me.jovica.notesapp.security.webauthn.WebAuthnAuthentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import java.util.*

@Service
class FileService(
    private val userAccountRepository: UserAccountRepository,
    private val fileEntityRepository: FileEntityRepository,
    private val folderEntityRepository: FolderEntityRepository, private val userEntityRepository: UserEntityRepository
) {

    fun newFile(): FileEntity {
        val auth = SecurityContextHolder.getContext().authentication as WebAuthnAuthentication

        val user = userEntityRepository.findById(auth.id)
            .orElseThrow { throw IllegalStateException("Nemoguce uraditi, korisnik ne postoji") }

        return newFile(user.topFolder!!, user)
    }

    fun newFile(folderUUID: UUID) {
        val auth = SecurityContextHolder.getContext().authentication as WebAuthnAuthentication
        val user = userEntityRepository.findById(auth.id)
            .orElseThrow { throw IllegalStateException("Nemoguce uraditi, korisnik ne postoji") }
        val folder = folderEntityRepository.findById(folderUUID)
            .orElseThrow { throw IllegalStateException("Nemoguce uraditi, korisnik ne postoji") }

        newFile(folder, user)
    }

    fun newFile(folderFolderEntity: FolderEntity, user: UserEntity): FileEntity {
        val file = FileEntity();
        file.folderEntity = folderFolderEntity
        file.name = "New File"
        file.owner = user
        return fileEntityRepository.saveAndFlush(file);
    }

    fun getFile(noteId: UUID): FileEntity {
        return fileEntityRepository.findById(noteId).orElseThrow { throw IllegalStateException("File dose not exist") }
    }

    fun updateTextAndTitle(noteUUID: UUID, noteText: String, noteName: String): Int {
        return fileEntityRepository.updateNameAndTextById(noteName, noteText, noteUUID)
    }

    fun deleteFile(fileUUID: UUID) {
        val file =
            fileEntityRepository.findById(fileUUID).orElseThrow { throw IllegalStateException("File dose not exist") }
        val hasAccesCopy = ArrayList(file.hasAccess)
        for (acc in hasAccesCopy) {
            acc.accessFiles.remove(file)
        }
        file.folderEntity?.fileEntities?.remove(file);
        file.owner?.files?.remove(file);
        fileEntityRepository.delete(file)
    }

    fun hasAccses(fileUUID: UUID): Pair<FileEntity, WebAuthnAuthentication> {
        val auth = SecurityContextHolder.getContext().authentication as WebAuthnAuthentication
        val folder =
            fileEntityRepository.findById(fileUUID).orElseThrow { throw IllegalArgumentException("Invalid folder") }
        if (folder.owner?.userAccount?.username != auth.username) {
            throw IllegalStateException("User can not change promotions if he is not the owner")
        }
        return Pair(folder, auth)
    }

    fun addPermission(fileUUID: UUID, username: String): FileEntity {
        val (file, auth) = hasAccses(fileUUID);
        val user = userEntityRepository.findByUserAccount_Username(username)
            .orElseThrow { throw IllegalStateException("User doses not existe") }
        file.hasAccess.add(user)
        user.accessFiles.add(file)
        return fileEntityRepository.saveAndFlush(file)
    }

    fun removePermission(fileUUID: UUID, userUUID: UUID): FileEntity {
        val (file, auth) = hasAccses(fileUUID);
        val user =
            userEntityRepository.findById(userUUID).orElseThrow { throw IllegalStateException("User doses not existe") }
        file.hasAccess.remove(user)
        user.accessFiles.remove(file)
        return fileEntityRepository.save(file)
    }


}