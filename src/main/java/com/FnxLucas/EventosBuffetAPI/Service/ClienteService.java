package com.FnxLucas.EventosBuffetAPI.Service;

import com.FnxLucas.EventosBuffetAPI.Model.Cliente;
import com.FnxLucas.EventosBuffetAPI.Repository.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ClienteService{

    @Autowired
    private EnderecoService enderecoService;

    @Autowired
    private ClienteRepository clienteRepository;


    public void validarClienteId(Long id){
        if(!clienteRepository.existsById(id)){
            throw new IllegalArgumentException("Erro: Cliente não encontrado");
        }
    }

    public void validarClienteNome(String nome){
        if( clienteRepository.findByNome(nome) == null){
            throw new IllegalArgumentException("Erro: Cliente não encontrado");
        }
    }

    public void validarCadastro(Cliente clienteRequest) {
        if (clienteRequest == null) {
            throw new IllegalArgumentException("Erro: Cliente não pode ser nulo");
        }
        validarNome(clienteRequest);
        validarCpfCnpj(clienteRequest);
        validarEmail(clienteRequest);
        if (clienteRequest.getEndereco() == null) {
            throw new IllegalArgumentException("Erro: Endereço não pode ser nulo");
        }
        enderecoService.validarEndereco(clienteRequest.getEndereco());
    }

    public void validarNome(Cliente clienteRequest){
        if (clienteRequest.getNome() == null || clienteRequest.getNome().isBlank()){
            throw new IllegalArgumentException("Erro: Nome não preenchido");
        }
    }

    public void validarCpfCnpj(Cliente clienteRequest){
        if (clienteRequest.getCpfCnpj() == null || clienteRequest.getCpfCnpj().isBlank()){
            throw new IllegalArgumentException("Erro: CPF ou CNPJ não preenchido");
        }
        String cleanCpfCnpj = clienteRequest.getCpfCnpj().replaceAll("\\D", "");
        if (cleanCpfCnpj.length() != 11 && cleanCpfCnpj.length() != 14){
            throw new IllegalArgumentException("Erro: CPF ou CNPJ inválido");
        }
    }

    public void validarEmail(Cliente clienteRequest){
        if(clienteRequest.getEmail() == null || clienteRequest.getEmail().isBlank()){
            throw new IllegalArgumentException("Erro: Email não preenchido");
        }
        if(!clienteRequest.getEmail().contains("@")){
            throw new IllegalArgumentException("Erro: Email inválido");
        }
    }
}
