package unioeste.geral.endereco.dao;
import com.fasterxml.jackson.databind.ObjectMapper;
import unioeste.geral.endereco.bo.*;

import unioeste.apoio.BD.ConexaoBD;
import unioeste.geral.endereco.exception.EnderecoException;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

public class EnderecoDAO_implementation implements EnderecoDAO {

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

                //Atencao pois ele esta setando no manuallll
                stTel.setInt(3, 1); // ID 1 = Brasil (DDI 55)
                stTel.setInt(4, 1); // ID 1 = Foz do Iguaçu (DDD 45)
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