package unioeste.geral.endereco.bo;
import java.io.Serializable;

public class Logradouro implements Serializable{
    private int idLogradouro;
    private String nomeLogradouro;
    private TipoLogradouro tipoLogradouro;

    public Logradouro(){}

    public void setIdLogradouro(int idLogradouro) {
        this.idLogradouro = idLogradouro;
    }
    public int getIdLogradouro() {
        return idLogradouro;
    }
    public void setNomeLogradouro(String nomeLogradouro) {
        this.nomeLogradouro = nomeLogradouro;
    }
    public String getNomeLogradouro() {
        return nomeLogradouro;
    }
    public TipoLogradouro getTipoLogradouro() {
        return this.tipoLogradouro;
    }
    public void setTipoLogradouro(TipoLogradouro tipoLogradouro) {
        this.tipoLogradouro = tipoLogradouro;
    }
}
