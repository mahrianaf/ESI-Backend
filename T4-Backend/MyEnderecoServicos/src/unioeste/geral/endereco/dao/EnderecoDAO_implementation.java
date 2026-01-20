package unioeste.geral.endereco.dao;
import com.fasterxml.jackson.databind.ObjectMapper;
import unioeste.geral.endereco.bo.*;

import unioeste.apoio.BD.ConexaoBD;
import unioeste.geral.endereco.exception.EnderecoException;

import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

public class EnderecoDAO_implementation implements EnderecoDAO {

    //Cadastro e Consulta de Pessoa

    @Override public Pessoa cadastrarPessoa(Pessoa obj, List<String> emails, List<String> telefones) throws Exception {
        Connection conn = null;
        PreparedStatement stEnd = null, stCli = null, stEmail = null, stTel = null;
        ResultSet rs = null;

        String entidade = obj.getClass().getSimpleName();
        String entidadeEmail= "email" + entidade;
        String entidadeTelefone= "telefone" + entidade;
        String idEntidade= "id" + entidade;

        try {
            conn = ConexaoBD.getConnection();
            conn.setAutoCommit(false); //Inicia Transação

            //Inserção Endereço
            String sqlEnd = "INSERT INTO endereco (CEP, idLogradouro, idBairro, idCidade) VALUES (?, ?, ?, ?)";
            stEnd = conn.prepareStatement(sqlEnd, Statement.RETURN_GENERATED_KEYS);
            stEnd.setString(1, obj.getEndereco().getCEP());
            stEnd.setInt(2, obj.getEndereco().getLogradouro().getIdLogradouro());
            stEnd.setInt(3, obj.getEndereco().getBairro().getIdBairro());
            stEnd.setInt(4, obj.getEndereco().getCidade().getIdCidade());
            stEnd.executeUpdate();

            rs = stEnd.getGeneratedKeys();
            if (rs.next()) obj.getEndereco().setIdEndereco(rs.getInt(1));

            //Inserção Cliente
            String sqlCli = "INSERT INTO " + entidade + "(nome, CPF, nroMoradia, complemento, idEndereco) VALUES (?, ?, ?, ?, ?)";
            stCli = conn.prepareStatement(sqlCli, Statement.RETURN_GENERATED_KEYS);
            stCli.setString(1, obj.getNome());
            stCli.setString(2, obj.getCPF());
            stCli.setInt(3, obj.getNroMoradia());
            stCli.setString(4, obj.getComplemento());
            stCli.setInt(5, obj.getEndereco().getIdEndereco());
            stCli.executeUpdate();

            rs = stCli.getGeneratedKeys();
            if (rs.next()) obj.setID(rs.getInt(1));

            //Emails
            String sqlEmail = "INSERT INTO " + entidadeEmail + "(" + idEntidade + ", enderecoEmail) VALUES (?, ?)";
            stEmail = conn.prepareStatement(sqlEmail);
            for (String email : emails) {
                stEmail.setInt(1, obj.getID());
                stEmail.setString(2, email);
                stEmail.executeUpdate();
            }

            //Telefones
            String sqlTel = "INSERT INTO " + entidadeTelefone + "(" + idEntidade + ", nroTelefone, idDDI, idDDD) VALUES (?, ?, ?, ?)";
            stTel = conn.prepareStatement(sqlTel);
            for (String tel : telefones) {
                stTel.setInt(1, obj.getID());
                stTel.setString(2, tel);
                stTel.setInt(3, 1); //Brasil (DDI 55)

                if (tel.startsWith("45")) {
                    stTel.setInt(4, 1); //Foz do Iguaçu
                } else if (tel.startsWith("44")) {
                    stTel.setInt(4, 2); //Umuarama
                } else {
                    stTel.setInt(4, 3); //Curitiba/Londrina
                }
                stTel.executeUpdate();
            }
            conn.commit();
            return obj;

        } catch (SQLException e) {
            if (conn != null) conn.rollback(); //Cancela se haver falha
            throw new Exception("Erro de Banco de Dados: " + e.getMessage());
        } finally {
            ConexaoBD.close(rs);
            ConexaoBD.close(stEnd);
            ConexaoBD.close(stCli);
            ConexaoBD.close(stEmail);
            ConexaoBD.close(stTel);
            ConexaoBD.close(conn);
        }
    }

