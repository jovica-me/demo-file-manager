package me.jovica.notesapp.controller

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping

@Controller
class PagesController {
    @GetMapping("/")
    fun home ():String {
        return "pages/index"
    }
}