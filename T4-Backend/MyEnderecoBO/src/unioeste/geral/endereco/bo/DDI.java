package unioeste.geral.endereco.bo;
import java.io.Serializable;

public class DDI implements Serializable{
    private int idDDI;
    private int ddi;

    public DDI(){}

    public void setIdDDI(int idDDI) {
        this.idDDI = idDDI;
    }
    public int getIdDDI() {
        return idDDI;
    }
    public void setDdi(int ddi) {
        this.ddi = ddi;
    }
    public int getDdi() {
        return ddi;
    }
}
