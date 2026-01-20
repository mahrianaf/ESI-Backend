package unioeste.geral.endereco.bo;
import java.io.Serializable;

public class CID implements Serializable{
    private String codCID;
    private String nome;

    public CID(){}

    public void setCodCID(String codCID) {
        this.codCID = codCID;
    }
    public String getCodCID() {
        return codCID;
    }
    public void setNome(String nome) {
        this.nome = nome;
    }
    public String getNome() {
        return nome;
    }
}
