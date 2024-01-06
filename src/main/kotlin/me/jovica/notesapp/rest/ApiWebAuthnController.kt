package me.jovica.notesapp.rest

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController

@Controller("/api/webauthn/")
class ApiWebAuthnController {

    @ResponseBody
    @PostMapping("login/option")
    fun loginStart() {

    }
}