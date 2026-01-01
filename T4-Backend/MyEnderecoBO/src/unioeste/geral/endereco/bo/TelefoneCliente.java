package unioeste.geral.endereco.bo;
import java.io.Serializable;

public class TelefoneCliente implements Serializable{
    private Cliente cliente;
    private DDI ddi;
    private DDD ddd;
    private String nroTelefone;

    public TelefoneCliente(){}

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }
    public Cliente getCliente() {
        return cliente;
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
