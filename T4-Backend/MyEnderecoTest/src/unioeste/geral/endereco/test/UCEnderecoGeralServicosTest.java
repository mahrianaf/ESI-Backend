package unioeste.geral.endereco.test;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import unioeste.geral.endereco.bo.Bairro;
import unioeste.geral.endereco.bo.Endereco;
import unioeste.geral.endereco.bo.Cidade;
import unioeste.geral.endereco.bo.Logradouro;
import unioeste.geral.endereco.exception.EnderecoException;
import unioeste.geral.endereco.manager.UCEnderecoGeralServicos;
import unioeste.geral.endereco.manager.UCEnderecoGeralServicos_implementation;

import java.util.List;
/*
public class UCEnderecoGeralServicosTest {

    private UCEnderecoGeralServicos servico = new UCEnderecoGeralServicos_implementation();

    // CENÁRIOS DE TESTE: cadastrarEndereco
    @Test void CadastrarEndereco_Sucesso() throws EnderecoException {

        //Objetos cidade, logradouro e bairro
        Cidade cidadeValida = new Cidade();
        cidadeValida.setIdCidade(1);
        Logradouro logradouroValido = new Logradouro();
        logradouroValido.setIdLogradouro(1);
        Bairro bairroValido = new Bairro();
        bairroValido.setIdBairro(1);

        //Criação do Objeto Endereco Principal
        Endereco enderecoValido = new Endereco();

        enderecoValido.setCEP("85801001");
        enderecoValido.setCidade(cidadeValida);
        enderecoValido.setLogradouro(logradouroValido);
        enderecoValido.setBairro(bairroValido);

        //Execução do Serviço (Manager)
        Endereco enderecoSalvo = servico.cadastrarEndereco(enderecoValido);

        //Asserções
        assertNotNull(enderecoSalvo, "O endereço retornado não deve ser nulo.");
        assertEquals("85801001", enderecoSalvo.getCEP(), "O CEP deve ser mantido.");
        assertTrue(enderecoSalvo.getIdEndereco() > 0, "O endereço deve ter recebido um ID.");
        assertEquals(1, enderecoSalvo.getCidade().getIdCidade(), "O ID da Cidade deve ser 1.");
    }

    //Falha 1: CEP Inválido
    @Test void CadastrarEndereco_CepInvalido() {
        Endereco enderecoInvalido = new Endereco();
        enderecoInvalido.setCEP("123");

        EnderecoException excecao = assertThrows(EnderecoException.class, () -> {
            servico.cadastrarEndereco(enderecoInvalido);
        });

        assertTrue(excecao.getMessage().contains("CEP é obrigatório e deve ter 8 dígitos!"),
                "A exceção deve conter a mensagem de CEP inválido.");
    }

    //Falha 2: Objeto Endereco Nulo
    @Test void CadastrarEndereco_ObjetoNulo() {
        EnderecoException excecao = assertThrows(EnderecoException.class, () -> {
            servico.cadastrarEndereco(null);
        });

        assertEquals("Objeto Endereco não pode ser nulo!", excecao.getMessage(),
                "Deve lançar exceção para objeto Endereco nulo.");
    }

    //Falha 3: Endereço Duplicado
    @Test void CadastrarEndereco_Duplicado() {

        Endereco enderecoExistente = new Endereco();
        enderecoExistente.setCEP("85801001"); // CEP existente

        EnderecoException excecao = assertThrows(EnderecoException.class, () -> {
            servico.cadastrarEndereco(enderecoExistente);
        });

        assertTrue(excecao.getMessage().contains("Endereço já cadastrado para o CEP: 85801000"),
                "A exceção deve indicar que o endereço já está cadastrado.");
    }

    // CENÁRIOS DE TESTE: obterEnderecoPorCep
    @Test void ObterEnderecoPorCep_Sucesso() throws EnderecoException {
        String cepExistente = "85801001";

        List<Endereco> listaEnderecos = servico.obterEnderecoPorCep(cepExistente);
        assertNotNull(listaEnderecos, "A lista de endereços retornada não deve ser nula.");
        assertFalse(listaEnderecos.isEmpty(), "A lista não deve estar vazia para um CEP válido.");

        //Primeiro elemento da lista para testar
        Endereco enderecoEncontrado = listaEnderecos.get(0);

        assertEquals(cepExistente, enderecoEncontrado.getCEP(), "O CEP do primeiro endereço retornado deve ser o buscado.");
        assertTrue(enderecoEncontrado.getIdEndereco() > 0, "O endereço deve ter um ID válido.");
    }

    //Falha 1: CEP Inválido
    @Test void ObterEnderecoPorCep_CepInvalido() {
        String cepInvalido = "12345";

        EnderecoException excecao = assertThrows(EnderecoException.class, () -> {
            servico.obterEnderecoPorCep(cepInvalido);
        });

        assertTrue(excecao.getMessage().contains("Formato de CEP inválido! Obrigatório ter 8 dígitos."),
                "Deve lançar exceção por formato de CEP inválido.");
    }

    // CENÁRIOS DE TESTE: obterEnderecoPorID
    @Test void ObterEnderecoPorID_Sucesso() throws EnderecoException {
        int idValido = 1;

        Endereco enderecoEncontrado = servico.obterEnderecoPorID(idValido);

        assertNotNull(enderecoEncontrado, "Deve encontrar o endereço para o ID válido.");
        assertEquals(idValido, enderecoEncontrado.getIdEndereco(), "O ID retornado deve ser o buscado.");
    }

    //Falha 1: ID Negativo
    @Test void ObterEnderecoPorID_IDNegativo() {
        int idInvalido = -10;

        EnderecoException excecao = assertThrows(EnderecoException.class, () -> {
            servico.obterEnderecoPorID(idInvalido);
        });

        assertTrue(excecao.getMessage().contains("ID inválido! Obrigatório ser positivo."),
                "Deve lançar exceção para ID negativo.");
    }

    // CENÁRIOS DE TESTE: obterCidadePorId
    @Test void ObterCidadePorId_Sucesso() throws EnderecoException {
        int idCidadeValido = 1;

        Cidade cidadeEncontrada = servico.obterCidadePorId(idCidadeValido);

        assertNotNull(cidadeEncontrada, "Deve encontrar a cidade para o ID válido.");
        assertEquals(idCidadeValido, cidadeEncontrada.getIdCidade(), "O ID retornado deve ser o buscado.");
    }

    //Falha 1: ID de Cidade Negativo
    @Test void ObterCidadePorId_IDNegativo() {
        int idInvalido = -5;

        EnderecoException excecao = assertThrows(EnderecoException.class, () -> {
            servico.obterCidadePorId(idInvalido);
        });

        assertTrue(excecao.getMessage().contains("ID inválido! Obrigatório ser positivo."),
                "Deve lançar exceção para ID de Cidade negativo.");
    }
}
*/

