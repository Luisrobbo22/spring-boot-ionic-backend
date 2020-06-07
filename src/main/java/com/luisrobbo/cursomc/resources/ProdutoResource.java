package com.luisrobbo.cursomc.resources;

import com.luisrobbo.cursomc.domain.Produto;
import com.luisrobbo.cursomc.dto.ProdutoDTO;
import com.luisrobbo.cursomc.resources.utils.URL;
import com.luisrobbo.cursomc.services.ProdutoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value="/produtos")
public class ProdutoResource {
	
	@Autowired
	private ProdutoService service;
	
	@RequestMapping(value="/{id}", method=RequestMethod.GET)
	public ResponseEntity<Produto> find(@PathVariable Integer id) {
		Produto produto = service.find(id);
		return ResponseEntity.ok().body(produto);
	}

	@GetMapping(value = "/")
	public ResponseEntity<List<Produto>> findAll(){
		List<Produto> produtos = service.findAll();
		return ResponseEntity.ok().body(produtos);
	}

	@GetMapping()
	public ResponseEntity<Page<ProdutoDTO>> findPage(
			@RequestParam(value = "nome", defaultValue = "") String nome,
			@RequestParam(value = "categorias", defaultValue = "") String categorias,
			@RequestParam(value = "page", defaultValue = "0") Integer page,
			@RequestParam(value = "linesPerPage", defaultValue = "24") Integer linesPerPage,
			@RequestParam(value = "orderBy", defaultValue = "nome") String orderBy,
			@RequestParam(value = "direction", defaultValue = "ASC") String direction) {
		String nomeDecoded = URL.decodeParam(nome);
		List<Integer> ids = URL.decodeIntList(categorias);
		Page<Produto> produtos = service.search(nomeDecoded, ids, page, linesPerPage, orderBy, direction);
		Page<ProdutoDTO> listDTO = produtos.map(prod -> new ProdutoDTO(prod));
		return ResponseEntity.ok().body(listDTO);
	}

}
