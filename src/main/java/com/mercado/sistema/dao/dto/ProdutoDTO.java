package com.mercado.sistema.dao.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mercado.sistema.dao.model.Produto;
import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProdutoDTO {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("nome")
    private String nome;

    @JsonProperty("descricao")
    private String descricao;

    @JsonProperty("preco")
    private BigDecimal preco;

    @JsonProperty("preco_promocao")
    private BigDecimal precoPromocao;

    @JsonProperty("codigo_barras")
    private String codigoBarras;

    @JsonProperty("estoque")
    private Integer quantidade_estoque;

    @JsonProperty("importado")
    private Boolean importado;

    @JsonProperty("ativo")
    private Boolean ativo;

    @JsonProperty("unidade_medida")
    private String unidadeMedida;

    @JsonProperty("categoria")
    private CategoriaDTO categoria;

    @JsonProperty("imagem")
    private String imagem;

    @JsonProperty("conteudo")
    private Boolean conteudo;

    // Construtor principal: recebe Produto e o valor de conteudo
    public ProdutoDTO(Produto produto, boolean conteudo) {
        CategoriaDTO categoriaDTO = new CategoriaDTO(produto.getId(), produto.getNome());
        this.id = produto.getId();
        this.nome = produto.getNome();
        this.descricao = produto.getDescricao();
        this.preco = produto.getPreco();
        this.precoPromocao = produto.getPrecoPromocao();
        this.codigoBarras = produto.getCodigoBarras();
        this.quantidade_estoque = produto.getEstoque();
        this.importado = produto.isImportado();
        this.ativo = produto.isAtivo();
        this.categoria = categoriaDTO;
        this.conteudo = conteudo;
    }

    // Construtor secundário: recebe Produto e assume conteudo = true
    public ProdutoDTO(Produto produto) {
        this(produto, true);
    }

    // Método utilitário para converter lista com conteudo padrão = true
    public static List<ProdutoDTO> fromProdutoList(List<Produto> produtos) {
        return produtos.stream()
                .map(ProdutoDTO::new) // usa o construtor acima
                .sorted(Comparator
                        .comparing(ProdutoDTO::getConteudo).reversed()               // true primeiro
                        .thenComparing(ProdutoDTO::getNome,                          // opcional: desempatar por nome
                                Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER)))
                .collect(Collectors.toList());
    }

    // Método utilitário para converter lista com conteudo definido manualmente
    public static List<ProdutoDTO> fromProdutoList(List<Produto> produtos, boolean conteudo) {
        return produtos.stream()
                .map(p -> new ProdutoDTO(p, conteudo))
                .sorted(Comparator
                        .comparing(ProdutoDTO::getConteudo).reversed()               // true primeiro
                        .thenComparing(ProdutoDTO::getNome,                          // opcional: desempatar por nome
                                Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER)))
                .collect(Collectors.toList());
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CategoriaDTO {
        @JsonProperty("id")
        private Long id;

        @JsonProperty("nome")
        private String nome;
    }

}
