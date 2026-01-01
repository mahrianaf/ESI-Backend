package unioeste.geral.endereco.bo;
import java.io.Serializable;

public class TelefonePaciente implements Serializable{
    private Paciente paciente;
    private DDI ddi;
    private DDD ddd;
    private String nroTelefone;

    public TelefonePaciente(){}

    public void setPaciente(Paciente paciente) {
        this.paciente = paciente;
    }
    public Paciente getPaciente() {
        return paciente;
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
