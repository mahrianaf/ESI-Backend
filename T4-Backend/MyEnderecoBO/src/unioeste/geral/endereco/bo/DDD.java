package unioeste.geral.endereco.bo;
import java.io.Serializable;

public class DDD implements Serializable{
    private int idDDD;
    private int ddd;

    public DDD(){}

    public void setIdDDD(int idDDD) {
        this.idDDD = idDDD;
    }
    public int getIdDDD() {
        return idDDD;
    }
    public void setDdd(int ddd) {
        this.ddd = ddd;
    }
    public int getDdd() {
        return ddd;
    }
}