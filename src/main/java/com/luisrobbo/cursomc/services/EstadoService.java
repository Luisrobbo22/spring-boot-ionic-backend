package com.luisrobbo.cursomc.services;

import com.luisrobbo.cursomc.domain.Estado;
import com.luisrobbo.cursomc.repositories.EstadoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EstadoService {

    @Autowired
    private EstadoRepository repository;

    public List<Estado> findAll(){
        return repository.findAllByOrderByNome();
    }
}
