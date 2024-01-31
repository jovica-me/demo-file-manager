package me.jovica.notesapp.files

import me.jovica.notesapp.domain.files.FileEntity
import me.jovica.notesapp.domain.files.FileEntityRepository
import me.jovica.notesapp.domain.files.FolderEntity
import me.jovica.notesapp.domain.files.FolderEntityRepository
import me.jovica.notesapp.security.user.UserAccountEntity
import me.jovica.notesapp.security.user.UserAccountRepository
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import java.util.*

@Service
class FileService(
    private val userAccountRepository: UserAccountRepository,
    private val fileEntityRepository: FileEntityRepository,
    private val folderEntityRepository: FolderEntityRepository
) {

    fun newFile(userName: String): FileEntity {

        val user = userAccountRepository.findByUsername(userName) ?: throw UsernameNotFoundException("User not found")

        val folder = folderEntityRepository.findById(user.id!!).orElseGet {
            val x = FolderEntity()
            x.id = user.id
            x.name = "New Folder"
            x.owner = user.userEntity;
            folderEntityRepository.saveAndFlush(x)
        }

        return newFile(folder, user)
    }

    fun newFile(folderFolderEntity: FolderEntity, user: UserAccountEntity): FileEntity {
        val file = FileEntity();
        file.folderEntity = folderFolderEntity
        file.name = "New File"
        file.owner = user.userEntity
        return fileEntityRepository.saveAndFlush(file);
    }

    fun getFile(noteId: UUID): FileEntity {
        return fileEntityRepository.findById(noteId).orElseThrow { throw IllegalStateException("File dose not exist") }
    }

    fun updateTextAndTitle(noteUUID: UUID, noteText: String, noteName: String): Int {
        return fileEntityRepository.updateNameAndTextById(noteName, noteText, noteUUID)
    }
}