package com.luisrobbo.cursomc.resources;

import com.luisrobbo.cursomc.domain.Pedido;
import com.luisrobbo.cursomc.dto.CategoriaDTO;
import com.luisrobbo.cursomc.services.PedidoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;

@RestController
@RequestMapping(value = "/pedidos")
public class PedidoResource {

    @Autowired
    private PedidoService service;

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<Pedido> find(@PathVariable Integer id) {
        Pedido obj = service.find(id);
        return ResponseEntity.ok().body(obj);
    }

    @PostMapping
    public ResponseEntity<Void> insert(@Valid @RequestBody Pedido pedido) {
        service.insert(pedido);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(pedido.getId()).toUri();

        return ResponseEntity.created(uri).build();
    }
}
