package com.mercado.sistema.dao.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class ConteudoRequest {

    private Long id;

    @NotNull(message = "Tipo de conteúdo é obrigatório!")
    private Integer tipoConteudo;

    @NotBlank(message = "Título obrigatório!")
    @Size(max = 100, message = "Título deve ter no máximo 100 caracteres!")
    private String titulo;

    @NotBlank(message = "Descrição obrigatório!")
    @Size(max = 100, message = "Descrição deve ter no máximo 100 caracteres!")
    private String descricao;

    private Long categoriaId;
    
    // Campo para o nome do arquivo (salvo no banco)
    private String nomeMidia;

    // Para produtos selecionados
    private String produtosSelecionados;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getTipoConteudo() {
        return tipoConteudo;
    }

    public void setTipoConteudo(Integer tipoConteudo) {
        this.tipoConteudo = tipoConteudo;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Long getCategoriaId() {
        return categoriaId;
    }

    public void setCategoriaId(Long categoriaId) {
        this.categoriaId = categoriaId;
    }

    public String getNomeMidia() {
        return nomeMidia;
    }

    public void setNomeMidia(String nomeMidia) {
        this.nomeMidia = nomeMidia;
    }

    public String getProdutosSelecionados() {
        return produtosSelecionados;
    }

    public void setProdutosSelecionados(String produtosSelecionados) {
        this.produtosSelecionados = produtosSelecionados;
    }
}
