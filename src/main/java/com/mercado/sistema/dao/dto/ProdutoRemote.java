package com.mercado.sistema.dao.dto;

import static com.mercado.sistema.general.Constants.TCTABELAPRECO;

import com.mercado.sistema.dao.model.Produto;
import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class ProdutoRemote {
    private Long id;
    private String nome;
    private String descricao;
    private BigDecimal preco;
    private BigDecimal precoPromocao;
    private int quantidade_estoque;
    private boolean disponivel;
    private String categoria;
    private String imagem;
    private boolean conteudo;
    private String descricaoCategoria;
    private String abreviacaoUM;
    private int tipoConteudo;
    private String caminhoImagemVideo;

    // Construtor principal: recebe Produto e o valor de conteudo
    public ProdutoRemote(Produto produto, boolean conteudo) {
        this.id = produto.getId();
        this.nome = produto.getNome();
        this.descricao = produto.getDescricao();
        this.preco = produto.getPreco();
        this.precoPromocao = produto.getPrecoPromocao();
        this.quantidade_estoque = produto.getEstoque();
        this.disponivel = produto.isAtivo();
        this.imagem = produto.getImagem();
        this.conteudo = conteudo;
        this.categoria = produto.getCategoria() != null ? produto.getCategoria().getNome() : "";
        this.abreviacaoUM = produto.getUnidadeMedida().getAbreviacao() != null ? produto.getUnidadeMedida().getAbreviacao() : "";
        this.tipoConteudo = TCTABELAPRECO;
        this.descricaoCategoria = produto.getCategoria() != null ? produto.getCategoria().getNomeExibicao() : "";
    }

    // Construtor secundário: recebe Produto e assume conteudo = true
    public ProdutoRemote(Produto produto) {
        this(produto, true);
    }

    // Método utilitário para converter lista com conteudo padrão = true
    public static List<ProdutoRemote> fromProdutoList(List<Produto> produtos) {
        return produtos.stream()
                .map(ProdutoRemote::new) // usa o construtor acima
                .sorted(Comparator
                        .comparing(ProdutoRemote::isConteudo).reversed()               // true primeiro
                        .thenComparing(ProdutoRemote::getNome,                          // opcional: desempatar por nome
                                Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER)))
                .collect(Collectors.toList());
    }

    // Método utilitário para converter lista com conteudo definido manualmente
    public static List<ProdutoRemote> fromProdutoList(List<Produto> produtos, boolean conteudo){
        return produtos.stream()
                .map(p -> new ProdutoRemote(p, conteudo))
                .filter( pFilter -> pFilter.isConteudo()== conteudo)
                .sorted(Comparator
                        .comparing(ProdutoRemote::isConteudo).reversed()                // true primeiro
                        .thenComparing(ProdutoRemote::getNome,                          // opcional: desempatar por nome
                                Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER)))
                .collect(Collectors.toList());
    }
}
