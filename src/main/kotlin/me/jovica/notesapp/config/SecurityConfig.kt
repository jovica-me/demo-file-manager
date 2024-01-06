package me.jovica.notesapp.config

import org.springframework.boot.autoconfigure.security.servlet.PathRequest
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.invoke
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.provisioning.InMemoryUserDetailsManager
import org.springframework.security.web.SecurityFilterChain

@Configuration
@EnableWebSecurity
class SecurityConfig {
    @Bean
    fun filterPagesChain(http: HttpSecurity): SecurityFilterChain {
        http {
            authorizeRequests {
                authorize("/", permitAll)
                authorize("/login", permitAll)
                authorize("/register", permitAll)
                authorize("/api/webauthn/**",permitAll)
                authorize(PathRequest.toStaticResources().atCommonLocations(), permitAll)
                authorize(anyRequest, authenticated)
            }
            formLogin {
                loginPage = "/login"
            }
        }
        return http.build()
    }




//    @Bean
//    fun userDetailsService(): UserDetailsService {
//        val userDetails = User.withDefaultPasswordEncoder()
//                .username("user")
//                .password("password")
//                .roles("USER")
//                .build()
//        return InMemoryUserDetailsManager(userDetails)
//    }


    /*	@Bean
	UserDetailsService userDetailsService() {
		UserDetails userDetails = User.withUsername("admin")
			.password(getPasswordEncoder().encode("123456"))
			.roles("MANAGER")
			.build();

		return new InMemoryUserDetailsManager(userDetails);
	}*/
    /*	@Bean
	UserDetailsService customUserDetailsService() {
		return new CustomUserDetailService();
	}*/


}