package unioeste.geral.endereco.bo;
import java.io.Serializable;

public class EmailCliente implements Serializable{
    private Cliente cliente;
    private String enderecoEmail;

    public EmailCliente(){}

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }
    public Cliente getCliente() {
        return cliente;
    }
    public void setEnderecoEmail(String enderecoEmail) {
        this.enderecoEmail = enderecoEmail;
    }
    public String getEnderecoEmail() {
        return enderecoEmail;
    }
}