    @Override public Pessoa obterPessoaPorCPF(String cpf, String usuario) throws Exception {
        Connection conn = null;
        PreparedStatement st = null;
        ResultSet rs = null;
        Pessoa pessoa = null;

        try {
            conn = ConexaoBD.getConnection();

            String sql = "SELECT * FROM " + usuario + " WHERE CPF = ?";
            st = conn.prepareStatement(sql);
            st.setString(1, cpf);
            rs = st.executeQuery();

            int idEnderecoEncontrado = -1;

            if (rs.next()) {
                if (usuario.equalsIgnoreCase("cliente")) {
                    pessoa = new Cliente();
                    pessoa.setID(rs.getInt("idCliente"));
                } else {
                    pessoa = new Paciente();
                    pessoa.setID(rs.getInt("idPaciente"));
                }

                pessoa.setNome(rs.getString("nome"));
                pessoa.setCPF(rs.getString("CPF"));
                pessoa.setNroMoradia(rs.getInt("nroMoradia"));
                pessoa.setComplemento(rs.getString("complemento"));
                idEnderecoEncontrado = rs.getInt("idEndereco");
            }

            if (pessoa != null && idEnderecoEncontrado != -1) {
                try {
                    Endereco enderecoCompleto = obterEnderecoPorID(idEnderecoEncontrado);
                    pessoa.setEndereco(enderecoCompleto);
                } catch (EnderecoException e) {
                    System.err.println("Erro ao buscar endereço: " + e.getMessage());
                }
            }
            return pessoa;

        } catch (SQLException e) {
            throw new Exception("Erro de Banco de Dados: " + e.getMessage());
        } finally {
            ConexaoBD.close(rs);
            ConexaoBD.close(st);
            ConexaoBD.close(conn);
        }
    }

    @Override public Pessoa obterPessoaPorID(String id, String user) throws Exception {
        Connection conn = null;
        PreparedStatement st = null;
        ResultSet rs = null;
        Pessoa pessoa = null;
        String sql;

        if (user.equalsIgnoreCase("cliente")) {
            sql = "SELECT * FROM " + user + " WHERE id" + user + " = ?";
        } else if (user.equalsIgnoreCase("paciente")){
            sql = "SELECT * FROM " + user + " WHERE CPF = ?";
        } else if (user.equalsIgnoreCase("medico")){
            sql = "SELECT * FROM " + user + " WHERE CRM = ?";
        } else {
            sql = "SELECT * FROM " + user + " WHERE id" + user + " = ?";
        }

        try {
            conn = ConexaoBD.getConnection();

            //String sql = "SELECT * FROM " + user + " WHERE id" + user + " = ?";
            st = conn.prepareStatement(sql);
            st.setString(1, id);
            rs = st.executeQuery();

            int idEnderecoEncontrado = -1;

            if (rs.next()) {
                if (user.equalsIgnoreCase("cliente")) {
                    pessoa = new Cliente();
                    pessoa.setID(rs.getInt("idCliente"));
                } else if (user.equalsIgnoreCase("paciente")){
                    pessoa = new Paciente();
                    pessoa.setID(rs.getInt("idPaciente"));
                } else if (user.equalsIgnoreCase("medico")){
                    pessoa = new Paciente();
                    pessoa.setID(rs.getInt("idMedico"));
                } else {
                    pessoa = new Paciente();
                    pessoa.setID(rs.getInt("idAtendente"));
                }

                pessoa.setNome(rs.getString("nome"));
                pessoa.setCPF(rs.getString("CPF"));
                pessoa.setNroMoradia(rs.getInt("nroMoradia"));
                pessoa.setComplemento(rs.getString("complemento"));
                idEnderecoEncontrado = rs.getInt("idEndereco");
            }

            if (pessoa != null && idEnderecoEncontrado != -1) {
                try {
                    Endereco enderecoCompleto = obterEnderecoPorID(idEnderecoEncontrado);
                    pessoa.setEndereco(enderecoCompleto);
                } catch (EnderecoException e) {
                    System.err.println("Erro ao buscar endereço: " + e.getMessage());
                }
            }
            return pessoa;

        } catch (SQLException e) {
            throw new Exception("Erro de Banco de Dados: " + e.getMessage());
        } finally {
            ConexaoBD.close(rs);
            ConexaoBD.close(st);
            ConexaoBD.close(conn);
        }
    }

