package me.jovica.notesapp.pages

import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod


@Controller
class AuthController {


    @GetMapping("login")
    fun loadForm(model: Model): String {
        return "login"
    }
}