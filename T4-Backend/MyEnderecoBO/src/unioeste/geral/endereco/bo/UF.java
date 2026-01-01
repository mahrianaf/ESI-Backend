package unioeste.geral.endereco.bo;
import java.io.Serializable;

public class UF implements Serializable{
    private String siglaUF;
    private String nomeUF;

    public UF(){}

    public void setSiglaUF(String siglaUF) {
        this.siglaUF = siglaUF;
    }
    public String getSiglaUF() {
        return siglaUF;
    }
    public void setNomeUF(String nomeUF) {
        this.nomeUF = nomeUF;
    }
    public String getNomeUF() {
        return nomeUF;
    }
}
