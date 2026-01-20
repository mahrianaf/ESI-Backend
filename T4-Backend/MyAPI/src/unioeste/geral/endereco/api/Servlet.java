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
import java.time.LocalDate;
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
            String acao = root.path("acao").asText();

            //Cadastrar Endereco Completo
            if("cadastrarPessoa".equalsIgnoreCase(acao)){
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

                    resultado = manager.cadastrarPessoa(cliente, listaEmails, listaTelefones, tipoUsuario);

                } else if ("PACIENTE".equalsIgnoreCase(tipoUsuario)) {
                    Paciente paciente = new Paciente();
                    paciente.setNome(root.path("nome").asText());
                    paciente.setCPF(root.path("cpf").asText());
                    paciente.setNroMoradia(root.path("nro").asInt());
                    paciente.setComplemento(root.path("complemento").asText());
                    paciente.setEndereco(endereco);

                    resultado = manager.cadastrarPessoa(paciente, listaEmails, listaTelefones, tipoUsuario);

                } else {
                    throw new IllegalArgumentException("Tipo de usuário inválido: " + tipoUsuario);
                }
                //Retorno Objeto Preenchido (Cliente ou Paciente)
                response.setStatus(HttpServletResponse.SC_CREATED);
                mapper.writeValue(response.getWriter(), resultado);

            //Registrar Ordem de Servico
            } else if ("ordemServico".equalsIgnoreCase(acao)){
                OrdemServico os = new OrdemServico();
                os.setNroOrdemServico(root.path("nrOS").asInt());
                os.setDataEmissaoOS(LocalDate.parse(root.path("dataOS").asText()));

                Cliente cliente= new Cliente();
                cliente.setID(root.path("codCliente").asInt());
                os.setCliente(cliente);

                Atendente atendente= new Atendente();
                atendente.setCodAtendente(root.path("codAtendente").asInt());
                os.setAtendente(atendente);

                os.setDescricaoProblema(root.path("descricao").asText());
                os.setTotal((float) root.path("totalOS").asDouble());

                List<ServicoOS> listaItens = new ArrayList<>();

                //Pega o primeiro serviço (o fixo do HTML)
                String codFixo = root.path("codServico").asText();
                if (!codFixo.isEmpty()) {
                    Servico s = new Servico();
                    s.setCodServico(codFixo);
                    ServicoOS ponte = new ServicoOS();
                    ponte.setServico(s);
                    ponte.setOrdemservico(os);
                    listaItens.add(ponte);
                }

                //Pega os serviços dinâmicos (da lista JS)
                root.path("codServicoJS").forEach(node -> {
                    Servico s = new Servico();
                    s.setCodServico(node.asText()); //node já é a string do código
                    ServicoOS ponte = new ServicoOS();
                    ponte.setServico(s);
                    ponte.setOrdemservico(os);
                    listaItens.add(ponte);
                });
                manager.registrarOS(os, listaItens);

                response.setStatus(HttpServletResponse.SC_CREATED);
                Map<String, String> sucesso = new HashMap<>();
                sucesso.put("message", "Ordem de Serviço " + os.getNroOrdemServico() + " registrada!");
                mapper.writeValue(response.getWriter(), sucesso);

            //Registrar Receita Medica
            } else if ("receitaMedica".equalsIgnoreCase(acao)) {
                ReceitaMedica rm = new ReceitaMedica();
                rm.setNroReceita(root.path("nrRM").asInt());
                rm.setDataEmissao(LocalDate.parse(root.path("dataRM").asText()));

                //Objetos temporários para busca por CPF/CRM
                Medico m = new Medico();
                m.setCRM(root.path("crm").asText());
                rm.setMedico(m);

                Paciente p = new Paciente();
                p.setCPF(root.path("cpfPaciente").asText());
                rm.setPaciente(p);

                CID c = new CID();
                c.setCodCID(root.path("codCID").asText());
                rm.setCid(c);

                List<Prescricao> listaItens = new ArrayList<>();
                root.path("itens").forEach(node -> {
                    Prescricao pr = new Prescricao();
                    Medicamento med = new Medicamento();

                    //asText() para garantir "06815" como texto
                    String codigoComZero = node.path("medicamento").asText();
                    med.setCodMedicamento(codigoComZero);
                    //med.setCodMedicamento(node.path("medicamento").asText());
                    pr.setMedicamento(med);
                    pr.setDataUsoInicial(LocalDate.parse(node.path("dataInicio").asText()));
                    pr.setDataUsoFinal(LocalDate.parse(node.path("dataFim").asText()));
                    pr.setPosologia(node.path("posologia").asText());
                    pr.setReceitaMedica(rm);
                    listaItens.add(pr);
                });

                try {
                    manager.registrarReceita(rm, listaItens);
                    response.setStatus(HttpServletResponse.SC_CREATED);
                    mapper.writeValue(response.getWriter(), Map.of("message", "Sucesso!"));
                } catch (Exception e) {
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    mapper.writeValue(response.getWriter(), Map.of("message", e.getMessage()));
                }
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                mapper.writeValue(response.getWriter(), Map.of("message", "Ação '" + acao + "' não reconhecida."));
            }
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

            //Busca Pessoa por CPF: /api/endereco?cod=...&pessoa=...
            String cod = request.getParameter("cod");
            String user = request.getParameter("pessoa");
            if (cod != null && !cod.isEmpty() && user!=null && !user.isEmpty()) {

                Pessoa pessoa = manager.obterPessoaPorID(cod, user);
                List<Object> telefones = manager.obterTelefonesCompletos(pessoa.getID(), user);
                List<Object> emails = manager.obterEmailsCompletos(pessoa.getID(), user);

                //Cria um Map para unir tudo em um único JSON
                Map<String, Object> resultadoFinal = new HashMap<>();
                resultadoFinal.put("fones", telefones);
                resultadoFinal.put("emails", emails);
                resultadoFinal.put("dados", pessoa);

                response.setStatus(HttpServletResponse.SC_OK);
                mapper.writeValue(response.getWriter(), resultadoFinal); //Envia o MAP
                return;
            }

            //Busca Servico: /api/endereco?codServico
            String codServico = request.getParameter("codServico");
            if (codServico != null && !codServico.isEmpty()) {
                Servico servico = manager.buscarServico(codServico);

                response.setStatus(HttpServletResponse.SC_OK); //200 OK
                mapper.writeValue(response.getWriter(), servico);
                return;
            }

            String buscaRM = request.getParameter("buscaRM");
            if (buscaRM != null && !buscaRM.isEmpty()) {
                ReceitaMedica rm = manager.buscarReceitaCompleta(buscaRM);
                List<Prescricao> prescricoes = manager.listarPrescricoes(buscaRM);

                if (rm != null) {
                    Map<String, Object> jsonFinal = new HashMap<>();

                    //1. Dados da Receita
                    Map<String, Object> dadosRM = new HashMap<>();
                    dadosRM.put("nroReceita", rm.getNroReceita());
                    dadosRM.put("dataEmissao", rm.getDataEmissao().toString());
                    dadosRM.put("paciente", rm.getPaciente().getNome());
                    dadosRM.put("medico", rm.getMedico().getNome());
                    dadosRM.put("cidNome", rm.getCid().getNome());

                    //2. Lista de Prescrições
                    List<Map<String, Object>> listaItens = new ArrayList<>();
                    for (Prescricao p : prescricoes) {
                        Map<String, Object> item = new HashMap<>();
                        item.put("medicamento", p.getMedicamento().getNome());
                        item.put("posologia", p.getPosologia());
                        item.put("inicio", p.getDataUsoInicial().toString());
                        item.put("fim", p.getDataUsoFinal().toString());
                        listaItens.add(item);
                    }

                    jsonFinal.put("receita", dadosRM);
                    jsonFinal.put("itens", listaItens);

                    response.setContentType("application/json");
                    response.setCharacterEncoding("UTF-8");
                    mapper.writeValue(response.getWriter(), jsonFinal);
                }
            }

            //Busca Servico: /api/endereco?CID
            String cid = request.getParameter("CID");
            if (cid != null && !cid.isEmpty()) {
                CID codCID = manager.buscarCID(cid);

                response.setStatus(HttpServletResponse.SC_OK);
                mapper.writeValue(response.getWriter(), codCID);
                return;
            }

            //Busca Servico: /api/endereco?buscaOS
            String buscaOS = request.getParameter("buscaOS");
            if (buscaOS != null && !buscaOS.isEmpty()) {
                OrdemServico os = manager.buscarOrdemServico(buscaOS);
                List<ServicoOS> servico = manager.buscarServicoOS(buscaOS);

                Map<String, Object> resultadoFinal = new HashMap<>();
                resultadoFinal.put("nroOS", os.getNroOrdemServico());
                resultadoFinal.put("data", os.getDataEmissaoOS().toString());
                resultadoFinal.put("clienteId", os.getCliente().getID());
                resultadoFinal.put("total", os.getTotal());

                List<String> codigosServicos = new ArrayList<>();
                for(ServicoOS s : servico) {
                    codigosServicos.add(s.getServico().getCodServico());
                }
                resultadoFinal.put("servicos", codigosServicos);
                mapper.writeValue(response.getWriter(), resultadoFinal);
                return;
            }

            //Busca Pessoa por CPF: /api/endereco?cpf=...&pessoa=...
            String cpf = request.getParameter("cpf");
            String usuario = request.getParameter("pessoa");
            if (cpf != null && !cpf.isEmpty() && usuario!=null && !usuario.isEmpty()) {
                Pessoa pessoa = manager.obterPessoaPorCPF(cpf, usuario);

                response.setStatus(HttpServletResponse.SC_OK);
                mapper.writeValue(response.getWriter(), pessoa);
                return;
            }

            //Busca ViaCEP: /api/endereco/viacep?cep=...
            if (pathInfo != null && pathInfo.contains("/viacep")) {
                String cepViaCep = request.getParameter("cep");

                if (cepViaCep != null && !cepViaCep.isEmpty()) {
                    Endereco enderecoViaCep = manager.obterEnderecoPorCepViaCEP(cepViaCep);

                    response.setStatus(HttpServletResponse.SC_OK);
                    mapper.writeValue(response.getWriter(), enderecoViaCep);
                    return;
                }
            }

            //Busca local Cep: /api/endereco?cep=...
            String cep = request.getParameter("cep");
            if (cep != null && !cep.isEmpty()) {
                List<Endereco> listaEnderecos = manager.obterEnderecoPorCep(cep);

                response.setStatus(HttpServletResponse.SC_OK);
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