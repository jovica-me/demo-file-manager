package me.jovica.notesapp.security.user

import com.yubico.webauthn.RegistrationResult
import me.jovica.notesapp.domain.UserEntity
import me.jovica.notesapp.domain.UserEntityRepository
import me.jovica.notesapp.domain.files.FolderEntity
import me.jovica.notesapp.domain.files.FolderEntityRepository
import me.jovica.notesapp.security.webauthn.WebAuthnCredentialEntity
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class UserService(
    private val userAccountRepository: UserAccountRepository,
    private val authoritiesEntityRepository: AuthoritiesEntityRepository,
    private val userEntityRepository: UserEntityRepository,
    private val folderEntityRepository: FolderEntityRepository
) : UserDetailsService {

    fun createUser(username: String?, fullName: String?): UserAccountEntity {
        if (fullName.isNullOrBlank()) throw IllegalArgumentException(" fullName is null or blank")
        if (username.isNullOrBlank()) throw IllegalArgumentException(" username is null or blank")

        userAccountRepository.findByUsername(username)
            .apply { if (this != null) throw IllegalStateException("User already exists") }


        val x = authoritiesEntityRepository.findByAuthority("ROLE_USER").let {
            val res: AuthoritiesEntity;

            if (it.isEmpty) {
                val r = AuthoritiesEntity()
                r.authority = "ROLE_USER"
                val radmin = AuthoritiesEntity()
                radmin.authority = "ROLE_ADMIN"
                authoritiesEntityRepository.save(radmin)
                res = authoritiesEntityRepository.saveAndFlush(r)
            } else {
                res = it.get()
            }
            res
        }

        var userAccount = UserAccountEntity()
        val user = UserEntity()
        val topFolder = FolderEntity()

        userAccount.username = username
        userAccount.fullName = fullName
        userAccount.authorities = mutableSetOf(x)
        userAccount.userEntity = user
        user.userAccount = userAccount

        userAccount = userAccountRepository.save(userAccount)

        user.topFolder = topFolder
        topFolder.owner = user
        topFolder.topFolderOfUser = user
        topFolder.name = "Hello from - New folder"

        userEntityRepository.save(user)

        return userAccount

    }

    @Transactional(propagation = Propagation.REQUIRED)
    fun addCredential(registrationResult: RegistrationResult, user: UserAccountEntity) {

        val userAccount = userAccountRepository.findById(user.id!!)
            .orElseThrow({ RuntimeException("Can not add webAuthnCredential to a user who does not exist") })

        val webAuthnCredentialEntity = WebAuthnCredentialEntity()
        webAuthnCredentialEntity.id = registrationResult.keyId.id.base64Url
        webAuthnCredentialEntity.userId = user.id
        webAuthnCredentialEntity.publicKey = registrationResult.publicKeyCose.base64Url
        webAuthnCredentialEntity.type = registrationResult.keyId.type.name

        userAccount.credentials.add(webAuthnCredentialEntity)

    }

    fun findUserById(userId: UUID): Optional<UserAccountEntity> {
        return userAccountRepository.findById(userId)
    }

    fun findUserByUsername(username: String): UserAccountEntity {
        val user: UserAccountEntity =
            userAccountRepository.findByUsername(username) ?: throw IllegalStateException("User not found")
        return user
    }


    fun findCredentialById(credentialId: String?): Optional<WebAuthnCredentialEntity> {
        return Optional.empty()
    }

    override fun loadUserByUsername(username: String): UserDetails {
        if (username.isEmpty()) throw BadCredentialsException("Username is mandatory")
        val user = userAccountRepository.findByUsername(username)

        return user?.let { TokenUserDetails(it) } ?: throw UsernameNotFoundException("User not found")
    }
}

class TokenUserDetails(private val userIdentity: UserAccountEntity) : UserDetails {

    companion object {
        private const val serialVersionUID = 1L
        private val log = org.slf4j.LoggerFactory.getLogger(TokenUserDetails::class.java)
    }

    override fun toString(): String = userIdentity.username

    override fun getAuthorities(): MutableCollection<out GrantedAuthority> {
        return userIdentity.authorities.map { SimpleGrantedAuthority(it.authority) }.toMutableList()
    }

    override fun getPassword(): String {
        return " why do you expect a password, how useles is this code????, yes pls password is the only way to log in"
    }

    override fun getUsername(): String {
        return userIdentity.username
    }

    override fun isAccountNonExpired(): Boolean {
        return true
//        return userIdentity.accountNonExpired yes i need this Yes
    }

    override fun isAccountNonLocked(): Boolean {
        return true;
//        return userIdentity.accountNonLocked
    }

    override fun isCredentialsNonExpired(): Boolean {
        return true;
//        return userIdentity.credentialsNonExpired
    }

    override fun isEnabled(): Boolean {
        return true
//        return userIdentity.enabled
    }


    fun getWebAuthNCredentials(): MutableSet<WebAuthnCredentialEntity> {
        return userIdentity.credentials
    }

}