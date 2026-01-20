package unioeste.geral.endereco.dao;

import unioeste.geral.endereco.bo.*;

import java.util.List;

public interface EnderecoDAO {
    //Cadastrar Endereço
    Pessoa cadastrarPessoa(Pessoa obj, List<String> email, List<String> fone) throws Exception;

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

    //Buscar serviços da OS
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
    List<Endereco> obterEnderecoPorCep(String cep);

    //Obter por ID
    Endereco obterEnderecoPorID(int id);

    //Obter Cidade
    Cidade obterCidadePorId(int idCidade);

    //Obter ViaCEP
    Endereco obterEnderecoPorCepViaCEP(String cep);
}











