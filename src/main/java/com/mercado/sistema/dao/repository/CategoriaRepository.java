package com.mercado.sistema.dao.repository;

import com.mercado.sistema.dao.model.Categoria;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Long> {
    List<Categoria> findAllByOrderByNomeAscDescricaoAsc();
    Optional<Categoria> findByNomeEqualsIgnoreCase(String nome);
    boolean existsByNome(String nome);
}
