package unioeste.geral.endereco.bo;
import java.io.Serializable;

public class Cliente implements Pessoa, Serializable{
    private int codCliente;
    private String nome;
    private String CPF;
    private int nroMoradia;
    private String complemento;
    private Endereco endereco;

    public Cliente(){}

    @Override public String getNome() {
        return nome;
    }
    @Override public String getCPF() {
        return CPF;
    }
    @Override public int getNroMoradia() {
        return nroMoradia;
    }
    @Override public String getComplemento() {
        return complemento;
    }
    @Override public Endereco getEndereco() {
        return endereco;
    }
    @Override
    public int getID() {
        return this.codCliente; // Mapeia o getID para o seu codCliente
    }
    @Override
    public void setID(int id) {
        this.codCliente = id; // Mapeia o setID para o seu codCliente
    }
    public void setNome(String nome) {
        this.nome = nome;
    }
    public void setCPF(String CPF) {
        this.CPF = CPF;
    }
    public void setNroMoradia(int nroMoradia) {
        this.nroMoradia = nroMoradia;
    }
    public void setComplemento(String complemento) {
        this.complemento = complemento;
    }
    public void setEndereco(Endereco endereco) {
        this.endereco = endereco;
    }

}
