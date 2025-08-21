package com.mercado.sistema.config;

import com.mercado.sistema.service.UsuarioService;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration config)
          throws Exception {
    return config.getAuthenticationManager();
  }

  @Bean
  public UserDetailsService userDetailsService(UsuarioService usuarioService) {
    return username -> {
      var usuario =
              usuarioService
                      .findByUsername(username)
                      .orElseThrow(
                              () -> new UsernameNotFoundException("Usuario nao encontrado: " + username));

      System.out.println("Usuario: " + username);
      System.out.println("Roles: " + usuario.getRole());

      return User.builder()
              .username(username)
              .password(usuario.getPassword())
              .roles(usuario.getRole())
              .build();
    };
  }

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http.authorizeHttpRequests(
                    authz ->
                            authz
                                    // 1) estáticos (sempre bom liberar)
                                    .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
                                    .requestMatchers("/webjars/**", "/favicon.ico").permitAll()
                                    .requestMatchers("/actuator/**").permitAll()
                                    .requestMatchers("/img/**").permitAll()

                                    // 2) seus endpoints PÚBLICO
                                    .requestMatchers(HttpMethod.GET, "/api/export/produtos/**").permitAll()
                                    .requestMatchers(HttpMethod.POST, "/api/export/import-txt/**").permitAll()
                                    .requestMatchers(HttpMethod.GET, "/api/export/produtos-json").permitAll()
                                    .requestMatchers(HttpMethod.GET, "/api/export/produtos-json-remoto/**").permitAll()

                                    // 3) endpoints de upload - permitir para usuários autenticados
                                    .requestMatchers(HttpMethod.POST, "/api-tv/upload").permitAll()
                                    .requestMatchers(HttpMethod.DELETE, "/api-tv/upload/**").permitAll()
                                    .requestMatchers(HttpMethod.GET, "/api-tv/upload/**").permitAll()

                                    // 4) tudo o mais precisa estar autenticado
                                    .anyRequest()
                                    .authenticated())
            .formLogin(
                    form ->
                            form.loginPage("/login")
                                    .defaultSuccessUrl("/home", true)
                                    .failureUrl("/login?error=true")
                                    .permitAll())
            .logout(
                    logout ->
                            logout
                                    // 🔧 CORREÇÃO: Usar URL completa com context path
                                    .logoutUrl("/api-mercado/logout")
                                    .logoutSuccessUrl("/api-mercado/login?logout=true")
                                    .permitAll())
            .csrf(csrf -> csrf.ignoringRequestMatchers("/api/**", "/api-tv/**"));
    return http.build();
  }
}