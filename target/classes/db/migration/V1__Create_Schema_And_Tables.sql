-- Criar schema
CREATE SCHEMA IF NOT EXISTS schemamercado;
GRANT USAGE ON SCHEMA schemamercado TO PUBLIC;

-- Tabela de categorias
CREATE TABLE schemamercado.categorias
(
    id        BIGSERIAL PRIMARY KEY,
    nome      VARCHAR(50)  NOT NULL,
    descricao VARCHAR(100) NOT NULL,
    nome_exibicao VARCHAR(50) NOT NULL);

-- Tabela de produtos
CREATE TABLE schemamercado.produtos
(
    id             BIGSERIAL PRIMARY KEY,
    nome           VARCHAR(100)   NOT NULL,
    descricao      VARCHAR(100)   NOT NULL,
    preco          DECIMAL(10, 2) NOT NULL,
    preco_promocao DECIMAL(10, 2) DEFAULT 0.0,
    codigo_barras  VARCHAR(20)    DEFAULT '',
    estoque        INTEGER        DEFAULT 0,
    importado      BOOLEAN        DEFAULT true,
    ativo          BOOLEAN        DEFAULT true,
    unidade_medida VARCHAR(1)     DEFAULT 'K',
    categoria_id   BIGINT         NULL,
    imagem         VARCHAR(3000)  DEFAULT '',
    cd_txtimport   BIGINT         DEFAULT 0,
CONSTRAINT produtos_categoria_fk FOREIGN KEY (categoria_id) REFERENCES schemamercado.categorias (id)
);

-- Tabela de terminais
CREATE TABLE schemamercado.terminais
(
    id           BIGSERIAL PRIMARY KEY,
    categoria_id BIGINT       NOT NULL,
    nr_terminal  INTEGER      NOT NULL,
    nome         VARCHAR(50)  NOT NULL,
    localizacao  VARCHAR(100) NOT NULL,
    url          VARCHAR(150),
    ativo        BOOLEAN DEFAULT true,
    CONSTRAINT terminais_pk UNIQUE (nr_terminal, categoria_id),
    CONSTRAINT terminais_categoria_fk FOREIGN KEY (categoria_id) REFERENCES schemamercado.categorias (id)
);

-- Tabela de usuarios
CREATE TABLE schemamercado.usuarios
(
    id       BIGSERIAL PRIMARY KEY,
    username VARCHAR(10)                 NOT NULL,
    email    VARCHAR(100)                NOT NULL,
    password VARCHAR(100)                NOT NULL,
    role     VARCHAR(100) DEFAULT 'ROLE' NOT NULL
);

-- Tabela de visualizacoes
CREATE TABLE schemamercado.visualizacoes
(
    id          BIGSERIAL PRIMARY KEY,
    terminal_id BIGINT       NOT NULL,
    produto_id  BIGINT       NOT NULL,
    data_hora   TIMESTAMP    NOT NULL,
    ip          VARCHAR(100) NOT NULL
);

CREATE TABLE schemamercado.conteudos (
             id bigserial NOT NULL,
             tipo_conteudo INTEGER NOT NULL,
             titulo varchar(100) NOT NULL,
             descricao varchar(100) NOT NULL,
             nome_midia varchar(100),
             CONSTRAINT conteudos_pk PRIMARY KEY (id)
            );

CREATE TABLE schemamercado.terminal_conteudo (
         id bigserial NOT NULL,
         terminal_id BIGINT NOT NULL,
         conteudo_id BIGINT NOT NULL,
         CONSTRAINT terminal_conteudo_pk PRIMARY KEY (id),
         CONSTRAINT terminal_conteudo_terminais_fk FOREIGN KEY (terminal_id) REFERENCES schemamercado.terminais (id) ON DELETE CASCADE ON UPDATE CASCADE,
         CONSTRAINT terminal_conteudo_conteudos_fk FOREIGN KEY (conteudo_id) REFERENCES schemamercado.conteudos (id) ON DELETE CASCADE ON UPDATE CASCADE
);

-- Tabela de agendamentos
CREATE TABLE schemamercado.agendamentos
(
    id               BIGSERIAL PRIMARY KEY,
    titulo           VARCHAR(100) NOT NULL,
    descricao        VARCHAR(100) NOT NULL,
    data_hora_inicio TIMESTAMP,
    data_hora_fim    TIMESTAMP    NOT NULL,
    ativo            BOOLEAN DEFAULT true,
    terminal_conteudo_id BIGINT   NOT NULL,
    usuario_id       BIGINT       NOT NULL,
    CONSTRAINT agendamentos_terminal_conteudo_fk FOREIGN KEY (terminal_conteudo_id) REFERENCES schemamercado.terminal_conteudo (id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT agendamentos_usuarios_fk FOREIGN KEY (usuario_id) REFERENCES schemamercado.usuarios (id) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE schemamercado.agendamento_logs (
                                                id BIGSERIAL PRIMARY KEY,
                                                agendamento_id BIGINT NOT NULL,
                                                data_execucao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                                status_execucao VARCHAR(20) NOT NULL,
                                                url_chamada VARCHAR(500),
                                                resposta_http INT,
                                                mensagem_erro TEXT,
                                                tempo_execucao_ms BIGINT,
                                                FOREIGN KEY (agendamento_id) REFERENCES schemamercado.agendamentos(id)
);

CREATE INDEX idx_agendamento_logs_data ON schemamercado.agendamento_logs(data_execucao);
CREATE INDEX idx_agendamento_logs_status ON schemamercado.agendamento_logs(status_execucao);
CREATE INDEX idx_agendamento_logs_agendamento ON schemamercado.agendamento_logs(agendamento_id);

-- schemamercado.conteudos_tabela_preco definition
CREATE TABLE schemamercado.conteudos_tabela_preco (
          id bigserial NOT NULL,
          conteudo_id BIGINT,
          produto_id BIGINT,
          CONSTRAINT conteudos_tabela_preco_pk PRIMARY KEY (id),
          CONSTRAINT conteudos_tabela_preco_cont_fk FOREIGN KEY (conteudo_id) REFERENCES schemamercado.conteudos(id) ON DELETE CASCADE ON UPDATE CASCADE,
          CONSTRAINT conteudos_tabela_preco_produto_fk FOREIGN KEY (produto_id) REFERENCES schemamercado.produtos(id) ON DELETE CASCADE ON UPDATE CASCADE
);