package com.luisrobbo.cursomc.services.validation;


import com.luisrobbo.cursomc.domain.Cliente;
import com.luisrobbo.cursomc.dto.ClienteDTO;
import com.luisrobbo.cursomc.repositories.ClienteRepository;
import com.luisrobbo.cursomc.resources.exception.FieldMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerMapping;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ClienteUpdateValidator implements ConstraintValidator<ClienteUpdate, ClienteDTO> {

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private ClienteRepository repository;

    @Override
    public void initialize(ClienteUpdate ann) {
    }

    @Override
    public boolean isValid(ClienteDTO objDto, ConstraintValidatorContext context) {

        // Pega o atributo na URI, nesse caso o ID do cliente
        Map<String, String> map = (Map<String, String>) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
        Integer uriId = Integer.valueOf(map.get("id"));

        List<FieldMessage> list = new ArrayList<>();

        Cliente cliente = repository.findByEmail(objDto.getEmail());
        if (cliente != null && !cliente.getId().equals(uriId)){
            list.add(new FieldMessage("email", "Email já existente"));
        }


        for (FieldMessage e : list) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(e.getMessage())
                    .addPropertyNode(e.getFieldName()).addConstraintViolation();
        }
        return list.isEmpty();
    }
}
