package unioeste.geral.endereco.bo;
import java.io.Serializable;

public class Atendente implements Serializable{
    private int codAtendente;
    private String nome;
    private String CPF;
    private int nroMoradia;
    private String complemento;
    private Endereco endereco;


    public Atendente(){}

    public void setCodAtendente(int codAtendente) {
        this.codAtendente = codAtendente;
    }
    public int getCodAtendente() {
        return codAtendente;
    }
    public void setNome(String nome) {
        this.nome = nome;
    }
    public String getNome() {
        return nome;
    }
    public void setCPF(String CPF) {
        this.CPF = CPF;
    }
    public String getCPF() {
        return CPF;
    }
    public void setNroMoradia(int nroMoradia) {
        this.nroMoradia = nroMoradia;
    }
    public int getNroMoradia() {
        return nroMoradia;
    }
    public void setComplemento(String complemento) {
        this.complemento = complemento;
    }
    public String getComplemento() {
        return complemento;
    }
    public void setEndereco(Endereco endereco) {
        this.endereco = endereco;
    }
    public Endereco getEndereco() {
        return endereco;
    }
}