    @Override public List<Object> obterEmailsCompletos(int idPessoa, String user) throws SQLException {
        List<Object> emails = new ArrayList<>();
        String sql = "SELECT * FROM Email" + user + " WHERE id" + user + "= ?";

        try (Connection conn = ConexaoBD.getConnection();
             PreparedStatement st = conn.prepareStatement(sql)) {
            st.setInt(1, idPessoa);
            ResultSet rs = st.executeQuery();

            while (rs.next()) {
                if (user.equalsIgnoreCase("cliente")) {
                    EmailCliente t = new EmailCliente();
                    t.setEnderecoEmail(rs.getString("enderecoEmail"));
                    emails.add(t);
                } else if (user.equalsIgnoreCase("paciente")){
                    EmailPaciente t = new EmailPaciente();
                    t.setEnderecoEmail(rs.getString("enderecoEmail"));
                    emails.add(t);
                } else if (user.equalsIgnoreCase("medico")){
                    EmailMedico t = new EmailMedico();
                    t.setEnderecoEmail(rs.getString("enderecoEmail"));
                    emails.add(t);
                } else {
                    EmailAtendente t = new EmailAtendente();
                    t.setEnderecoEmail(rs.getString("enderecoEmail"));
                    emails.add(t);
                }
            }
        }
        return emails;
    }

    @Override public List<Object> obterTelefonesCompletos(int idPessoa, String user) throws SQLException {
        List<Object> fones = new ArrayList<>();
        String sql = "SELECT * FROM Telefone" + user + " WHERE id" + user + "= ?";

        try (Connection conn = ConexaoBD.getConnection();
             PreparedStatement st = conn.prepareStatement(sql)) {
            st.setInt(1, idPessoa);
            ResultSet rs = st.executeQuery();

            while (rs.next()) {
                if (user.equalsIgnoreCase("cliente")) {
                    TelefoneCliente t = new TelefoneCliente();
                    t.setNroTelefone(rs.getString("nroTelefone"));
                    fones.add(t);
                } else if (user.equalsIgnoreCase("paciente")){
                    TelefonePaciente t = new TelefonePaciente();
                    t.setNroTelefone(rs.getString("nroTelefone"));
                    fones.add(t);
                } else if (user.equalsIgnoreCase("medico")){
                    TelefoneMedico t = new TelefoneMedico();
                    t.setNroTelefone(rs.getString("nroTelefone"));
                    fones.add(t);
                } else {
                    TelefoneAtendente t = new TelefoneAtendente();
                    t.setNroTelefone(rs.getString("nroTelefone"));
                    fones.add(t);
                }
            }
        }
        return fones;
    }

    //Cadastro e Consulta dos Serviços

    @Override public Servico buscarServico(String codServico) throws Exception {
        Connection conn = null;
        PreparedStatement st = null;
        ResultSet rs = null;
        Servico infoServico=null;

        try{
            conn = ConexaoBD.getConnection();

            String sql = "SELECT * FROM Servico WHERE codServico = ?";
            st = conn.prepareStatement(sql);
            st.setString(1, codServico);
            rs = st.executeQuery();

            if(rs.next()){
                infoServico= new Servico();

                infoServico.setCodServico(rs.getString("codServico"));
                infoServico.setNomeTipo(rs.getString("nomeTipo"));
                infoServico.setValor(rs.getFloat("valor"));
            }
            return infoServico;
        }catch (SQLException e) {
            throw new Exception("Erro de Banco de Dados: " + e.getMessage());
        } finally {
            ConexaoBD.close(rs);
            ConexaoBD.close(st);
            ConexaoBD.close(conn);
        }
    }

