package me.jovica.notesapp.files

import io.github.wimdeblauwe.htmx.spring.boot.mvc.HxRefresh
import io.github.wimdeblauwe.htmx.spring.boot.mvc.HxRequest
import me.jovica.notesapp.domain.files.FolderEntityRepository
import me.jovica.notesapp.security.user.UserAccountRepository
import me.jovica.notesapp.security.webauthn.WebAuthnAuthentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*
import java.util.*

@Controller
@RequestMapping("/files")
class FolderController(
    private val userAccountRepository: UserAccountRepository,
    private val folderEntityRepository: FolderEntityRepository,
    private val folderService: FolderService,
    private val fileService: FileService
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


    @GetMapping("/{id}")
    fun pageFolder(@PathVariable("id") folderUUID: UUID, model: Model): String {

        val (folder,hasAccess, isOwner) = folderService.hasAccsesToFolder(folderUUID)

        if (!hasAccess) {
            return "redirect:/files/"
        }

        model.addAttribute("title", folder.name)

        model.addAttribute("foldersToShow", folder.childrenFolders)
        model.addAttribute("filesToShow", folder.fileEntities)

        if (isOwner) {
            model.addAttribute("isOwner", isOwner)
            model.addAttribute("folderUUID", folder.id)
            model.addAttribute("folderPermisions", folder.folderPermissionsEntities)
        }

        return "pages/files/usersFolder"
    }


//    @HxRequest
//    @HxRefresh
//    @PostMapping("/new")
//    fun postStar(folderUUID: UUID): String {
//        folderService.star(folderUUID)
//        return "fragments/ok"
//    }

    // New folder -> contentChanged
    @HxRequest
    @HxRefresh
    @PostMapping("/new")
    fun postNewFolder(parentUUID: UUID, title: String): String {
        folderService.createFolder(parentUUID, title);
        return "fragments/ok"
    }

    // Folder Delete -> contentChanged
    @HxRequest
    @HxRefresh
    @DeleteMapping("/delete")
    fun deleteFolder(folderUUID: UUID): String {
        folderService.deleteFolder(folderUUID)
        return "fragments/ok"
    }

    // Add File -> contentChanged
    @HxRequest
    @HxRefresh
    @PostMapping("/newfile")
    fun postNewFile(folderUUID: UUID): String {
        fileService.newFile(folderUUID)
        return "fragments/ok"
    }

    //Delete File -> contentChanged
    @HxRequest
    @HxRefresh
    @DeleteMapping("/deletefile")
    fun deleteFile(fileUUID: UUID): String {
        fileService.deleteFile(fileUUID)
        return "fragments/ok"
    }

    // add Permission -> permissionChanged
    @HxRequest
    @HxRefresh
    @PostMapping("/addpermission")
    fun addPermission(folderUUID: UUID, username: String): String {
        folderService.addPermission(folderUUID, username)
        return "fragments/ok"
    }


    // delete Permission -> permissionChanged
    @HxRequest
    @HxRefresh
    @DeleteMapping("/deletepermission")
    fun deletePermission(permissionUUID: UUID): String {
        folderService.deletePermission(permissionUUID)
        return "fragments/ok"
    }

//    @PostMapping("/addPermission")
//    fun postNewPermissions(folderUUID: UUID, username: String, canWrite:Boolean):String {
//
//    }

    @GetMapping("/shared-with-me")
    fun pageSharedWithMe(model: Model): String {
        val (list,fileList) = folderService.getAllShredWithMe();
        // files
        model.addAttribute("isOwner",false)
        model.addAttribute("title", "Shared with me")
        model.addAttribute("foldersToShow", list)
        model.addAttribute("filesToShow", fileList)

        return "pages/files/usersFolder"

    }

}