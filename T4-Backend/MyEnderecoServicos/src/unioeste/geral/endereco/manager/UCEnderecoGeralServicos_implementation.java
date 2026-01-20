package unioeste.geral.endereco.manager;
import unioeste.geral.endereco.bo.*;
import unioeste.geral.endereco.dao.EnderecoDAO;
import unioeste.geral.endereco.dao.EnderecoDAO_implementation;
import unioeste.geral.endereco.exception.EnderecoException;

import java.util.List;

public class UCEnderecoGeralServicos_implementation implements UCEnderecoGeralServicos {
    private EnderecoDAO enderecoDAO = new EnderecoDAO_implementation();

    //Cadastro e Consulta de Pessoa

    @Override public Pessoa cadastrarPessoa(Pessoa obj, List<String> emails, List<String> telefones, String user) throws Exception {
        if (obj == null) {
            throw new Exception("Objeto Pessoa não pode ser nulo!");
        }
        if (emails == null || emails.isEmpty()) {
            throw new Exception("Necessário informar ao menos um email!");
        }
        for (String email : emails) {
            if (email == null || email.trim().isEmpty()) {
                throw new Exception("Email inválido na lista!");
            }
        }
        if (telefones == null || telefones.isEmpty()) {
            throw new Exception("Necessário informar ao menos um telefone!");
        }
        Pessoa existente = enderecoDAO.obterPessoaPorCPF(obj.getCPF(), user);
        if (existente != null) {
            throw new Exception("Erro: Já existe uma pessoa cadastrada com o CPF " + obj.getCPF());
        }

        return enderecoDAO.cadastrarPessoa(obj, emails, telefones);
    }

    @Override public Pessoa obterPessoaPorCPF(String cpf, String usuario) throws Exception{
        if (cpf == null || cpf.length() != 11) {
            throw new EnderecoException("Formato de CPF inválido! Obrigatório ter 11 dígitos.");
        }
        return enderecoDAO.obterPessoaPorCPF(cpf, usuario);
    }

    @Override public List<Object> obterTelefonesCompletos(int idPessoa, String user) throws Exception{
        if (idPessoa <=0 || user == null){
            throw new EnderecoException("ID inválido ou usuário nulo.");
        }
        return enderecoDAO.obterTelefonesCompletos(idPessoa, user);
    }

    @Override public List<Object> obterEmailsCompletos(int idPessoa, String user) throws Exception{
        if (idPessoa <=0 || user == null){
            throw new EnderecoException("ID inválido ou usuário nulo.");
        }
        return enderecoDAO.obterEmailsCompletos(idPessoa, user);
    }

    @Override public Pessoa obterPessoaPorID(String id, String user) throws Exception{
        if (id == null || user == null) {
            throw new EnderecoException("Valor nulo para id ou usuário.");
        }
        return enderecoDAO.obterPessoaPorID(id, user);
    }

    //Cadastro e Consulta dos Serviços

    @Override public Servico buscarServico(String codServico) throws Exception{
        if (codServico == null) {
            throw new EnderecoException("Código do serviço nulo.");
        }
        return enderecoDAO.buscarServico(codServico);
    }

    @Override public void registrarOS(OrdemServico os, List<ServicoOS> itens) throws Exception{
        if (os == null) throw new Exception("Objeto Ordem de Serviço não pode ser nulo!");
        if (itens == null) throw new Exception("Objeto Serviço não pode ser nulo!");

        OrdemServico existente = enderecoDAO.buscarOrdemServico(String.valueOf(os.getNroOrdemServico()));
        if (existente != null) {
            throw new Exception("Erro: Duplicação do Nº " + os.getNroOrdemServico());
        }
        enderecoDAO.registrarOS(os, itens);
    }

    @Override public OrdemServico buscarOrdemServico(String buscaOS) throws Exception{
        if (buscaOS == null) {
            throw new EnderecoException("Número de busca nulo para Ordem de Serviço.");
        }
        return enderecoDAO.buscarOrdemServico(buscaOS);
    }

    @Override public List<ServicoOS> buscarServicoOS(String buscaOS) throws Exception{
        if (buscaOS == null) {
            throw new EnderecoException("Número de busca nulo para Ordem de Serviço.");
        }
        return enderecoDAO.buscarServicoOS(buscaOS);
    }

    @Override public CID buscarCID(String codCid) throws Exception{
        if (codCid == null) {
            throw new EnderecoException("Código CID nulo.");
        }
        return enderecoDAO.buscarCID(codCid);
    }

    @Override public void registrarReceita(ReceitaMedica rm, List<Prescricao> itens) throws Exception{
        if (rm == null) throw new Exception("Objeto Receita Médica não pode ser nulo!");
        if (itens == null) throw new Exception("Objeto Prescrição não pode ser nulo!");

        ReceitaMedica existente = enderecoDAO.buscarReceitaCompleta(String.valueOf(rm.getNroReceita()));
        if (existente != null) {
            throw new Exception("Erro: Duplicação do Nº " + rm.getNroReceita());
        }
        enderecoDAO.registrarReceita(rm, itens);
    }

    @Override public ReceitaMedica buscarReceitaCompleta(String nroRM) throws Exception{
        if (nroRM == null) {
            throw new EnderecoException("Número de busca nulo para Receita Médica.");
        }
        return enderecoDAO.buscarReceitaCompleta(nroRM);
    }

    @Override public List<Prescricao> listarPrescricoes(String nroRM) throws Exception{
        if (nroRM == null) {
            throw new EnderecoException("Número de busca nulo para Receita Médica.");
        }
        return enderecoDAO.listarPrescricoes(nroRM);
    }

    //Consulta de Endereço

    @Override public List<Endereco> obterEnderecoPorCep(String cep) throws EnderecoException {
        if (cep == null || cep.length() != 8) {
            throw new EnderecoException("Formato de CEP inválido! Obrigatório ter 8 dígitos.");
        }
        List<Endereco> listaEnderecos = enderecoDAO.obterEnderecoPorCep(cep);
        if (listaEnderecos.isEmpty()) {
            throw new EnderecoException("Endereço não encontrado no banco de dados.");
        }
        return listaEnderecos;
    }

    @Override public Endereco obterEnderecoPorID(int id) throws EnderecoException {
        if (id <= 0) {
            throw new EnderecoException("ID inválido! Obrigatório ser positivo.");
        }
        return enderecoDAO.obterEnderecoPorID(id);
    }

    @Override public Cidade obterCidadePorId(int idCidade) throws EnderecoException {
        if (idCidade <= 0) {
            throw new EnderecoException("ID inválido! Obrigatório ser positivo.");
        }
        return enderecoDAO.obterCidadePorId(idCidade);
    }

    @Override public Endereco obterEnderecoPorCepViaCEP(String cep) throws EnderecoException {
        if (cep == null || cep.replaceAll("[^0-9]", "").length() != 8) {
            throw new EnderecoException("Formato de CEP inválido para busca ViaCEP. Obrigatório ter 8 dígitos.");
        }
        Endereco endereco = enderecoDAO.obterEnderecoPorCepViaCEP(cep);
        if (endereco == null) {
            throw new EnderecoException("CEP não encontrado na base externa (ViaCEP).");
        }
        return endereco;
    }
}