package com.mercado.sistema.config;

import com.mercado.sistema.dao.model.Usuario;
import com.mercado.sistema.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {
  @Autowired private UsuarioService usuarioService;

  @Override
  public void run(String... args) throws Exception {
    // Criar usuário admin se não existir
    if (!usuarioService.existsByUsername("admin")) {
      Usuario admin = new Usuario();
      admin.setUsername("admin");
      admin.setEmail("admin@sistema.com");
      admin.setPassword("At@cada0Camb");

      // Será criptografada pelo service
      admin.setRole("ADMIN");
      usuarioService.save(admin);
      System.out.println("✅ Usuário admin criado com sucesso!");
      System.out.println("   Username: admin");
      System.out.println("   Role: ADMIN");
    } else {
      System.out.println("ℹ  Usuário admin já existe");
    }
  }
}
