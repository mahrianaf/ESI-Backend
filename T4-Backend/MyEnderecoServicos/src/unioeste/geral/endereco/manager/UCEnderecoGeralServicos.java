package unioeste.geral.endereco.manager;

import unioeste.geral.endereco.bo.*;
import unioeste.geral.endereco.exception.EnderecoException;
import java.util.List;

public interface UCEnderecoGeralServicos {
    //Cadastrar Endereço
    Pessoa cadastrarPessoa(Pessoa obj, List<String> email, List<String> fone) throws EnderecoException, Exception;

    //Consultar Pessoa
    Pessoa obterPessoaPorCPF(String cpf, String usuario) throws Exception;

    //Obter por CEP
    List<Endereco> obterEnderecoPorCep(String cep) throws EnderecoException;

    //Obter por ID
    Endereco obterEnderecoPorID(int id) throws EnderecoException;

    //Obter Cidade
    Cidade obterCidadePorId(int idCidade) throws EnderecoException;

    //Obter ViaCEP
    Endereco obterEnderecoPorCepViaCEP(String cep);
}


