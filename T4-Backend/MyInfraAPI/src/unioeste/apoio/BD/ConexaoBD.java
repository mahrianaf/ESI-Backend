package unioeste.apoio.BD;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.Statement;

public class ConexaoBD {
    private static final String url = "jdbc:mysql://localhost:3306/servicos";
    private static final String user = "root";
    private static final String senha = "senha";
    private static Connection conn;

    public static Connection getConnection(){
        try {
            //Garante o carregamento do driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            if (conn == null || conn.isClosed()) {
                conn = DriverManager.getConnection(url, user, senha);
                System.out.println("Conexão estabelecida com sucesso!");
            }
        } catch (ClassNotFoundException e) {
            System.err.println("Erro: Driver JDBC não encontrado!");
            e.printStackTrace();
            return null;
        } catch (SQLException e) {
            System.err.println("Erro ao estabelecer a conexão: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
        return conn;
    }
    public static void close(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                System.err.println("Erro ao fechar a conexão: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    public static void close(Statement stmt) {
        if (stmt != null) {
            try {
                stmt.close();
            } catch (SQLException e) {
                System.err.println("Erro ao fechar Statement: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    public static void close(ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                System.err.println("Erro ao fechar ResultSet: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
}
