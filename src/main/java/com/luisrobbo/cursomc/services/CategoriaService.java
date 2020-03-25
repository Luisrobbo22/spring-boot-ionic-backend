package com.luisrobbo.cursomc.services;

import java.util.List;
import java.util.Optional;

import com.luisrobbo.cursomc.domain.Categoria;
import com.luisrobbo.cursomc.repositories.CategoriaRepository;
import com.luisrobbo.cursomc.services.exceptions.DataIntegretyException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import com.luisrobbo.cursomc.services.exceptions.ObjectNotFoundException;

@Service
public class CategoriaService {

    @Autowired
    private CategoriaRepository repo;

    public Categoria find(Integer id) {
        Optional<Categoria> obj = repo.findById(id);
        return obj.orElseThrow(() -> new ObjectNotFoundException(
                "Objeto não encontrado! Id: " + id + ", Tipo: " + Categoria.class.getName()));
    }

    public Categoria insert(Categoria categoria) {
        categoria.setId(null);
        return repo.save(categoria);
    }

    public Categoria update(Categoria categoria) {
        find(categoria.getId());
        return repo.save(categoria);
    }
    public void delete(Integer id) {
        find(id);
        try {
            repo.deleteById(id);
        } catch (DataIntegrityViolationException e){
            throw new DataIntegretyException("Não é possível deletar uma categoria com produtos");
        }
    }

    public List<Categoria> findAll(){
        return repo.findAll();
    }

}

