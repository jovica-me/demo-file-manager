package me.jovica.notesapp.files

import me.jovica.notesapp.security.user.UserAccountRepository
import me.jovica.notesapp.security.webauthn.WebAuthnAuthentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import java.util.*

@Controller
@RequestMapping("/files")
class FolderController(private val userAccountRepository: UserAccountRepository) {

    @GetMapping("")
    fun redirectToFolder(model: Model): String {
        val auth = SecurityContextHolder.getContext().authentication as WebAuthnAuthentication

        val user = userAccountRepository.findByUsername(auth.username)
        if(user === null) {
            return "redirect:/";
        }

        return "redirect:/files/"+ user.id
    }
    @GetMapping("/")
    fun rediToFolder(model: Model): String {
        val auth = SecurityContextHolder.getContext().authentication as WebAuthnAuthentication

        val user = userAccountRepository.findByUsername(auth.username)
        if(user === null) {
            return "redirect:/";
        }

        return "redirect:/files/"+ user.id
    }
    @GetMapping("/{id}")
    fun pageFolder(@PathVariable("id") noteUUID: UUID, model: Model): String {

        model.addAttribute("folderName", "Hello")
        return "pages/files/folder"
    }

}