    @Override public void registrarOS(OrdemServico os, List<ServicoOS> itens) throws Exception {
        Connection conn = null;
        PreparedStatement st = null, st2 = null;

        try {
            conn = ConexaoBD.getConnection();
            conn.setAutoCommit(false);

            //1. Inserir a Ordem de Serviço
            String sql = "INSERT INTO OrdemServico (nroOrdemServico, dataEmissaoOS, descricaoProblema, total, codCliente, codAtendente) VALUES (?, ?, ?, ?, ?, ?)";
            st = conn.prepareStatement(sql);
            st.setInt(1, os.getNroOrdemServico());
            st.setDate(2, java.sql.Date.valueOf(os.getDataEmissaoOS())); // LocalDate para SQL Date
            st.setString(3, os.getDescricaoProblema());
            st.setFloat(4, os.getTotal());
            st.setInt(5, os.getCliente().getID());
            st.setInt(6, os.getAtendente().getCodAtendente());
            st.executeUpdate();

            //2. Inserir os Itens (Ponte)
            String sqlPonte = "INSERT INTO ServicoOS (codServico, nroOrdemServico) VALUES (?, ?)";
            st2 = conn.prepareStatement(sqlPonte);

            for (ServicoOS item : itens) {
                st2.setString(1, item.getServico().getCodServico());
                st2.setInt(2, os.getNroOrdemServico());
                st2.executeUpdate();
            }

            conn.commit();
        } catch (SQLException e) {
            if (conn != null) conn.rollback();
            throw new Exception("Erro ao salvar OS: " + e.getMessage());
        } finally {
            ConexaoBD.close(st);
            ConexaoBD.close(st2);
            ConexaoBD.close(conn);
        }
    }

    @Override public OrdemServico buscarOrdemServico(String buscaOS) throws Exception{
        Connection conn = null;
        PreparedStatement st = null;
        ResultSet rs = null;
        OrdemServico info=null;

        try{
            conn = ConexaoBD.getConnection();

            String sql = "SELECT * FROM OrdemServico WHERE nroOrdemServico = ?";
            st = conn.prepareStatement(sql);
            st.setString(1, buscaOS);
            rs = st.executeQuery();

            if(rs.next()){
                info= new OrdemServico();
                Cliente c= new Cliente();
                Atendente a= new Atendente();
                info.setNroOrdemServico(rs.getInt("nroOrdemServico"));
                info.setDataEmissaoOS(rs.getDate("dataEmissaoOS").toLocalDate());
                info.setDescricaoProblema(rs.getString("descricaoProblema"));
                info.setTotal(rs.getFloat("total"));
                c.setID(rs.getInt("codCliente"));
                info.setCliente(c);
                a.setCodAtendente(rs.getInt("codAtendente"));
                info.setAtendente(a);
            }
            return info;
        }catch (SQLException e) {
            throw new Exception("Erro de Banco de Dados: " + e.getMessage());
        } finally {
            ConexaoBD.close(rs);
            ConexaoBD.close(st);
            ConexaoBD.close(conn);
        }
    }

    @Override public List<ServicoOS> buscarServicoOS(String buscaOS) throws Exception {
        Connection conn = null;
        PreparedStatement st = null;
        ResultSet rs = null;
        List<ServicoOS> lista = new ArrayList<>();

        try {
            conn = ConexaoBD.getConnection();

            String sql = "SELECT * FROM ServicoOS WHERE nroOrdemServico = ?";
            st = conn.prepareStatement(sql);
            st.setString(1, buscaOS);
            rs = st.executeQuery();

            while (rs.next()) {
                ServicoOS ponte = new ServicoOS();
                Servico s = new Servico();
                OrdemServico os = new OrdemServico();

                s.setCodServico(rs.getString("codServico"));
                os.setNroOrdemServico(rs.getInt("nroOrdemServico"));

                ponte.setServico(s);
                ponte.setOrdemservico(os);

                lista.add(ponte);
            }
            return lista;
        } catch (SQLException e) {
            throw new Exception("Erro de Banco de Dados: " + e.getMessage());
        } finally {
            ConexaoBD.close(rs);
            ConexaoBD.close(st);
            ConexaoBD.close(conn);
        }
    }

