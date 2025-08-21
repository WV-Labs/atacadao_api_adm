package com.mercado.sistema.service;

import com.mercado.sistema.dao.dto.ConteudoRequest;
import com.mercado.sistema.dao.model.Conteudo;
import com.mercado.sistema.dao.model.ConteudoTabelaPreco;
import com.mercado.sistema.dao.model.Produto;
import com.mercado.sistema.dao.repository.ConteudoRepository;
import com.mercado.sistema.dao.repository.ConteudoTabelaPrecoRepository;
import com.mercado.sistema.dao.repository.ProdutoRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ConteudoService {
    @Autowired
    private ConteudoRepository conteudoRepository;
    @Autowired
    private ProdutoRepository produtoRepository;
    @Autowired
    private ConteudoTabelaPrecoRepository conteudoTabelaPrecoRepository;
    @Autowired
    private ConteudoTabelaPrecosService conteudoTabelaPrecosService;
    public Conteudo findConteudoById(Long id) {
        return conteudoRepository.findConteudoById(id);
    }
    public List<Produto> findProdutos(Long terminalId) {
        List<Produto> produtos = conteudoTabelaPrecoRepository.findProdutos(terminalId);
        return produtos;
    }
    public List<Long> findProdutoId(Long terminalId) {
        return conteudoRepository.findProdutosId(terminalId);
    }
    public Optional<Conteudo> findById(Long id) {
        return conteudoRepository.findById(id);
    }

    public Conteudo getConteudo(ConteudoRequest request) {
        Conteudo conteudo = new Conteudo();
        conteudo.setId(request.getId());
        conteudo.setTitulo(request.getTitulo());
        conteudo.setDescricao(request.getDescricao());
        conteudo.setTipoConteudo(request.getTipoConteudo());
        conteudo.setProdutosSelecionados(request.getProdutosSelecionados());
        return conteudo;
    }
    @Transactional
    public void salvar(Conteudo conteudo) {
        if(conteudo.getId() == null){
            conteudoRepository.save(conteudo);
            setProdutosSelecionados(conteudo);
        }else {
            List<ConteudoTabelaPreco> conteudoTabelaPrecos = conteudoTabelaPrecosService.getConteudoTabelaPrecos(conteudo);
            conteudoTabelaPrecoRepository.deleteAll(conteudoTabelaPrecos);
            setProdutosSelecionados(conteudo);
            conteudoRepository.save(conteudo);
        }
    }

    private void setProdutosSelecionados(Conteudo conteudo) {
        if (conteudo.getProdutosSelecionados() != null && !conteudo.getProdutosSelecionados().isBlank()) {
            String[] produtosSelecionados = conteudo.getProdutosSelecionados().split(",");
            for (String produtoSelecionado : produtosSelecionados) {
                ConteudoTabelaPreco conteudoTabelaPreco = new ConteudoTabelaPreco();
                conteudoTabelaPreco.setConteudo(conteudo);

                Produto produto = new Produto();
                produto.setId(Long.parseLong(produtoSelecionado));
                conteudoTabelaPreco.setProduto(produto);

                conteudoTabelaPrecoRepository.save(conteudoTabelaPreco);
            }
        }
    }

    @CacheEvict(value = "produtosRemotos", allEntries = true)
    public void deleteById(long id) {
        Optional<Conteudo> conteudo = findById(id);
        List<ConteudoTabelaPreco> conteudoTabelaPrecos = conteudoTabelaPrecosService.getConteudoTabelaPrecos(conteudo.get());
        conteudoTabelaPrecoRepository.deleteAll(conteudoTabelaPrecos);
        conteudoRepository.deleteById(id);
    }

    public List<Conteudo> listarTodos() { return conteudoRepository.findAll(); }

}
