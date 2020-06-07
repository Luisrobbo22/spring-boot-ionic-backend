package com.luisrobbo.cursomc.services;

import com.luisrobbo.cursomc.domain.Categoria;
import com.luisrobbo.cursomc.domain.Pedido;
import com.luisrobbo.cursomc.domain.Produto;
import com.luisrobbo.cursomc.repositories.CategoriaRepository;
import com.luisrobbo.cursomc.repositories.ProdutoRepository;
import com.luisrobbo.cursomc.services.exceptions.ObjectNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProdutoService {

    @Autowired
    private ProdutoRepository repository;

    @Autowired
    private CategoriaRepository categoriaRepository;


    public Produto find(Integer id) {
        Optional<Produto> obj = repository.findById(id);
        return obj.orElseThrow(() -> new ObjectNotFoundException(
                "Objeto não encontrado! Id: " + id + ", Tipo: " + Produto.class.getName()));
    }
    public List<Produto> findAll(){
        Optional<List<Produto>> produtos  = Optional.of(repository.findAll());

        return produtos.orElseThrow(() -> new ObjectNotFoundException(
                "Produtos não encontrado! " + Produto.class.getName()));
    }

    public Page<Produto> search(String nome, List<Integer> ids,
                                Integer page, Integer linesPerPage,
                                String orderBy, String direction) {

        PageRequest pageRequest = PageRequest.of(page, linesPerPage, Sort.Direction.valueOf(direction), orderBy);
        List<Categoria> categorias = categoriaRepository.findAllById(ids);
        return repository.findDistinctByNomeContainingAndCategoriasIn(nome, categorias, pageRequest);
    }


}