    @Override public CID buscarCID(String codCid) throws Exception {
        Connection conn = null;
        PreparedStatement st = null;
        ResultSet rs = null;
        CID cid= null;

        try {
            conn = ConexaoBD.getConnection();

            String sql = "SELECT * FROM CID WHERE codCID = ?";
            st = conn.prepareStatement(sql);
            st.setString(1, codCid);
            rs = st.executeQuery();

            if (rs.next()) {
                cid = new CID();
                cid.setNome(rs.getString("nome"));
            }
            return cid;
        }catch (SQLException e) {
            throw new Exception("Erro de Banco de Dados: " + e.getMessage());
        } finally {
            ConexaoBD.close(rs);
            ConexaoBD.close(st);
            ConexaoBD.close(conn);
        }
    }

    @Override public void registrarReceita(ReceitaMedica rm, List<Prescricao> itens) throws Exception {
        Connection conn = null;
        PreparedStatement stBusca = null, st1 = null, st2 = null;
        ResultSet rs = null;

        try {
            conn = ConexaoBD.getConnection();
            conn.setAutoCommit(false);

            //Descobrir o ID do Paciente pelo CPF
            String sqlP = "SELECT idPaciente FROM Paciente WHERE CPF = ?";
            stBusca = conn.prepareStatement(sqlP);
            stBusca.setString(1, rm.getPaciente().getCPF());
            rs = stBusca.executeQuery();
            if (rs.next()) {
                rm.getPaciente().setID(rs.getInt("idPaciente"));
            } else {
                throw new Exception("Paciente com CPF " + rm.getPaciente().getCPF() + " não encontrado.");
            }

            //Descobrir o ID do Médico pelo CRM
            String sqlM = "SELECT idMedico FROM Medico WHERE CRM = ?";
            stBusca = conn.prepareStatement(sqlM);
            stBusca.setString(1, rm.getMedico().getCRM());
            rs = stBusca.executeQuery();
            if (rs.next()) {
                rm.getMedico().setIdMedico(rs.getInt("idMedico"));
            } else {
                throw new Exception("Médico com CRM " + rm.getMedico().getCRM() + " não encontrado.");
            }

            //Inserir a Receita
            String sqlRec = "INSERT INTO Receita (nroReceita, dataEmissao, codCID, idPaciente, idMedico) VALUES (?, ?, ?, ?, ?)";
            st1 = conn.prepareStatement(sqlRec);
            st1.setInt(1, rm.getNroReceita());
            st1.setDate(2, java.sql.Date.valueOf(rm.getDataEmissao()));
            st1.setString(3, rm.getCid().getCodCID());
            st1.setInt(4, rm.getPaciente().getID());
            st1.setInt(5, rm.getMedico().getIdMedico());
            st1.executeUpdate();

            //Inserir as Prescrições
            String sqlPres = "INSERT INTO Prescricao (dataUsoInicial, dataUsoFinal, posologia, codMedicamento, nroReceita) VALUES (?, ?, ?, ?, ?)";
            st2 = conn.prepareStatement(sqlPres);

            for (Prescricao p : itens) {
                st2.setDate(1, java.sql.Date.valueOf(p.getDataUsoInicial()));
                st2.setDate(2, java.sql.Date.valueOf(p.getDataUsoFinal()));
                st2.setString(3, p.getPosologia());
                st2.setString(4, p.getMedicamento().getCodMedicamento());
                st2.setInt(5, rm.getNroReceita());
                st2.executeUpdate();
            }

            conn.commit();
        } catch (SQLException e) {
            if (conn != null) conn.rollback();
            throw new Exception("Erro ao salvar receita: " + e.getMessage());
        } finally {
            ConexaoBD.close(rs);
            ConexaoBD.close(stBusca);
            ConexaoBD.close(st1);
            ConexaoBD.close(st2);
            ConexaoBD.close(conn);
        }
    }

