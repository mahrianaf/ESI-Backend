package unioeste.geral.endereco.api;

import com.fasterxml.jackson.databind.JsonNode;
import unioeste.geral.endereco.bo.*;
import unioeste.geral.endereco.exception.EnderecoException;
import unioeste.geral.endereco.manager.UCEnderecoGeralServicos;
import unioeste.geral.endereco.manager.UCEnderecoGeralServicos_implementation;

// Imports para Servlet
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

// Imports para JSON
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/api/endereco/*")
public class Servlet extends HttpServlet {

    private UCEnderecoGeralServicos manager;
    private ObjectMapper mapper;

    @Override public void init() throws ServletException {
        this.manager = new UCEnderecoGeralServicos_implementation();
        this.mapper = new ObjectMapper();
    }

    //Cadastrar Endereço Completo
    @Override protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");

        try {
            JsonNode root = mapper.readTree(request.getInputStream());
            String tipoUsuario = root.path("tipo").asText();

            Bairro bairro = new Bairro();
            bairro.setIdBairro(root.path("bairro").asInt());

            Cidade cidade = new Cidade();
            cidade.setIdCidade(root.path("cidade").asInt());

            Logradouro logradouro = new Logradouro();
            logradouro.setIdLogradouro(root.path("logradouro").asInt());

            Endereco endereco = new Endereco();
            endereco.setCEP(root.path("cep").asText());
            endereco.setBairro(bairro);
            endereco.setCidade(cidade);
            endereco.setLogradouro(logradouro);

            List<String> listaEmails = new ArrayList<>();
            root.path("emails").forEach(node -> listaEmails.add(node.asText()));

            List<String> listaTelefones = new ArrayList<>();
            root.path("telefones").forEach(node -> listaTelefones.add(node.asText()));

            Object resultado;

            //Verificação Tipo Usuário
            if ("CLIENTE".equalsIgnoreCase(tipoUsuario)) {
                Cliente cliente = new Cliente();
                cliente.setNome(root.path("nome").asText());
                cliente.setCPF(root.path("cpf").asText());
                cliente.setNroMoradia(root.path("nro").asInt());
                cliente.setComplemento(root.path("complemento").asText());
                cliente.setEndereco(endereco);

                resultado = manager.cadastrarPessoa(cliente, listaEmails, listaTelefones);

            } else if ("PACIENTE".equalsIgnoreCase(tipoUsuario)) {
                Paciente paciente = new Paciente();
                paciente.setNome(root.path("nome").asText());
                paciente.setCPF(root.path("cpf").asText());
                paciente.setNroMoradia(root.path("nro").asInt());
                paciente.setComplemento(root.path("complemento").asText());
                paciente.setEndereco(endereco);

                resultado = manager.cadastrarPessoa(paciente, listaEmails, listaTelefones);

            } else {
                throw new IllegalArgumentException("Tipo de usuário inválido: " + tipoUsuario);
            }

            //Retorno Objeto Preenchido (Cliente ou Paciente)
            response.setStatus(HttpServletResponse.SC_CREATED);
            mapper.writeValue(response.getWriter(), resultado);

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            Map<String, String> erro = new HashMap<>();
            erro.put("message", "Erro ao processar cadastro: " + e.getMessage());
            mapper.writeValue(response.getWriter(), erro);
        }
    }

    //Buscar Endereços ou Consultar Usuários
    @Override protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        response.setContentType("application/json;charset=UTF-8");
        String pathInfo = request.getPathInfo();

        try {

            //Busca ViaCEP: /api/endereco/viacep?cep=...
            if (pathInfo != null && pathInfo.contains("/viacep")) {
                String cepViaCep = request.getParameter("cep");

                if (cepViaCep != null && !cepViaCep.isEmpty()) {
                    Endereco enderecoViaCep = manager.obterEnderecoPorCepViaCEP(cepViaCep);

                    response.setStatus(HttpServletResponse.SC_OK); // 200 OK
                    mapper.writeValue(response.getWriter(), enderecoViaCep);
                    return;
                }
            }

            //Busca Pessoa por CPF: /api/endereco?cpf=...&pessoa=...
            String cpf = request.getParameter("cpf");
            String usuario = request.getParameter("pessoa");
            if (cpf != null && !cpf.isEmpty() && usuario!=null && !usuario.isEmpty()) {
                Pessoa pessoa = manager.obterPessoaPorCPF(cpf, usuario);

                response.setStatus(HttpServletResponse.SC_OK); // 200 OK
                mapper.writeValue(response.getWriter(), pessoa);
                return;
            }

            //Busca local Cep: /api/endereco?cep=...
            String cep = request.getParameter("cep");
            if (cep != null && !cep.isEmpty()) {
                List<Endereco> listaEnderecos = manager.obterEnderecoPorCep(cep);

                response.setStatus(HttpServletResponse.SC_OK); // 200 OK
                mapper.writeValue(response.getWriter(), listaEnderecos);
                return;
            }

            //Busca ID (Endereco ou Cidade): /api/endereco/id/1 ou /api/endereco/cidade/1
            if (pathInfo != null && pathInfo.length() > 1) {
                String[] pathParts = pathInfo.substring(1).split("/");

                if (pathParts.length == 2) {
                    String tipoBusca = pathParts[0].toLowerCase();
                    String idStr = pathParts[1];
                    int id;
                    try {
                        id = Integer.parseInt(idStr);
                    } catch (NumberFormatException e) {
                        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                        mapper.writeValue(response.getWriter(), new ErrorResponse("ID inválido. Deve ser um número inteiro."));
                        return;
                    }

                    if (tipoBusca.equals("id")) { //Obter Endereco por ID: /api/endereco/id/1
                        Endereco endereco = manager.obterEnderecoPorID(id);
                        if (endereco != null) {
                            response.setStatus(HttpServletResponse.SC_OK);
                            mapper.writeValue(response.getWriter(), endereco);
                        } else {
                            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                            mapper.writeValue(response.getWriter(), new ErrorResponse("Endereço não encontrado para o ID: " + id));
                        }
                        return;

                    } else if (tipoBusca.equals("cidade")) { //Obter Cidade por ID: /api/endereco/cidade/1
                        Cidade cidade = manager.obterCidadePorId(id);
                        if (cidade != null) {
                            response.setStatus(HttpServletResponse.SC_OK);
                            mapper.writeValue(response.getWriter(), cidade);
                        } else {
                            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                            mapper.writeValue(response.getWriter(), new ErrorResponse("Cidade não encontrada para o ID: " + id));
                        }
                        return;
                    }
                }
            }

            //Se nenhuma rota for acionada...
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST); //400 Bad Request
            mapper.writeValue(response.getWriter(), new ErrorResponse("Parâmetros de busca insuficientes ou inválidos."));

        } catch (EnderecoException e) {
            if (e.getMessage().contains("não encontrado")) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND); //404 not found
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);//400 Bad Request
            }
            mapper.writeValue(response.getWriter(), new ErrorResponse(e.getMessage()));

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            mapper.writeValue(response.getWriter(), new ErrorResponse("Erro interno na API: " + e.getMessage()));
        }
    }
}

class ErrorResponse {
    public String message;
    public ErrorResponse(String message) { this.message = message; }
}

//http://localhost:8080/api/endereco/id/1