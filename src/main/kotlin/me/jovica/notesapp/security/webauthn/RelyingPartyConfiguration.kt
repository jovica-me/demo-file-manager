package me.jovica.notesapp.security.webauthn

import com.yubico.webauthn.CredentialRepository
import com.yubico.webauthn.RelyingParty
import com.yubico.webauthn.data.RelyingPartyIdentity
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class RelyingPartyConfiguration {

    @Value("\${myapp.domain}")
    private val domainName = "localhost"

    @Value("\${spring.application.name}")
    private val appName = "RIS Notes app"

    @Bean
    fun relyingParty(credentialRepository: CredentialRepository?): RelyingParty {
        val rpIdentity: RelyingPartyIdentity =
                RelyingPartyIdentity.builder()
                        .id(domainName)
                        .name(appName)
                        .build()

        val relyingParty = RelyingParty.builder().identity(rpIdentity).credentialRepository(credentialRepository).allowOriginPort(true).build()

        return relyingParty
    }
}