    @Override public ReceitaMedica buscarReceitaCompleta(String nroRM) throws Exception {
        Connection conn = null;
        PreparedStatement st = null;
        ResultSet rs = null;
        try {
            conn = ConexaoBD.getConnection();
            String sql = "SELECT r.*, p.nome as nomeP, p.CPF as cpfP, m.nome as nomeM, m.CRM as crmM, c.nome as nomeCID " +
                    "FROM Receita r " +
                    "INNER JOIN Paciente p ON r.idPaciente = p.idPaciente " +
                    "INNER JOIN Medico m ON r.idMedico = m.idMedico " +
                    "INNER JOIN CID c ON r.codCID = c.codCID " +
                    "WHERE r.nroReceita = ?";
            st = conn.prepareStatement(sql);
            st.setString(1, nroRM);
            rs = st.executeQuery();

            if (rs.next()) {
                ReceitaMedica rm = new ReceitaMedica();
                rm.setNroReceita(rs.getInt("nroReceita"));
                rm.setDataEmissao(rs.getDate("dataEmissao").toLocalDate());

                Paciente pac = new Paciente();
                pac.setNome(rs.getString("nomeP"));
                pac.setCPF(rs.getString("cpfP"));
                rm.setPaciente(pac);

                Medico med = new Medico();
                med.setNome(rs.getString("nomeM"));
                med.setCRM(rs.getString("crmM"));
                rm.setMedico(med);

                CID cid = new CID();
                cid.setCodCID(rs.getString("codCID"));
                cid.setNome(rs.getString("nomeCID"));
                rm.setCid(cid);

                return rm;
            }
            return null;
        } finally {
            ConexaoBD.close(rs);
            ConexaoBD.close(st);
            ConexaoBD.close(conn);
        }
    }

    @Override public List<Prescricao> listarPrescricoes(String nroRM) throws Exception {
        List<Prescricao> lista = new ArrayList<>();
        Connection conn = null;
        PreparedStatement st = null;
        ResultSet rs = null;
        try {
            conn = ConexaoBD.getConnection();

            String sql = "SELECT p.*, m.nome as nomeMed FROM Prescricao p " +
                    "INNER JOIN Medicamento m ON p.codMedicamento = m.codMedicamento " +
                    "WHERE p.nroReceita = ?";
            st = conn.prepareStatement(sql);
            st.setInt(1, Integer.parseInt(nroRM.trim()));
            rs = st.executeQuery();

            while (rs.next()) {
                Prescricao pr = new Prescricao();
                pr.setPosologia(rs.getString("posologia"));
                pr.setDataUsoInicial(rs.getDate("dataUsoInicial").toLocalDate());
                pr.setDataUsoFinal(rs.getDate("dataUsoFinal").toLocalDate());

                Medicamento m = new Medicamento();

                m.setCodMedicamento(rs.getString("codMedicamento"));
                m.setNome(rs.getString("nomeMed"));
                pr.setMedicamento(m);
                lista.add(pr);
            }
        } finally {
            ConexaoBD.close(rs);
            ConexaoBD.close(st);
            ConexaoBD.close(conn);
        }
        return lista;
    }

    //Consulta de Endereço

    @Override public List<Endereco> obterEnderecoPorCep(String cep) throws EnderecoException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        List<Endereco> listaEnderecos = new ArrayList<>();

        //JOIN para obter todos os dados numa única consulta
        String sql = "SELECT E.*, L.nomeLogradouro, T.nomeSiglaLogradouro, B.nomeBairro, C.nomeCidade, C.siglaUF " +
                "FROM Endereco E " +
                "JOIN Logradouro L ON E.idLogradouro = L.idLogradouro " +
                "JOIN TipoLogradouro T ON L.siglaLogradouro = T.siglaLogradouro " +
                "JOIN Bairro B ON E.idBairro = B.idBairro " +
                "JOIN Cidade C ON E.idCidade = C.idCidade " +
                "WHERE E.CEP = ?";

