package com.luisrobbo.cursomc.services;

import com.luisrobbo.cursomc.domain.Cidade;
import com.luisrobbo.cursomc.repositories.CidadeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CidadeService {

    @Autowired
    private CidadeRepository repository;

    public List<Cidade> findByEstado(Integer estadoId) {
        return repository.findCidades(estadoId);
    }

}
