package unioeste.geral.endereco.manager;

import unioeste.geral.endereco.bo.*;
import unioeste.geral.endereco.exception.EnderecoException;
import java.util.List;

public interface UCEnderecoGeralServicos {
    //Cadastrar Endereço
    Pessoa cadastrarPessoa(Pessoa obj, List<String> email, List<String> fone, String user) throws EnderecoException, Exception;

    //Consultar Pessoa
    Pessoa obterPessoaPorCPF(String cpf, String usuario) throws Exception;

    //Consultar Pessoa
    Pessoa obterPessoaPorID(String id, String user) throws Exception;

    //Obter Telefones
    List<Object> obterTelefonesCompletos(int idPessoa, String user) throws Exception;

    //Obter Emails
    List<Object> obterEmailsCompletos(int idPessoa, String user) throws Exception;

    //Buscar Serviços
    Servico buscarServico(String codServico) throws Exception;

    //Cadastrar Ordem de Serviço
    void registrarOS(OrdemServico os, List<ServicoOS> itens) throws Exception;

    //Buscar Ordem de Serviço
    OrdemServico buscarOrdemServico(String buscaOS) throws Exception;

    //Buscar Serviços da OS
    List<ServicoOS> buscarServicoOS(String buscaOS) throws Exception;

    //Buscar código CID
    CID buscarCID(String codCid) throws Exception;

    //Registrar receita médica
    void registrarReceita(ReceitaMedica rm, List<Prescricao> itens) throws Exception;

    //Buscar receita médica
    ReceitaMedica buscarReceitaCompleta(String nroRM) throws Exception;

    //Buscar as prescrições da receita médica
    List<Prescricao> listarPrescricoes(String nroRM) throws Exception;

    //Obter por CEP
    List<Endereco> obterEnderecoPorCep(String cep) throws EnderecoException;

    //Obter por ID
    Endereco obterEnderecoPorID(int id) throws EnderecoException;

    //Obter Cidade
    Cidade obterCidadePorId(int idCidade) throws EnderecoException;

    //Obter ViaCEP
    Endereco obterEnderecoPorCepViaCEP(String cep);
}


