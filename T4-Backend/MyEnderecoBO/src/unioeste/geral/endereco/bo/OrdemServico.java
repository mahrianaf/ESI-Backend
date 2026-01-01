package unioeste.geral.endereco.bo;
import java.io.Serializable;
import java.time.LocalDate;

public class OrdemServico implements Serializable{
    private int nroOrdemServico;
    private LocalDate dataEmissaoOS;
    private String descricaoProblema;
    private float total;
    private Cliente cliente;
    private Atendente atendente;

    public OrdemServico(){}

    public void setNroOrdemServico(int nroOrdemServico) {
        this.nroOrdemServico = nroOrdemServico;
    }
    public int getNroOrdemServico() {
        return nroOrdemServico;
    }
    public void setDataEmissaoOS(LocalDate dataEmissaoOS) {
        this.dataEmissaoOS = dataEmissaoOS;
    }
    public LocalDate getDataEmissaoOS() {
        return dataEmissaoOS;
    }
    public void setDescricaoProblema(String descricaoProblema) {
        this.descricaoProblema = descricaoProblema;
    }
    public String getDescricaoProblema() {
        return descricaoProblema;
    }
    public void setTotal(float total) {
        this.total = total;
    }
    public float getTotal() {
        return total;
    }
    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }
    public Cliente getCliente() {
        return cliente;
    }
    public void setAtendente(Atendente atendente) {
        this.atendente = atendente;
    }
    public Atendente getAtendente() {
        return atendente;
    }
}
