package me.jovica.notesapp.files

import me.jovica.notesapp.domain.UserEntity
import me.jovica.notesapp.domain.UserEntityRepository
import me.jovica.notesapp.domain.files.FileEntity
import me.jovica.notesapp.domain.files.FileEntityRepository
import me.jovica.notesapp.domain.files.FolderEntity
import me.jovica.notesapp.domain.files.FolderEntityRepository
import me.jovica.notesapp.files.domain.FileMessageEntity
import me.jovica.notesapp.files.domain.FileMessageEntityRepository
import me.jovica.notesapp.security.user.UserAccountRepository
import me.jovica.notesapp.security.webauthn.WebAuthnAuthentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.*

@Service
class FileService(
    private val userAccountRepository: UserAccountRepository,
    private val fileEntityRepository: FileEntityRepository,
    private val folderEntityRepository: FolderEntityRepository, private val userEntityRepository: UserEntityRepository,
    private val fileMessageEntityRepository: FileMessageEntityRepository
) {
    fun hasAccses(fileUUID: UUID): Pair<FileEntity, WebAuthnAuthentication> {
        val auth = SecurityContextHolder.getContext().authentication as WebAuthnAuthentication
        val folder =
            fileEntityRepository.findById(fileUUID).orElseThrow { throw IllegalArgumentException("Invalid folder") }
        if (folder.owner?.userAccount?.username != auth.username) {
            throw IllegalStateException("User can not change promotions if he is not the owner")
        }
        return Pair(folder, auth)
    }

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

    fun getFile(fileUUID: UUID): Pair<FileEntity, Boolean>? {
        val auth = SecurityContextHolder.getContext().authentication as WebAuthnAuthentication
        val file =
            fileEntityRepository.findById(fileUUID).orElseThrow { throw IllegalArgumentException("Invalid folder") }

        if (file.owner?.userAccount?.username == auth.username) {
            return Pair(file, true)
        }
        val user = userEntityRepository.findById(auth.id).orElseThrow { throw IllegalArgumentException("Iligal ID") }
        if (file.hasAccess.contains(user)) {
            return Pair(file, false)
        }

        return null;
    }

    fun updateTextAndTitle(fileUUID: UUID, noteText: String, noteName: String): Int {
        val auth = SecurityContextHolder.getContext().authentication as WebAuthnAuthentication
        val file =
            fileEntityRepository.findById(fileUUID).orElseThrow { throw IllegalArgumentException("Invalid folder") }
        val user = userEntityRepository.findById(auth.id).orElseThrow { throw IllegalArgumentException("Iligal ID") }

        if (file.owner?.userAccount?.username != auth.username && !file.hasAccess.contains(user)) {
            throw IllegalStateException("Accses deny")
        }

        return fileEntityRepository.updateNameAndTextById(noteName, noteText, fileUUID)
    }

    fun deleteFile(fileUUID: UUID) {
        var (file, auth) = hasAccses(fileUUID);
        val hasAccesCopy = ArrayList(file.hasAccess)
        for (acc in hasAccesCopy) {
            acc.accessFiles.remove(file)
        }
        file.folderEntity?.fileEntities?.remove(file);
        file.owner?.files?.remove(file);
        fileEntityRepository.delete(file)
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


    fun sendMessage(fileUUID: UUID, message: String): List<FileMessageEntity> {
        val auth = SecurityContextHolder.getContext().authentication as WebAuthnAuthentication
        val file =
            fileEntityRepository.findById(fileUUID).orElseThrow { throw IllegalArgumentException("Invalid folder") }
        val user = userEntityRepository.findById(auth.id).orElseThrow { throw IllegalArgumentException("Iligal ID") }

        if (file.owner?.userAccount?.username != auth.username && !file.hasAccess.contains(user)) {
            throw IllegalStateException("Accses deny")
        }

        val msg = FileMessageEntity()
        msg.fileEntity = file
        msg.userEntity = user
        msg.timestamp = LocalDateTime.now()
        msg.text = message
        fileMessageEntityRepository.saveAndFlush(msg)

        return fileMessageEntityRepository.findByFileEntityOrderByTimestampAsc(file)
    }

    fun getMessages(fileUUID: UUID): List<FileMessageEntity> {
        val auth = SecurityContextHolder.getContext().authentication as WebAuthnAuthentication
        val file =
            fileEntityRepository.findById(fileUUID).orElseThrow { throw IllegalArgumentException("Invalid folder") }
        val user = userEntityRepository.findById(auth.id).orElseThrow { throw IllegalArgumentException("Iligal ID") }

        if (file.owner?.userAccount?.username != auth.username && !file.hasAccess.contains(user)) {
            throw IllegalStateException("Accses deny")
        }

        return fileMessageEntityRepository.findByFileEntityOrderByTimestampAsc(file)
    }
}