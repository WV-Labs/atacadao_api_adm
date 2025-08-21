package com.mercado.sistema.dao.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "visualizacoes", schema = "schemamercado")
public class Visualizacao {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "terminal_id")
  private Terminal terminal;

  @ManyToOne
  @JoinColumn(name = "produto_id")
  private Produto produto;

  private LocalDateTime dataHora = LocalDateTime.now();
  private String ip;

  // Construtores

  public Visualizacao() {}

  public Visualizacao(Terminal terminal, Produto produto, String ip) {
    this.terminal = terminal;
    this.produto = produto;
    this.ip = ip;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Terminal getTerminal() {
    return terminal;
  }

  public void setTerminal(Terminal terminal) {
    this.terminal = terminal;
  }

  public Produto getProduto() {
    return produto;
  }

  public void setProduto(Produto produto) {
    this.produto = produto;
  }

  public LocalDateTime getDataHora() {
    return dataHora;
  }

  public void setDataHora(LocalDateTime dataHora) {
    this.dataHora = dataHora;
  }

  public String getIp() {
    return ip;
  }

  public void setIp(String ip) {
    this.ip = ip;
  }
}
