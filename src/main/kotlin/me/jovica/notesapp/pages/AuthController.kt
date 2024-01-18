package me.jovica.notesapp.pages

import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping


@Controller
class AuthController {


    @GetMapping("/login")
    fun login(model: Model): String {
        return "login"
    }

    @GetMapping("/register")
    fun register(model: Model): String {
        return "register"
    }

    @GetMapping("/hello")
    fun logedInOnly(model: Model): String {

        return "hello"
    }
}