package unioeste.geral.endereco.bo;
import java.io.Serializable;

public class Medico implements Serializable{
    private int idMedico;
    private String CRM;
    private String nome;
    private String CPF;
    private int nroMoradia;
    private String complemento;
    private Endereco endereco;

    public Medico(){}

    public void setIdMedico(int idMedico) {
        this.idMedico = idMedico;
    }
    public int getIdMedico() {
        return idMedico;
    }
    public void setCRM(String CRM) {
        this.CRM = CRM;
    }
    public String getCRM() {
        return CRM;
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
