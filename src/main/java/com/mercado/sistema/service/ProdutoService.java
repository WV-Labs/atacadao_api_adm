package com.mercado.sistema.service;

import static com.mercado.sistema.general.Constants.TCTABELAPRECO;
import static java.lang.Long.parseLong;

import com.mercado.sistema.config.FileMonitorConfig;
import com.mercado.sistema.dao.dto.ProdutoDTO;
import com.mercado.sistema.dao.dto.ProdutoRemote;
import com.mercado.sistema.dao.model.Categoria;
import com.mercado.sistema.dao.model.Produto;
import com.mercado.sistema.dao.model.Terminal;
import com.mercado.sistema.dao.repository.ProdutoRepository;
import com.mercado.sistema.enums.UnidadeMedida;
import jakarta.validation.Valid;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class ProdutoService {
  public static final String ERRO_NA_TRANSACAO_ROLLBACK_SERÁ_EXECUTADO = "❌ Erro na transação - Rollback será executado: ";
  public static final int CAT_SEMCADASTRO = 16;
  public static final int CAT_PADARIA = 13;
  public static final int CAT_CONFEITARIA = 14;
  private static final String SUCCESS = "success";
  private static final String MESSAGE = "message";
  public static final String PRODUTO = "produto";
  private static final String TIPO_ERRO = "tipo_erro";
  private static final String TRANSACAO = "transacao";
  private static final String ROLLBACK = "ROLLBACK";
  
  // Usando records para melhor organização das categorias
  private static final Map<String, Set<String>> CATEGORIA_PALAVRAS_CHAVE = Map.of(
          "acougue", Set.of("LINGUICA", "BARRIGA", "BIFE CONTRA FILE", "FRALDINHA", "CUPIM", "BOVINO", "FRANGO", "LING.", "SUIN.", "BOV.", "ALCATRA", "PICANHA", "ANCHO", "BACON",
                  "PEITO COM OSSO", "PATINHO SEM OSSO", "MAMINHA"),
          "hortifruti", Set.of("MANGA", "TOMATE", "UVA", "VAGEM", "LIMAO", "ALFACE",
                  "ABACATE", "CENOURA", "CEBOLA", "CEREJA", "LARANJA", "LICHIA", "AMEIXA", "MEXIRICA", "MELAO"),
          "laticinios", Set.of("MORTADELA", "QJO.", "QUEIJO", "SALAME", "SALAMINHO", "PARMESAO", "PRES.COZ.AURORA",
                  "PRESUNTO ESTRELA", "PRESUNTO EXCELENCIA", "PRESUNTO FRIMESA", "PRESUNTO NOBRE"),
          "frios", Set.of("MUSSARELA", "LEVISSIMO SEARA"),
                  "padaria", Set.of("PAO ROSETA", "PAO DE QUEIJO", "PAO PAO NA CHAPA", "PAO ITALIANO", "PAO FRANCES MINI", "PAO FRANCES",
                          "PAO FAT.HUNGARA", "PAO DOCE CARTEIRA", "PAO DOCE", "PAO DE MILHO", "PAO BRIOCHE", "NESCAFE TRAD."));
  @Autowired private FileMonitorConfig config;
  @Autowired private ProdutoRepository produtoRepository;
  @Autowired private CategoriaService categoriaService;
  @Autowired private TerminalService terminalService;
  @Autowired private VisualizacaoService visualizacaoService;
  @Autowired private ConteudoService conteudoService;

  public ProdutoService(ProdutoRepository produtoRepository) {
    this.produtoRepository = produtoRepository;
  }

  @Cacheable("produtosRemotos")
  public List<Produto> listarTodos() {
    log.info("📦 Buscando produtos do banco (sem cache), lista ordenada");
    return produtoRepository.findAllByAtivoTrueOrderByCategoriaAscNomeAscNomeAscAtivo();
  }

  @CacheEvict(value = "produtosRemotos", allEntries = true)
  public List<Produto> limparCache() {
    log.info("🧹 Cache limpo");
    return this.recarregarCache();
  }

  @CachePut(value = "produtosRemotos")
  public List<Produto> recarregarCache() {
    log.info("♻️ Cache recarregado");
    return this.listarTodos(); // recarrega e atualiza o Redis
  }

  @Cacheable("produtos")
  public List<Produto> findByCategoria(Categoria categoria) {
    return produtoRepository.findByCategoriaAndAtivoTrueOrderByNomeAsc(categoria);
  }

  public Long countByProdutoNaoAssociado() {
    return produtoRepository.countByProdutoNaoAssociado();
  }

  @CacheEvict(value = "produtosRemotos", allEntries = true)
  public Produto save(Produto produto) {
      Produto save = produtoRepository.save(produto);
      if(save.getCdTxtimport()==null){
            save.setCdTxtimport(parseLong(String.valueOf(90000L+save.getId())));
            produtoRepository.save(save);
      }
      log.info("Produto salvo com sucesso: " + save.getNome() + " (ID: " + save.getId() + ")");

      return save;
  }

  @CacheEvict(value = "produtosRemotos", allEntries = true)
  public void deleteById(long id) {
    Optional<Produto> produto = findById(id);
    visualizacaoService.deleteByProduto(produto.orElse(null));
    produtoRepository.deleteById(id);
    this.limparCache();
  }

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public ResultadoOperacao tratarImportacaoTxt(List<ProdutoDTO> produtoDTOs, Map<String, Object> response) {
    try {
      ResultadoOperacao resultadoOperacao = new ResultadoOperacao();

      log.info("*** Atualizando Banco de Dados ***");
      this.atualizaProdutosImportacao();

      for (ProdutoDTO produtoDTO : produtoDTOs) {
        log.info("♻️ Carregado produto " + produtoDTO.getNome());
        resultadoOperacao = this.trataProduto(produtoDTO, response);
      }

      log.info("*** Excluindo registros importados antigos Banco de Dados ***");
      this.excluiProdutosImportacao();

      log.info("*** Todos PRODUTOS carregados ***");

      return resultadoOperacao;
    } catch (Exception e) {
      log.info(ERRO_NA_TRANSACAO_ROLLBACK_SERÁ_EXECUTADO + e.getMessage());
      // O @Transactional automaticamente faz rollback para qualquer Exception
      throw new RuntimeException("Erro ao processar produto: " + e.getMessage(), e);
    }
  }

  @Transactional(propagation = Propagation.MANDATORY)
  public void atualizaProdutosImportacao() {
    try{
      produtoRepository.atualizaProdutosImportacao();
    } catch (Exception e) {
      System.err.println(ERRO_NA_TRANSACAO_ROLLBACK_SERÁ_EXECUTADO + e.getMessage());
      // O @Transactional automaticamente faz rollback para qualquer Exception
      throw new RuntimeException("Erro ao atualizar BD na tabela produto: " + e.getMessage(), e);
    }
  }

  @Transactional(propagation = Propagation.MANDATORY)
  public void excluiProdutosImportacao() {
    try{
      produtoRepository.excluiProdutosImportacao();
    } catch (Exception e) {
      System.err.println(ERRO_NA_TRANSACAO_ROLLBACK_SERÁ_EXECUTADO + e.getMessage());
      // O @Transactional automaticamente faz rollback para qualquer Exception
      throw new RuntimeException("Erro ao excluir BD na tabela produto: " + e.getMessage(), e);
    }
  }

  @Cacheable("produtosRemotos")
  public List<Produto> findByProdutoCategoria(Categoria categoria) {
    return produtoRepository.findByCategoriaAndAtivoTrueOrderByNomeAsc(categoria);
  }

  @Cacheable("produtosRemotos")
  public List<Produto> findByCategoriaId(Long categoriaId) {
    return produtoRepository.findByCategoria_IdOrderByNome(categoriaId);
  }
  public List<Produto> findByCategoriaIdCustom(Long categoriaId) {
    return produtoRepository.findByCategoria_IdCustom(categoriaId);
  }
  public List<ProdutoRemote> obterListaProdutoRemotoGeral(String categoria, Integer numero) {
    List<Produto> produtos = obterListaProduto(categoria, numero);
    List<ProdutoRemote> produtosRemote = new ArrayList<>();
    if(produtos.isEmpty()){
      Optional<Terminal> terminal = terminalService.findByCategoriaNomeAndNumero(categoria, numero);
      produtosRemote.add(builderProdutoRemoteVazio(terminal.get()));
    } else {
        produtosRemote = getProdutoRemotes(produtos);
    }
    return produtosRemote;
  }

  public List<Produto> obterListaProduto(String categoria, Integer numero) {
    List<Produto> produtos;
    Optional<Terminal> terminal = terminalService.findByCategoriaNomeAndNumero(categoria, numero);
    if(terminal.isPresent()) {
      /*if(terminal.get().getTipoConteudo()==TCTABELAPRECO) {
        produtos = conteudoService.findProdutos(terminal.get().getId());
        if(produtos.isEmpty()) {
          produtos = getProdutos(categoria);
        }
        return produtos;
      }*/
    } else{
      return getProdutos(categoria);
    }
    return Collections.emptyList();
  }

  public List<ProdutoRemote> obterListaProdutoRemotoEspecifico(String categoria, Integer numero) {
    Optional<Terminal> termFind = terminalService.findByCategoriaNomeAndNumero(categoria, numero);
    if(termFind.isPresent()) {
      /*if (termFind.get().getTipoConteudo() != TCTABELAPRECO) {
        return Arrays.asList(builderProdutoRemoteVazio(termFind.get()));
      }*/
    }
    // quando NÃO existe terminal → usa só a categoria com conteudo=false
    return termFind
            .map(terminal -> {
/*
              // quando existe terminal, mas não é TCTABELAPRECO → lista vazia
              if (terminal..getTipoConteudo() != TCTABELAPRECO) {
                return Arrays.asList(builderProdutoRemoteVazio(terminal));
              }
*/

              // produtos vinculados ao terminal (conteudo = true por padrão no seu DTO)
              var doTerminal = ProdutoRemote.fromProdutoList(
                      nullSafe(conteudoService.findProdutos(terminal.getId()))
              );

              // quando não há produtos do terminal → traz os da categoria (conteudo=false)
              if (doTerminal.isEmpty()) {
                return ProdutoRemote.fromProdutoList(getProdutos(categoria), /*conteudo=*/false);
              }

              // senão, concatena: do terminal (true) + da categoria (false)
              var daCategoria = ProdutoRemote.fromProdutoList(getProdutos(categoria), /*conteudo=*/false);

              // 🔴 DEDUP: remova daCategoria os IDs já presentes em doTerminal
              var idsTerminal = doTerminal.stream()
                      .map(ProdutoRemote::getId)
                      .filter(Objects::nonNull)
                      .collect(Collectors.toSet());

              var daCategoriaSemDuplicar = daCategoria.stream()
                      .filter(p -> p.getId() == null || !idsTerminal.contains(p.getId()))
                      .toList();

              System.out.println("Acessou e vai unir");
              System.out.println(doTerminal.size());
              System.out.println(daCategoriaSemDuplicar.size());

              // junta e ordena (true primeiro, depois nome)
              return Stream.concat(doTerminal.stream(), daCategoriaSemDuplicar.stream())
                      .sorted(Comparator
                              .comparing(ProdutoRemote::isConteudo).reversed() // true primeiro
                              .thenComparing(ProdutoRemote::getNome,
                                      Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER)))
                      .toList();
            })
            // terminal não encontrado → só os da categoria (conteudo=false)
            .orElse( ProdutoRemote.fromProdutoList(getProdutos(categoria), /*conteudo=*/false));

  }
  
  private List<Produto> getProdutos(String categoria) {
    Optional<Categoria> categoriaOptional = categoriaService.findByNome(categoria);
    List<Produto> byCategoria = this.findByCategoria(categoriaOptional.orElse(null));
    return byCategoria;
  }

  public List<Produto> findProdutosEmPromocao() {
    return produtoRepository.findAllByAtivoTrue();
  }

  public List<Produto> findByNome(String nome) {
    return produtoRepository.findByNomeContainingIgnoreCase(nome);
  }

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  protected ResultadoOperacao trataProduto(ProdutoDTO produtoDTO, Map<String, Object> response) {
    try {
      System.out.println("📦 Recebendo produto: " + produtoDTO.getNome());
      System.out.println("🔍 Código: " + produtoDTO.getId());

      // 1. Verificar se já existe produto com código + nome
      Optional<Produto> produtoExistente = this.buscarProdutoExistente(produtoDTO);

      Produto produto;
      String operacao;

      if (produtoExistente.isPresent()) {
        // 2. ATUALIZAÇÃO - Produto existe, atualizar apenas o preço
        produto = produtoExistente.get();

        System.out.println(
            "🔄 Produto encontrado (ID: " + produto.getId() + "). Atualizando preço...");
        System.out.println(
            "💰 Preço atual: " + produto.getPreco() + " -> Novo preço: " + produtoDTO.getPreco());
        produto.setPreco(produtoDTO.getPreco());
        // Atualizar preço promocional se fornecido
        if (produtoDTO.getPrecoPromocao() != null && produtoDTO.getPrecoPromocao() != BigDecimal.valueOf(0L)) {
          System.out.println(
                  "💰 Preço Promocional atual: " + produto.getPrecoPromocao() + " -> Novo preço: " + produtoDTO.getPrecoPromocao());
          produto.setPrecoPromocao(produtoDTO.getPrecoPromocao());
        }

        produto.setNome(produtoExistente.get().getNome());

        if (produto.getCategoria().getId() == CAT_SEMCADASTRO) {
          verificaCategoria(produtoDTO, produto);
        }
        operacao = "ATUALIZADO";
      } else {
        // 3. INCLUSÃO - Produto não existe, criar novo
        System.out.println("➕ Produto não encontrado. Criando novo produto...");
        produto = mapearDTOParaEntidade(produtoDTO);
        Categoria categoria = this.buscarOuCriarCategoria(produtoDTO.getCategoria());
        produto.setCategoria(categoria);
        produto.setUnidadeMedida(UnidadeMedida.K);
        produto.setEstoque(1);
        verificaCategoria(produtoDTO, produto);
        operacao = "CRIADO";
      }
      prepararCamposComuns(produtoDTO, produto);
      if (produto.getCategoria().getId() == CAT_PADARIA || produto.getCategoria().getId() == CAT_CONFEITARIA ) {
          produto.setImportado(false);
      }

      // 4. Salvar produto
      Produto produtoSalvo = this.save(produto);

      // 5. Preparar resposta de sucesso
      response.put(SUCCESS, true);
      response.put(MESSAGE, "Produto " + operacao.toLowerCase() + " com sucesso!");
      response.put("operacao", operacao);
      response.put("produto", criarRespostaProduto(produtoSalvo));

      log.info(
          "✅ Produto " + operacao.toLowerCase() + " com sucesso! ID: " + produtoSalvo.getId());

      return new ResultadoOperacao(true, operacao, produtoSalvo, null);

    } catch (Exception e) {
      log.info(ERRO_NA_TRANSACAO_ROLLBACK_SERÁ_EXECUTADO + e.getMessage());
      // O @Transactional automaticamente faz rollback para qualquer Exception
      throw new RuntimeException("Erro ao processar produto: " + e.getMessage(), e);
    }
  }

  /** Buscar produto existente por código de barras + nome */
  private Optional<Produto> buscarProdutoExistente(ProdutoDTO produtoDTO) {
    // Primeiro tenta buscar por código de barras
    if (produtoDTO.getId() != null) {
      Optional<Produto> porCodigo = this.findByCdTxtimport(produtoDTO.getId());
      if (porCodigo.isPresent()) {
        return porCodigo;
      }
    }
    return Optional.empty();
  }

  /** Buscar ou criar categoria baseado no DTO Faz o de-para por ID ou nome */
  private Categoria buscarOuCriarCategoria(ProdutoDTO.CategoriaDTO categoriaDTO) {
    System.out.println(
        "🏷️ Processando categoria: ID="
            + categoriaDTO.getId()
            + ", Nome=SemCadastro");

    // 1. Tentar buscar por nome se fornecido
    if (categoriaDTO.getNome() != null && !categoriaDTO.getNome().trim().isEmpty()) {
      Optional<Categoria> categoriaPorNome = categoriaService.findByNome("SemCadastro");
      if (categoriaPorNome.isPresent()) {
        log.info("✅ Categoria encontrada por nome: " + categoriaPorNome.get().getNome());
        return categoriaPorNome.get();
      }
    }

    // 3. Se não encontrou e tem nome, criar nova categoria
    if (categoriaDTO.getNome() != null && !categoriaDTO.getNome().trim().isEmpty()) {
      log.info("➕ Criando nova categoria: " + categoriaDTO.getNome());

      Categoria novaCategoria = new Categoria();
      novaCategoria.setNome(categoriaDTO.getNome());
      novaCategoria.setDescricao("Categoria criada automaticamente via API");

      return categoriaService.save(novaCategoria);
    }

    // 4. Se chegou até aqui, usar categoria padrão (primeira disponível)
    List<Categoria> todasCategorias = categoriaService.findAll();
    if (!todasCategorias.isEmpty()) {
      Categoria categoriaDefault = todasCategorias.get(0);
      log.info("⚠️ Usando categoria padrão: " + categoriaDefault.getNome());
      return categoriaDefault;
    }

    throw new RuntimeException("Nenhuma categoria encontrada e não foi possível criar uma nova");
  }

  /** Mapear DTO para Entidade Produto */
  private Produto mapearDTOParaEntidade(ProdutoDTO produtoDTO) {
    Produto produto = new Produto();

    // Dados básicos
    produto.setNome(produtoDTO.getNome());
    produto.setCdTxtimport(produtoDTO.getId());
    produto.setDescricao(produtoDTO.getDescricao() != null ? produtoDTO.getDescricao() : "");
    produto.setPreco(produtoDTO.getPreco());
    produto.setPrecoPromocao(BigDecimal.valueOf(0.0));
    return produto;
  }

  private void prepararCamposComuns(ProdutoDTO produtoDTO, Produto produto) {
    produto.setCodigoBarras("");
    produto.setImportado(true);
    produto.setAtivo(true);
    produto.setImagem("");
  }

  private void verificaCategoria(ProdutoDTO produtoDTO, Produto produto) {
    if (produto.getCategoria().getId() == CAT_SEMCADASTRO) {
      System.out.println(
              "🔄 Categoria atualizada (ID: " + produtoDTO.getCategoria().getId() + "). Atualizando ...");

      var categoriaNova = determinarCategoria(produtoDTO)
              .orElse(produto.getCategoria());

      produto.setCategoria(categoriaNova);
    }
  }

  private Optional<Categoria> determinarCategoria(ProdutoDTO produtoDTO) {
    var nomeUpperCase = produtoDTO.getNome().toUpperCase();

    return CATEGORIA_PALAVRAS_CHAVE.entrySet().stream()
            .filter(entry -> entry.getValue().stream()
                    .anyMatch(nomeUpperCase::contains))
            .map(Map.Entry::getKey)
            .findFirst()
            .flatMap(nomeCategoria -> categoriaService.findByNome(nomeCategoria));
  }

  /** Criar resposta do produto para o JSON de retorno */
  public Map<String, Object> criarRespostaProduto(Produto produto) {
    Map<String, Object> produtoResponse = new HashMap<>();
    produtoResponse.put("id", produto.getId());
    produtoResponse.put("nome", produto.getNome());
    produtoResponse.put("descricao", produto.getDescricao());
    produtoResponse.put("preco", produto.getPreco());
    produtoResponse.put("precoPromocao", produto.getPrecoPromocao());
    produtoResponse.put("codigoBarras", produto.getCodigoBarras());
    produtoResponse.put("estoque", produto.getEstoque());
    produtoResponse.put("ativo", produto.isAtivo());
    produtoResponse.put(
        "unidadeMedida",
        produto.getUnidadeMedida() != null ? produto.getUnidadeMedida().getCodigo() : null);

    // Categoria
    if (produto.getCategoria() != null) {
      Map<String, Object> categoriaResponse = new HashMap<>();
      categoriaResponse.put("id", produto.getCategoria().getId());
      categoriaResponse.put("nome", produto.getCategoria().getNome());
      produtoResponse.put("categoria", categoriaResponse);
    }

    return produtoResponse;
  }

  @Transactional(rollbackFor = Exception.class)
  public Map<String, Object> buildImport(@Valid List<ProdutoDTO> produtoDTOs) {
      System.out.println("entrou no ADM Importação");
      Map<String, Object> response = new HashMap<>();
      try {
        setupDirectories();
        ResultadoOperacao resultado;
        if (!produtoDTOs.isEmpty()) {
          resultado = tratarImportacaoTxt(produtoDTOs, response);

          // Chamar método transacional do service
          if (resultado.isSucesso()) {
            // ✅ COMMIT AUTOMÁTICO - Transação foi bem-sucedida
            response.put(SUCCESS, true);
            response.put(
                    MESSAGE, "Produto " + resultado.getOperacao().toLowerCase() + " com sucesso!");
            response.put("operacao", resultado.getOperacao());
            response.put(TRANSACAO, "COMMIT");
            response.put(PRODUTO, criarRespostaProduto(resultado.getProduto()));

            log.info("✅ COMMIT executado - Produto ");

            log.info("*** Recarregando cache Banco de Dados ***");
            recarregarCache();
          } else {
            // ❌ Erro controlado
            response.put(SUCCESS, false);
            response.put(MESSAGE, resultado.getErro());
            response.put(TRANSACAO, ROLLBACK);
          }
        } else {
          response.put(SUCCESS, true);
          response.put(MESSAGE, "Sem Produto para carregar!");
          response.put("operacao", " N/A ");
          response.put(TRANSACAO, "COMMIT");
          response.put(PRODUTO, criarRespostaProduto(new Produto()));
        }
        return response;
      } catch (IllegalArgumentException e) {
        // ❌ ROLLBACK AUTOMÁTICO - Erro de validação
        System.err.println("⚠️ ROLLBACK executado - Erro de validação: " + e.getMessage());

        response.put(SUCCESS, false);
        response.put(MESSAGE, "Erro de validação: " + e.getMessage());
        response.put(TRANSACAO, ROLLBACK);
        response.put(TIPO_ERRO, "VALIDACAO");

        return response;

      } catch (RuntimeException e) {
        // ❌ ROLLBACK AUTOMÁTICO - Erro de negócio/banco
        System.err.println("💥 ROLLBACK executado - Erro de sistema: " + e.getMessage());
        e.printStackTrace();

        response.put(SUCCESS, false);
        response.put(MESSAGE, "Erro no processamento: " + e.getMessage());
        response.put(TRANSACAO, ROLLBACK);
        response.put(TIPO_ERRO, "SISTEMA");

        return response;

      } catch (Exception e) {
        // ❌ ROLLBACK AUTOMÁTICO - Erro inesperado
        System.err.println("🚨 ROLLBACK executado - Erro inesperado: " + e.getMessage());
        e.printStackTrace();

        response.put(SUCCESS, false);
        response.put(MESSAGE, "Erro interno do servidor");
        response.put(TRANSACAO, ROLLBACK);
        response.put(TIPO_ERRO, "INESPERADO");

        return response;
      }
  }


  // Classe para resultado da operação
  public static class ResultadoOperacao {
    private boolean sucesso;
    private String operacao;
    private Produto produto;
    private String erro;

    public ResultadoOperacao() {}

    public ResultadoOperacao(boolean sucesso, String operacao, Produto produto, String erro) {
      this.sucesso = sucesso;
      this.operacao = operacao;
      this.produto = produto;
      this.erro = erro;
    }

    // Getters
    public boolean isSucesso() { return sucesso; }
    public String getOperacao() { return operacao; }
    public Produto getProduto() { return produto; }
    public String getErro() { return erro; }
  }

  public Optional<Produto> findById(Long id) {
    return produtoRepository.findById(id);
  }

  public Optional<Produto> findByCdTxtimport(Long cdTxtimport) {
    return produtoRepository.findByCdTxtimport(cdTxtimport);
  }

  private static ProdutoRemote builderProdutoRemoteVazio(Terminal terminal) {
    return ProdutoRemote.builder()
            .id(1L)
            .nome("")
            .descricao("")
            .preco(BigDecimal.ZERO)
            .categoria("")
            .imagem("")
            .precoPromocao(BigDecimal.ZERO)
            .quantidade_estoque(1)
            .disponivel(true)
            .conteudo(false)
            .abreviacaoUM("")
            .descricaoCategoria("")
            .conteudo(false)
            .build();
  }

  private static List<ProdutoRemote> getProdutoRemotes(List<Produto> produtos) {
    List<ProdutoRemote> produtosRemote;
    produtosRemote = produtos.stream()
            .map(
                    produto ->
                            ProdutoRemote.builder()
                                    .id(produto.getId())
                                    .nome(produto.getNome())
                                    .descricao(produto.getDescricao())
                                    .preco(produto.getPreco())
                                    .categoria(
                                            produto.getCategoria() != null ? produto.getCategoria().getNome() : "")
                                    .imagem(produto.getImagem())
                                    .precoPromocao(produto.getPrecoPromocao())
                                    .quantidade_estoque(produto.getEstoque())
                                    .disponivel(produto.isAtivo())
                                    .abreviacaoUM(produto.getUnidadeMedida().getAbreviacao() != null ? produto.getUnidadeMedida().getAbreviacao() : "")
                                    .descricaoCategoria(
                                            produto.getCategoria() != null
                                                    ? produto.getCategoria().getNomeExibicao()
                                                    : "")
                                    .tipoConteudo(TCTABELAPRECO)
                                    .build())
            .toList();
    return produtosRemote;
  }

  private static <T> List<T> nullSafe(List<T> list) {
    return list == null ? List.of() : list;
  }

  private void setupDirectories() throws IOException {
    Path inputDir = Paths.get(config.getInputDirectory());
    Path outputDir = Paths.get(config.getOutputDirectory());

    Files.createDirectories(inputDir);
    Files.createDirectories(outputDir);

    log.info("Diretórios configurados - Input: {}, Output: {}", inputDir, outputDir);
  }
}
