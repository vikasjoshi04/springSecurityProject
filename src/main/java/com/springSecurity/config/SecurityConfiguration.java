package com.springSecurity.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.springSecurity.entity.User;
import com.springSecurity.entity.UserLogin;
import com.springSecurity.repository.UserLoginRepository;
import com.springSecurity.repository.UserRepository;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter implements WebMvcConfigurer {
	
	@Autowired
	private UserLoginRepository userLoginRepo;

	@Value("${allowedOrigins}")
	private String allowedOrigins;
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.httpBasic().and().authorizeRequests().antMatchers(HttpMethod.OPTIONS, "/**").permitAll().antMatchers("/user/login", "/user/logout").permitAll().anyRequest().authenticated().and().csrf().csrfTokenRepository(csrfTokenRepository()).ignoringAntMatchers("/**").and().addFilterAfter(new CsrfHeaderFilter(), CsrfFilter.class);
	}

	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/**").allowedOrigins(allowedOrigins).allowedMethods("GET", "POST","PUT", "DELETE");
	}

	@Override
	public void configure(WebSecurity web) throws Exception {
		web.ignoring().antMatchers("/objects/**", "/user/register");
	}

	private static CsrfTokenRepository csrfTokenRepository() {
		HttpSessionCsrfTokenRepository repository = new HttpSessionCsrfTokenRepository();
		repository.setHeaderName("X-XSRF-TOKEN");
		return repository;
	}

	@Override
	protected void configure(AuthenticationManagerBuilder authManagerBuilder) throws Exception {
		authManagerBuilder.authenticationProvider(dbAuthenticationProvider());
	}

	@Bean
	public AuthenticationProvider dbAuthenticationProvider() {
		return new AuthenticationProvider() {
			@Autowired
			private UserRepository userRepository;

			@Override
			public boolean supports(Class<?> authentication) {
				return authentication.isAssignableFrom(UsernamePasswordAuthenticationToken.class);
			}

			@Override
			public Authentication authenticate(Authentication authentication) throws AuthenticationException {
				UsernamePasswordAuthenticationToken up = (UsernamePasswordAuthenticationToken) authentication;
				// fetching user by email.
				User user = userRepository.findByEmail(up.getName());
				// checking if exist
				if (user != null) {
					// fetching user login detail by user.
					UserLogin userlogin = userLoginRepo.findByUser(user);
					// checking login detail.
					if (userlogin != null && userRepository.encryptPassword(up.getCredentials().toString()).equals(userlogin.getPassword())) {
						MyUserDetails userDetails = new MyUserDetails(user);
						UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(up.getPrincipal(), up.getCredentials(), userDetails.getAuthorities());

						auth.setDetails(userDetails);
						return auth;
					}
				}
				return null;
			}
		};
	}

	
}
