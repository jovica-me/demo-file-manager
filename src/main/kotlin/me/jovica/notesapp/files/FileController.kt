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
        val data = fileService.getFile(noteUUID) ?: return "redirect:/";
        val (file,isOwner) = data;
        model.addAttribute("file",file)
        model.addAttribute("isOwner",isOwner)
        model.addAttribute("fileUUID",file.id)
        return "pages/files/note"
    }

    @GetMapping("/new")
    fun redirectNewFile(): String {
        val file = fileService.newFile();
        return "redirect:/files/file/" + file.id;
    }

    @HxRequest
    @PostMapping("/save")
    fun saveFile( noteUUID: UUID, noteText: String, noteName: String): String {
        val x = fileService.updateTextAndTitle(noteUUID, noteText,noteName)
        if (x == 0) {
            return "fragments/files/notes/failedtosave"
        }
        return "fragments/files/notes/saved"
    }

    @HxRequest
    @PostMapping("/autosave")
    fun autosave( noteUUID: UUID, noteText: String, noteName: String): String {
        val x = fileService.updateTextAndTitle(noteUUID, noteText,noteName)
        if (x == 0) {
            return "fragments/files/notes/failedtoautosave"
        }
        return "fragments/files/notes/autosave"
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

    @HxRequest
    @PostMapping("/message/{fileUUID}")
    fun postMessage(@PathVariable fileUUID: UUID,message:String,model: Model): String {
        val messages = fileService.sendMessage(fileUUID,message);
        model.addAttribute("messages",messages)
        model.addAttribute("fileUUID",fileUUID)

        return "fragments/files/notes/messages"
    }

    @HxRequest
    @GetMapping("/message/{fileUUID}")
    fun getMessages(@PathVariable fileUUID: UUID,model: Model): String {
        val messages = fileService.getMessages(fileUUID);
        model.addAttribute("messages",messages)
        model.addAttribute("fileUUID",fileUUID)

        return "fragments/files/notes/messages"
    }





}