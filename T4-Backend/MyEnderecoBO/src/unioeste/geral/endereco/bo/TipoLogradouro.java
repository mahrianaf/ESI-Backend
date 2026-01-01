package unioeste.geral.endereco.bo;
import java.io.Serializable;

public class TipoLogradouro implements Serializable{
    private String siglaLogradouro;
    private String nomeSiglaLogradouro;

    public TipoLogradouro(){}

    public void setSiglaLogradouro(String siglaLogradouro) {
        this.siglaLogradouro = siglaLogradouro;
    }
    public String getSiglaLogradouro() {
        return siglaLogradouro;
    }
    public void setNomeSiglaLogradouro(String nomeSiglaLogradouro) {
        this.nomeSiglaLogradouro = nomeSiglaLogradouro;
    }
    public String getNomeSiglaLogradouro() {
        return nomeSiglaLogradouro;
    }
}
