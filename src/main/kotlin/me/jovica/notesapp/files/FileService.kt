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

        val user = userEntityRepository.findById(auth.id).orElseThrow{ throw IllegalStateException("Nemoguce uraditi, korisnik ne postoji")}

        return newFile(user.topFolder!!, user)
    }
    fun newFile(folderUUID: UUID) {
        val auth = SecurityContextHolder.getContext().authentication as WebAuthnAuthentication
        val user = userEntityRepository.findById(auth.id).orElseThrow{ throw IllegalStateException("Nemoguce uraditi, korisnik ne postoji")}
        val folder = folderEntityRepository.findById(folderUUID).orElseThrow{ throw IllegalStateException("Nemoguce uraditi, korisnik ne postoji")}

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
        val file =  fileEntityRepository.findById(fileUUID).orElseThrow{ throw IllegalStateException("File dose not exist") }
        val owner = file.owner?: throw IllegalStateException("File dose not exist")
        fileEntityRepository.delete(file)

    }




}