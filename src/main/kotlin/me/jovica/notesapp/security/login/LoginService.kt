package me.jovica.notesapp.security.login

import com.yubico.webauthn.RelyingParty
import org.springframework.stereotype.Service

@Service
class LoginService(val relyingParty: RelyingParty) {



}