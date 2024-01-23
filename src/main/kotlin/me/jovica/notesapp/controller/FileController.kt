package me.jovica.notesapp.controller

import io.github.wimdeblauwe.htmx.spring.boot.mvc.HxRequest
import me.jovica.notesapp.security.webauthn.WebAuthnAuthentication
import me.jovica.notesapp.services.NoteService
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import java.util.*


@Controller
@RequestMapping("/note")
class FileController(
    val noteService: NoteService
) {

    @GetMapping("/{id}")
    fun pageFile(@PathVariable("id") noteUUID: UUID, model: Model): String {
        val file = noteService.getFile(noteUUID);
        model.addAttribute("noteUUID", file.id)
        model.addAttribute("noteName", file.name)
        model.addAttribute("noteText", file.text)
        model.addAttribute("noteFolderUUID", file.folderEntity?.id)

        return "pages/note"
    }

    @GetMapping("/new")
    fun redirectNewFile(): String {
        val auth = SecurityContextHolder.getContext().authentication as WebAuthnAuthentication
        val file = noteService.newFile(auth.username);
        return "redirect:/note/" + file.id;
    }

    @HxRequest
    @PostMapping("/{id}")
    fun saveFile(@PathVariable("id") noteUUID: UUID, noteText: String, noteName: String): String {
        val x = noteService.updateTextAndTitle(noteUUID, noteText,noteName)
        if (x == 0) {
            return "fragments/notes/failedtosave"
        }
        return "fragments/notes/saved"
    }


    fun move() {
        TODO("Not yet implemented")

        // is he the owner
        // is he the owner of the destination

    }

    fun delete() {
        TODO("Not yet implemented")


    }

}