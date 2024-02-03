package me.jovica.notesapp.controller

import me.jovica.notesapp.domain.files.FolderEntityRepository
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping

@Controller
class PagesController(private val folderEntityRepository: FolderEntityRepository) {
    @GetMapping("/")
    fun home ():String {
        return "pages/index"
    }

    @GetMapping("/test")
    fun test(): String {
//        folderEntityRepository.findById("")
        return "usersFolder"
    }
}