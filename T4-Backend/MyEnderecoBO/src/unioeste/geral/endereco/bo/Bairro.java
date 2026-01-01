package unioeste.geral.endereco.bo;
import java.io.Serializable;

public class Bairro implements Serializable{
    private int idBairro;
    private String nomeBairro;

    public Bairro(){}

    public void setIdBairro(int idBairro) {
        this.idBairro = idBairro;
    }
    public int getIdBairro() {
        return idBairro;
    }
    public void setNomeBairro(String nomeBairro) {
        this.nomeBairro = nomeBairro;
    }
    public String getNomeBairro() {
        return nomeBairro;
    }
}
