package unioeste.geral.endereco.manager;
import unioeste.geral.endereco.bo.*;
import unioeste.geral.endereco.dao.EnderecoDAO;
import unioeste.geral.endereco.dao.EnderecoDAO_implementation;
import unioeste.geral.endereco.exception.EnderecoException;

import java.util.List;

public class UCEnderecoGeralServicos_implementation implements UCEnderecoGeralServicos {
    private EnderecoDAO enderecoDAO = new EnderecoDAO_implementation();

    @Override public Pessoa cadastrarPessoa(Pessoa obj, List<String> emails, List<String> telefones) throws Exception {
        if (obj == null) throw new Exception("Objeto Pessoa não pode ser nulo!");
        if (obj.getCPF() == null || obj.getCPF().trim().length() != 11) throw new Exception("CPF inválido!");
        if (obj.getEndereco() == null) throw new Exception("Dados de endereço são obrigatórios!");

        return enderecoDAO.cadastrarPessoa(obj, emails, telefones);
    }

    @Override public Pessoa obterPessoaPorCPF(String cpf, String usuario) throws Exception{
        if (cpf == null || cpf.length() != 11) {
            throw new EnderecoException("Formato de CPF inválido! Obrigatório ter 11 dígitos.");
        }
        return enderecoDAO.obterPessoaPorCPF(cpf, usuario);
    }

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