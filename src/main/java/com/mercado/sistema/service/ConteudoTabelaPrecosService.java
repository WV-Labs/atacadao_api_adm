package com.mercado.sistema.service;

import com.mercado.sistema.dao.model.Conteudo;
import com.mercado.sistema.dao.model.ConteudoTabelaPreco;
import com.mercado.sistema.dao.repository.ConteudoTabelaPrecoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ConteudoTabelaPrecosService {

    @Autowired
    private ConteudoTabelaPrecoRepository conteudoTabelaPrecoRepository;

    public List<ConteudoTabelaPreco> getConteudoTabelaPrecos(Conteudo conteudo) {
        return conteudoTabelaPrecoRepository.findByConteudo(conteudo);

    }
}
