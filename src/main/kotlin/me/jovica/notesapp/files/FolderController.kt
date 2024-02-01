package me.jovica.notesapp.files

import io.github.wimdeblauwe.htmx.spring.boot.mvc.HxRefresh
import io.github.wimdeblauwe.htmx.spring.boot.mvc.HxRequest
import me.jovica.notesapp.domain.files.FolderEntityRepository
import me.jovica.notesapp.security.user.UserAccountRepository
import me.jovica.notesapp.security.webauthn.WebAuthnAuthentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import java.util.*

@Controller
@RequestMapping("/files")
class FolderController(
    private val userAccountRepository: UserAccountRepository,
    private val folderEntityRepository: FolderEntityRepository,
    private val folderService: FolderService
) {

    @GetMapping("")
    fun redirectToFolder(model: Model): String {
        val auth = SecurityContextHolder.getContext().authentication as WebAuthnAuthentication

        val user = userAccountRepository.findByUsername(auth.username)
        if (user === null) {
            return "redirect:/";
        }

        return "redirect:/files/" + user.userEntity?.topFolder?.id
    }

    @GetMapping("/")
    fun rediToFolder(model: Model): String {
        val auth = SecurityContextHolder.getContext().authentication as WebAuthnAuthentication

        val user = userAccountRepository.findByUsername(auth.username)
        if (user === null) {
            return "redirect:/";
        }

        return "redirect:/files/" + user.userEntity?.topFolder?.id
    }

    @GetMapping("/refreshFolders")
    fun refreshFolders() {

    }


    @GetMapping("/{id}")
    fun pageFolder(@PathVariable("id") folderUUID: UUID, model: Model): String {
        val folder = folderEntityRepository.findById(folderUUID)
            .orElseThrow { throw IllegalStateException("File dose not exist") }

        if(!folderService.hasAccsesToFolder(folder)){
            return "redirect:/files/"
        }

        model.addAttribute("foldersToShow", folder.childrenFolders)
        model.addAttribute("title", folder.name)
        model.addAttribute("currentFolder", folder)
        return "pages/files/folder"
    }

//    @GetMapping("/shared-with-me")
//    fun pageSharedWithMe( model: Model): String {
//        val list: MutableList<FolderEntity> =  folderService.getAllShredWithMe();
//
//        model.addAttribute("foldersToShow", list)
//        model.addAttribute("title", "Shared with me")
//       model.addAttribute("currentFolder", folder)
//        return "pages/files/folder"
//    }

    @HxRequest
    @HxRefresh
    @PostMapping("/new")
    fun newFolder(parentUUID: UUID, title: String):String {
        // TODO CHKECK IF HE CAN DO IT

        folderService.createFolder(parentUUID, title);


        return "pages/files/folder"
    }

}