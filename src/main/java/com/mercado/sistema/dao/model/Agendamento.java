package com.mercado.sistema.dao.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

@Entity
@Table(name = "agendamentos", schema = "schemamercado")
@AllArgsConstructor
@NoArgsConstructor
public class Agendamento {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotBlank(message = "Título obrigatório!")
  @Size(max=100, message = "Título deve ter no máximo 100 caracteres!")
  @Column(name = "titulo", nullable = false, length = 100)
  private String titulo;

  @NotBlank(message = "Descrição obrigatório!")
  @Size(max=100, message = "Descrição deve ter no máximo 100 caracteres!")
  @Column(name = "descricao", nullable = false, length = 100)
  private String descricao;

  @Column(name = "data_hora_inicio")
  @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
  private LocalDateTime dataInicio = LocalDateTime.now();

  @Column(name = "data_hora_fim")
  @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
  private LocalDateTime dataFim = LocalDateTime.now().plusDays(1);

  private boolean ativo = true;

  @ManyToOne
  @JoinColumn(name = "terminal_conteudo_id")
  private TerminalConteudo terminalConteudo;

  @ManyToOne
  @JoinColumn(name = "usuario_id")
  private Usuario usuario;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
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

  public LocalDateTime getDataInicio() {
    return dataInicio;
  }

  public void setDataInicio(LocalDateTime dataInicio) {
    this.dataInicio = dataInicio;
  }

  public LocalDateTime getDataFim() {
    return dataFim;
  }

  public void setDataFim(LocalDateTime dataFim) {
    this.dataFim = dataFim;
  }

  public TerminalConteudo getTerminalConteudo() {
    return terminalConteudo;
  }

  public void setTerminalConteudo(TerminalConteudo terminalConteudo) {
    this.terminalConteudo = terminalConteudo;
  }

  public Usuario getUsuario() {
    return usuario;
  }

  public void setUsuario(Usuario usuario) {
    this.usuario = usuario;
  }

  public boolean isAtivo() {
    return ativo;
  }

  public void setAtivo(boolean ativo) {
    this.ativo = ativo;
  }
}