        try {
            conn = ConexaoBD.getConnection();
            if (conn == null) throw new SQLException("Erro de conexão com BD!");

            stmt = conn.prepareStatement(sql);
            stmt.setString(1, cep);
            rs = stmt.executeQuery();

            while (rs.next()) {
                Endereco enderecoEncontrado = new Endereco();
                enderecoEncontrado.setIdEndereco(rs.getInt("idEndereco"));
                enderecoEncontrado.setCEP(rs.getString("CEP"));

                //1. Objeto UF
                UF ufCompleta = new UF();
                ufCompleta.setSiglaUF(rs.getString("siglaUF"));

                //2. Objeto Cidade
                Cidade cidadeCompleta = new Cidade();
                cidadeCompleta.setIdCidade(rs.getInt("idCidade"));
                cidadeCompleta.setNomeCidade(rs.getString("nomeCidade"));
                cidadeCompleta.setUF(ufCompleta); // Composição
                enderecoEncontrado.setCidade(cidadeCompleta);

                //3. Objeto Bairro
                Bairro bairroCompleto = new Bairro();
                bairroCompleto.setIdBairro(rs.getInt("idBairro"));
                bairroCompleto.setNomeBairro(rs.getString("nomeBairro"));
                enderecoEncontrado.setBairro(bairroCompleto);

                //4. Objeto TipoLogradouro
                TipoLogradouro tipoLogradouroCompleto = new TipoLogradouro();
                tipoLogradouroCompleto.setNomeSiglaLogradouro(rs.getString("nomeSiglaLogradouro"));

                //5. Objeto Logradouro
                Logradouro logradouroCompleto = new Logradouro();
                logradouroCompleto.setIdLogradouro(rs.getInt("idLogradouro"));
                logradouroCompleto.setNomeLogradouro(rs.getString("nomeLogradouro"));
                logradouroCompleto.setTipoLogradouro(tipoLogradouroCompleto);
                enderecoEncontrado.setLogradouro(logradouroCompleto);

                //Adição do objeto Endereco populado à lista!!
                listaEnderecos.add(enderecoEncontrado);
            }

        } catch (SQLException e) {
            System.err.println("Erro SQL: " + e.getMessage());
            throw new EnderecoException("Erro de Infraestrutura ao buscar CEP: " + e.getMessage(), e);
        } finally {
            ConexaoBD.close(rs);
            ConexaoBD.close(stmt);
            ConexaoBD.close(conn);
        }
        return listaEnderecos;
    }

    @Override public Endereco obterEnderecoPorID(int id) throws EnderecoException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        Endereco enderecoEncontrado = null;

        //JOIN para obter todos os dados numa única consulta
        String sql = "SELECT E.*, L.nomeLogradouro, T.nomeSiglaLogradouro, B.nomeBairro, C.nomeCidade, C.siglaUF " +
                "FROM Endereco E " +
                "JOIN Logradouro L ON E.idLogradouro = L.idLogradouro " +
                "JOIN TipoLogradouro T ON L.siglaLogradouro = T.siglaLogradouro " +
                "JOIN Bairro B ON E.idBairro = B.idBairro " +
                "JOIN Cidade C ON E.idCidade = C.idCidade " +
                "WHERE E.idEndereco = ?";

        try {
            conn = ConexaoBD.getConnection();
            if (conn == null) throw new SQLException("Erro de conexão com BD!");

            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);
            rs = stmt.executeQuery();

            if (rs.next()) {
                enderecoEncontrado = new Endereco();
                enderecoEncontrado.setIdEndereco(rs.getInt("idEndereco"));
                enderecoEncontrado.setCEP(rs.getString("CEP"));

                //Objeto UF
                UF ufCompleta = new UF();
                ufCompleta.setSiglaUF(rs.getString("siglaUF"));

                //Objeto Cidade
                Cidade cidadeCompleta = new Cidade();
                cidadeCompleta.setIdCidade(rs.getInt("idCidade"));
                cidadeCompleta.setNomeCidade(rs.getString("nomeCidade"));
                cidadeCompleta.setUF(ufCompleta);
                enderecoEncontrado.setCidade(cidadeCompleta);

                //Objeto Bairro
                Bairro bairroCompleto = new Bairro();
                bairroCompleto.setIdBairro(rs.getInt("idBairro"));
                bairroCompleto.setNomeBairro(rs.getString("nomeBairro"));
                enderecoEncontrado.setBairro(bairroCompleto);

                //Objeto TipoLogradouro
                TipoLogradouro tipoLogradouroCompleto = new TipoLogradouro();
                tipoLogradouroCompleto.setNomeSiglaLogradouro(rs.getString("nomeSiglaLogradouro"));

                //Objeto Logradouro
                Logradouro logradouroCompleto = new Logradouro();
                logradouroCompleto.setIdLogradouro(rs.getInt("idLogradouro"));
                logradouroCompleto.setNomeLogradouro(rs.getString("nomeLogradouro"));
                logradouroCompleto.setTipoLogradouro(tipoLogradouroCompleto);
                enderecoEncontrado.setLogradouro(logradouroCompleto);
            }

        } catch (SQLException e) {
            System.err.println("Erro SQL: " + e.getMessage());
            throw new EnderecoException("Erro de Infraestrutura ao buscar id: " + e.getMessage(), e);

        } finally {
            ConexaoBD.close(rs);
            ConexaoBD.close(stmt);
            ConexaoBD.close(conn);
        }
        return enderecoEncontrado;
    }

    @Override public Cidade obterCidadePorId(int idCidade) throws EnderecoException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        Cidade cidadeEncontrada = null;

        String sql = "SELECT nomeCidade, siglaUF FROM Cidade WHERE idCidade = ?";

        try {
            conn = ConexaoBD.getConnection();
            if (conn == null) throw new SQLException("Erro de conexão com BD!");

            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, idCidade);
            rs = stmt.executeQuery();

            if (rs.next()) {
                cidadeEncontrada = new Cidade();
                cidadeEncontrada.setIdCidade(idCidade);
                cidadeEncontrada.setNomeCidade(rs.getString("nomeCidade"));

                UF ufCompleta = new UF();
                ufCompleta.setSiglaUF(rs.getString("siglaUF"));
                cidadeEncontrada.setUF(ufCompleta);
            }

        } catch (SQLException e) {
            System.err.println("Erro SQL: " + e.getMessage());
            throw new EnderecoException("Erro ao buscar Cidade por ID: " + e.getMessage(), e);

        } finally {
            ConexaoBD.close(rs);
            ConexaoBD.close(stmt);
            ConexaoBD.close(conn);
        }
        return cidadeEncontrada;
    }

    @Override public Endereco obterEnderecoPorCepViaCEP(String cep) throws EnderecoException {
        //Limpeza do CEP e URL
        String cepLimpo = cep.replaceAll("[^0-9]", "");
        String urlString = "https://viacep.com.br/ws/" + cepLimpo + "/json/";

        try {
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);

            if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new EnderecoException("Erro ao consultar ViaCEP. Código: " + conn.getResponseCode());
            }

            //Leitura da Resposta JSON
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String inputLine;

            StringBuffer response = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            conn.disconnect();

            String jsonResponse = response.toString();
            ObjectMapper localMapper = new ObjectMapper();

            //Mapeamento do JSON (lido para um mapa de strings)
            Map<String, String> data = localMapper.readValue(jsonResponse, Map.class);

            //Verifica Erro
            if (data.containsKey("erro") && "true".equals(data.get("erro"))) {
                return null; //CEP não encontrado pelo ViaCEP!
            }

            //Conversão do Mapa para o BO Endereco
            Endereco enderecoViaCep = new Endereco();
            enderecoViaCep.setCEP(data.get("cep").replaceAll("[^0-9]", ""));

            //Objeto UF
            UF uf = new UF();
            uf.setSiglaUF(data.get("uf"));

            //Objeto Cidade
            Cidade cidade = new Cidade();
            cidade.setNomeCidade(data.get("localidade"));
            cidade.setUF(uf);
            enderecoViaCep.setCidade(cidade);

            //Objeto Bairro
            Bairro bairro = new Bairro();
            bairro.setNomeBairro(data.get("bairro"));
            enderecoViaCep.setBairro(bairro);

            //Objeto TipoLogradouro (ViaCEP não informa o Tipo, usamos um padrão 'Rua')
            TipoLogradouro tipoLogradouro = new TipoLogradouro();
            tipoLogradouro.setNomeSiglaLogradouro("Rua");

            // Objeto Logradouro
            Logradouro logradouro = new Logradouro();
            logradouro.setNomeLogradouro(data.get("logradouro"));
            logradouro.setTipoLogradouro(tipoLogradouro);
            enderecoViaCep.setLogradouro(logradouro);

            return enderecoViaCep;

        } catch (IOException e) {
            System.err.println("Erro de conexão ao ViaCEP: " + e.getMessage());
            throw new EnderecoException("Erro de Infraestrutura: Falha de conexão com a API ViaCEP.", e);
        } catch (Exception e) {
            System.err.println("Erro de processamento do JSON ViaCEP: " + e.getMessage());
            throw new EnderecoException("Erro ao processar dados do ViaCEP. Detalhe: " + e.getMessage(), e);
        }
    }
}