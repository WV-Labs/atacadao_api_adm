package com.mercado.sistema.service;

import com.mercado.sistema.dao.model.Categoria;
import com.mercado.sistema.dao.model.Produto;
import com.mercado.sistema.dao.repository.CategoriaRepository;
import com.mercado.sistema.dao.repository.ProdutoRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoriaService {
    @Autowired
    private CategoriaRepository categoriaRepository;
    @Autowired
    private ProdutoRepository produtoRepository;

    public List<Categoria> findAll() {
        return categoriaRepository.findAllByOrderByNomeAscDescricaoAsc();
    }

    public Optional<Categoria> findById(Long id) {
        return categoriaRepository.findById(id);
    }

    public Optional<Categoria> findByNome(String nome) {
        return categoriaRepository.findByNomeEqualsIgnoreCase(nome);
    }

    public Categoria save(Categoria categoria) {
        if(categoria.getId()==null){
            Optional<Categoria> categoriaFind = categoriaRepository.findByNomeEqualsIgnoreCase(categoria.getNome());
            if (categoriaFind.isPresent()) {
                return null;
            }
            Categoria save = categoriaRepository.save(categoria);
            setProdutosSelecionados(save);
            return save;
        }
        setProdutosSelecionados(categoria);
        Categoria save = categoriaRepository.save(categoria);
        return save;
        
    }

    private void setProdutosSelecionados(Categoria categoria) {
        if (categoria.getProdutosSelecionados() != null && !categoria.getProdutosSelecionados().isBlank()) {
            String[] produtosSelecionados = categoria.getProdutosSelecionados().split(",");
            for (String produtoSelecionado : produtosSelecionados) {
                Produto produto = produtoRepository.getReferenceById(Long.parseLong(produtoSelecionado));
                produto.setId(Long.parseLong(produtoSelecionado));
                produto.setCategoria(categoria);
                produtoRepository.save(produto);
            }
        }
    }
    
    public void deleteById(long id) {
        categoriaRepository.deleteById(id);
    }

    public boolean existsByNome(String nome) {
        return categoriaRepository.existsByNome(nome);
    }
}
