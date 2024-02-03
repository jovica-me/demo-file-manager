package me.jovica.notesapp.files

import io.github.wimdeblauwe.htmx.spring.boot.mvc.HxRequest
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import java.util.*


@Controller
@RequestMapping("/files/file")
class FileController(
    val fileService: FileService
) {
    @GetMapping("/{id}")
    fun pageFile(@PathVariable("id") noteUUID: UUID, model: Model): String {
        val file = fileService.getFile(noteUUID);
        model.addAttribute("noteUUID", file.id)
        model.addAttribute("noteName", file.name)
        model.addAttribute("noteText", file.text)
        model.addAttribute("noteFolderUUID", file.folderEntity?.id)
        return "pages/files/note"
    }

    @GetMapping("/new")
    fun redirectNewFile(): String {
        val file = fileService.newFile();
        return "redirect:/files/file/" + file.id;
    }

    @HxRequest
    @PostMapping("/{id}")
    fun saveFile(@PathVariable("id") noteUUID: UUID, noteText: String, noteName: String): String {
        val x = fileService.updateTextAndTitle(noteUUID, noteText,noteName)
        if (x == 0) {
            return "fragments/files/notes/failedtosave"
        }
        return "fragments/files/notes/saved"
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