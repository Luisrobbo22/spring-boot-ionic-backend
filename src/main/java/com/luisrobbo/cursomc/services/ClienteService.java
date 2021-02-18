package com.luisrobbo.cursomc.services;

import com.luisrobbo.cursomc.domain.Cidade;
import com.luisrobbo.cursomc.domain.Cliente;
import com.luisrobbo.cursomc.domain.Endereco;
import com.luisrobbo.cursomc.domain.enums.Perfil;
import com.luisrobbo.cursomc.domain.enums.TipoCliente;
import com.luisrobbo.cursomc.dto.ClienteDTO;
import com.luisrobbo.cursomc.dto.ClienteNewDTO;
import com.luisrobbo.cursomc.repositories.ClienteRepository;
import com.luisrobbo.cursomc.repositories.EnderecoRepository;
import com.luisrobbo.cursomc.security.UserSS;
import com.luisrobbo.cursomc.services.exceptions.AuthorizationException;
import com.luisrobbo.cursomc.services.exceptions.DataIntegretyException;
import com.luisrobbo.cursomc.services.exceptions.ObjectNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.awt.image.BufferedImage;
import java.net.URI;
import java.util.List;
import java.util.Optional;

@Service
public class ClienteService {

    @Autowired
    private ClienteRepository repo;

    @Autowired
    private EnderecoRepository enderecoRepository;

    @Autowired
    private BCryptPasswordEncoder pe;

    @Autowired
    private S3Service s3Service;

    @Autowired
    private ImageService imageService;

    @Value("${img.prefix.client.profile}")
    private String prefix;

    @Value("${img.profile.size}")
    private Integer size;

    public Cliente find(Integer id) {

        UserSS user = UserService.authenticated();
        if (user == null || !user.hasRole(Perfil.ADMIN) && !id.equals(user.getId())) {
            throw new AuthorizationException("Acesso negado");
        }

        Optional<Cliente> obj = repo.findById(id);
        return obj.orElseThrow(() -> new ObjectNotFoundException(
                "Objeto não encontrado! Id: " + id + ", Tipo: " + Cliente.class.getName()));
    }

    public Cliente update(Cliente cliente) {
        Cliente newCliente = find(cliente.getId());
        updateData(newCliente, cliente);
        return repo.save(newCliente);
    }

    public void delete(Integer id) {
        find(id);
        try {
            repo.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            throw new DataIntegretyException("Não é possível deletar porque há pedidos relacionadas");
        }
    }

    public List<Cliente> findAll() {
        return repo.findAll();
    }


    public Cliente findByEmail(String email) {
        UserSS user = UserService.authenticated();
        if (user == null || user.hasRole(Perfil.ADMIN) && !email.equals(user.getUsername())) {
            throw new AuthorizationException("Acesso negado");
        }

        Cliente cliente = repo.findByEmail(email);
        if (cliente == null) {
            throw new ObjectNotFoundException("Cliente não encontrado! ID: " + user.getId()
                    + ", Tipo: " + Cliente.class.getName());
        }
        return cliente;
    }

    public Page<Cliente> findPage(Integer page, Integer linesPerPage, String orderBy, String direction) {
        PageRequest pageRequest = PageRequest.of(page, linesPerPage, Sort.Direction.valueOf(direction), orderBy);
        return repo.findAll(pageRequest);
    }

    public Cliente fromDTO(ClienteDTO clienteDTO) {
        return new Cliente(clienteDTO.getId(), clienteDTO.getNome(), clienteDTO.getEmail(), null, null, null);
    }

    public Cliente fromDTO(ClienteNewDTO clienteNewDTO) {
        Cliente cliente = new Cliente(null, clienteNewDTO.getNome(), clienteNewDTO.getEmail(),
                clienteNewDTO.getCpfOuCnpj(), TipoCliente.toEnum(clienteNewDTO.getTipo()), pe.encode(clienteNewDTO.getSenha()));

        Cidade cidade = new Cidade(clienteNewDTO.getCidadeId(), null, null);

        Endereco endereco = new Endereco(null, clienteNewDTO.getLogradouro(),
                clienteNewDTO.getNumero(), clienteNewDTO.getComplemento(), clienteNewDTO.getBairro(),
                clienteNewDTO.getCep(), cliente, cidade);

        cliente.getEnderecos().add(endereco);
        cliente.getTelefones().add(clienteNewDTO.getTelefone1());
        if (clienteNewDTO.getTelefone2() != null) {
            cliente.getTelefones().add(clienteNewDTO.getTelefone2());
        }
        if (clienteNewDTO.getTelefone3() != null) {
            cliente.getTelefones().add(clienteNewDTO.getTelefone3());
        }

        return cliente;
    }

    private void updateData(Cliente newCliente, Cliente cliente) {
        newCliente.setNome(cliente.getNome());
        newCliente.setEmail(cliente.getEmail());
    }

    @Transactional
    public Cliente insert(Cliente cliente) {
        cliente.setId(null);
        cliente = repo.save(cliente);
        enderecoRepository.saveAll(cliente.getEnderecos());
        return cliente;
    }

    public URI uploadProfilePicture(MultipartFile multipartFile) {

        UserSS user = UserService.authenticated();
        if (user == null) {
            throw new AuthorizationException("Acesso Negado");
        }

        BufferedImage jpgImage = imageService.getJpgImageFromFile(multipartFile);

        // deixa a imagem quadrada
        jpgImage = imageService.cropSquare(jpgImage);

        // redefine o tamanho
        jpgImage = imageService.resize(jpgImage, size);

        String fileName = prefix + user.getId() + "jpg";

        return s3Service.uploadFile(imageService.getInputStream(jpgImage, "jpg"), fileName, "image");

    }


}
