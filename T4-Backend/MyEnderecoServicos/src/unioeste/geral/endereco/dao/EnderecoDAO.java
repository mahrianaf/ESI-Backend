package unioeste.geral.endereco.dao;

import unioeste.geral.endereco.bo.*;

import java.util.List;

public interface EnderecoDAO {
    //Cadastrar Endereço
    Pessoa cadastrarPessoa(Pessoa obj, List<String> email, List<String> fone) throws Exception;

    //Consultar Pessoa
    Pessoa obterPessoaPorCPF(String cpf, String usuario) throws Exception;

    //Obter por CEP
    List<Endereco> obterEnderecoPorCep(String cep);

    //Obter por ID
    Endereco obterEnderecoPorID(int id);

    //Obter Cidade
    Cidade obterCidadePorId(int idCidade);

    //Obter ViaCEP
    Endereco obterEnderecoPorCepViaCEP(String cep);
}











