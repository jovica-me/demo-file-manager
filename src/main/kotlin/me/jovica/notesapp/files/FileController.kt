package me.jovica.notesapp.files

import io.github.wimdeblauwe.htmx.spring.boot.mvc.HxRequest
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*
import java.util.*


@Controller
@RequestMapping("/files/file")
class FileController(
    val fileService: FileService
) {
    @GetMapping("/{id}")
    fun pageFile(@PathVariable("id") noteUUID: UUID, model: Model): String {
        val file = fileService.getFile(noteUUID);
        file.text
        model.addAttribute("file",file)
        model.addAttribute("isOwner",true)
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

    @HxRequest
    @PostMapping("/addpermission")
    fun postAddPermission(fileUUID: UUID,username: String,model: Model): String {
        val file = fileService.addPermission(fileUUID,username);
        model.addAttribute("file",file)

        return "fragments/files/notes/control"
    }

    @HxRequest
    @DeleteMapping("/removepermision")
    fun postAddPermission(fileUUID: UUID,userUUID: UUID,model: Model): String {
        val file = fileService.removePermission(fileUUID,userUUID);
        model.addAttribute("file",file)

        return "fragments/files/notes/control"
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