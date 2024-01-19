package me.jovica.notesapp.controller

import io.github.wimdeblauwe.htmx.spring.boot.mvc.HtmxResponse
import io.github.wimdeblauwe.htmx.spring.boot.mvc.HxRefresh
import io.github.wimdeblauwe.htmx.spring.boot.mvc.HxRequest
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam


@Controller
class AuthController {


    @GetMapping("/login")
    fun login(model: Model, @RequestParam(required = false) error: String?): String {
        if(error.equals("error")) {
            model.addAttribute("error","error while logging in")
        }
        return "login"
    }

    @GetMapping("/register")
    fun register(model: Model): String {
        return "register"
    }

    val logoutHandler = SecurityContextLogoutHandler()

    @HxRequest
    @HxRefresh
    @GetMapping("/logout")
    fun performLogout(authentication: Authentication,  request: HttpServletRequest,  response: HttpServletResponse): String {
        this.logoutHandler.logout(request, response, authentication)
        return "fragments/nav"
    }

    @HxRequest
    @GetMapping("/ui/update-nav")
    fun updateNav():HtmxResponse {
        return HtmxResponse.builder()
            .view("fragments/nav")
            .build()
    }



}