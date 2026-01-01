package unioeste.geral.endereco.bo;
import java.io.Serializable;

public class Cidade implements Serializable{
    private int idCidade;
    private String nomeCidade;
    private UF uf;

    public Cidade(){}

    public void setIdCidade(int idCidade) {
        this.idCidade = idCidade;
    }
    public int getIdCidade() {
        return idCidade;
    }
    public void setNomeCidade(String nomeCidade) {
        this.nomeCidade = nomeCidade;
    }
    public String getNomeCidade() {
        return nomeCidade;
    }
    public UF getUF() {
        return this.uf;
    }
    public void setUF(UF uf) {
        this.uf = uf;
    }
}
