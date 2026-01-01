package unioeste.geral.endereco.bo;
import java.io.Serializable;

public class Endereco implements Serializable{
    private int idEndereco;
    private String CEP;
    private Cidade cidade;
    private Logradouro logradouro;
    private Bairro bairro;

    public Endereco(){}

    public void setIdEndereco(int idEndereco) {
        this.idEndereco = idEndereco;
    }
    public int getIdEndereco() {
        return idEndereco;
    }
    public void setCEP(String CEP) {
        this.CEP = CEP;
    }
    public String getCEP() {
        return CEP;
    }
    public Cidade getCidade() {
        return this.cidade;
    }
    public void setCidade(Cidade cidade) {
        this.cidade = cidade;
    }
    public Logradouro getLogradouro() {
        return this.logradouro;
    }
    public void setLogradouro(Logradouro logradouro) {
        this.logradouro = logradouro;
    }
    public Bairro getBairro() {
        return this.bairro;
    }
    public void setBairro(Bairro bairro) {
        this.bairro = bairro;
    }
}
