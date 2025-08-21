package com.mercado.sistema.dao.repository;

import static com.mercado.sistema.service.ProdutoService.CAT_SEMCADASTRO;

import com.mercado.sistema.dao.model.Categoria;
import com.mercado.sistema.dao.model.Produto;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ProdutoRepository extends JpaRepository<Produto, Long> {
    Optional<Produto> findById(long id);

    Optional<Produto> findByCdTxtimport(Long cdTxtimport);
    List<Produto> findAllByAtivoTrueOrderByCategoriaAscNomeAscNomeAscAtivo();
    List<Produto> findAllByAtivoTrue();
    @Query("SELECT p FROM Produto p WHERE p.categoria.id = :categoriaId or  p.categoria.id = " + CAT_SEMCADASTRO + " order by p.categoria.id, p.nome asc ")
    List<Produto> findByCategoria_IdCustom(Long categoriaId);
    List<Produto> findByCategoria_IdOrderByNome(Long categoriaId);
    List<Produto> findByCategoriaAndAtivoTrueOrderByNomeAsc(Categoria categoria);
    List<Produto> findByNomeContainingIgnoreCase(String nome);
    @Query("SELECT p FROM Produto p WHERE p.estoque < :minimo")
    List<Produto> findProdutosComEstoqueBaixo(Integer minimo);
    @Query("SELECT COUNT(p) FROM Produto p WHERE p.categoria is null or (p.categoria.id not in (Select c.id from Categoria c))")
    Long countByProdutoNaoAssociado();

    @Modifying
    @Query("UPDATE Produto set ativo = false WHERE importado = true")
    void atualizaProdutosImportacao();
    @Modifying
    @Query("DELETE FROM Produto WHERE importado = true and ativo = false and categoria.id = " + CAT_SEMCADASTRO)
    void excluiProdutosImportacao();
}
