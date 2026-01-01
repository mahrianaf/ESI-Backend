package unioeste.geral.endereco.bo;
import java.io.Serializable;

public class TelefoneMedico implements Serializable{
    private Medico medico;
    private DDI ddi;
    private DDD ddd;
    private String nroTelefone;

    public TelefoneMedico(){}

    public void setMedico(Medico medico) {
        this.medico = medico;
    }
    public Medico getMedico() {
        return medico;
    }
    public void setDdi(DDI ddi) {
        this.ddi = ddi;
    }
    public DDI getDdi() {
        return ddi;
    }
    public void setDdd(DDD ddd) {
        this.ddd = ddd;
    }
    public DDD getDdd() {
        return ddd;
    }
    public void setNroTelefone(String nroTelefone) {
        this.nroTelefone = nroTelefone;
    }
    public String getNroTelefone() {
        return nroTelefone;
    }
}
