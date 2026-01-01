package unioeste.geral.endereco.bo;
import java.io.Serializable;
import java.time.LocalDate;

public class ReceitaMedica implements Serializable{
    private int nroReceita;
    private LocalDate dataEmissao;
    private CID cid;
    private Paciente paciente;
    private Medico medico;

    public ReceitaMedica(){}

    public void setNroReceita(int nroReceita) {
        this.nroReceita = nroReceita;
    }
    public int getNroReceita() {
        return nroReceita;
    }
    public void setDataEmissao(LocalDate dataEmissao) {
        this.dataEmissao = dataEmissao;
    }
    public LocalDate getDataEmissao() {
        return dataEmissao;
    }
    public void setCid(CID cid) {
        this.cid = cid;
    }
    public CID getCid() {
        return cid;
    }
    public void setPaciente(Paciente paciente) {
        this.paciente = paciente;
    }
    public Paciente getPaciente() {
        return paciente;
    }
    public void setMedico(Medico medico) {
        this.medico = medico;
    }
    public Medico getMedico() {
        return medico;
    }
}
