package unioeste.geral.endereco.bo;
import java.io.Serializable;

public class CID implements Serializable{
    private int codCID;
    private String nome;

    public CID(){}

    public void setCodCID(int codCID) {
        this.codCID = codCID;
    }
    public int getCodCID() {
        return codCID;
    }
    public void setNome(String nome) {
        this.nome = nome;
    }
    public String getNome() {
        return nome;
    }
}
