package com.FnxLucas.EventosBuffetAPI.Service;

import com.FnxLucas.EventosBuffetAPI.Model.Endereco;
import org.springframework.stereotype.Service;

@Service
public class EnderecoService {

    public void validarEndereco(Endereco enderecoRequest){
        if (enderecoRequest == null) {
            throw new IllegalArgumentException("Erro: Endereço não pode ser nulo");
        }
        validarCidade(enderecoRequest);
        validarRua(enderecoRequest);
        validarNumero(enderecoRequest);
    }

    public void validarCidade(Endereco enderecoRequest){
        if (enderecoRequest.getCidade() == null || enderecoRequest.getCidade().isBlank()){
            throw new IllegalArgumentException("Erro: Cidade não preenchida");
        }
    }

    public void validarRua(Endereco enderecoRequest){
        if (enderecoRequest.getRua() == null || enderecoRequest.getRua().isBlank()){
            throw new IllegalArgumentException("Erro: Rua não preenchida");
        }
    }

    public void validarNumero(Endereco enderecoRequest){
        if (enderecoRequest.getNumero() == null || enderecoRequest.getNumero().isBlank()){
            throw new IllegalArgumentException("Erro: Número não preenchido");
        }
    }

}
