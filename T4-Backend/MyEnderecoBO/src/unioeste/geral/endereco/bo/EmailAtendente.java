package unioeste.geral.endereco.bo;
import java.io.Serializable;

public class EmailAtendente implements Serializable{
    private Atendente atendente;
    private String enderecoEmail;

    public EmailAtendente(){}

    public void setAtendente(Atendente atendente) {
        this.atendente = atendente;
    }
    public Atendente getAtendente() {
        return atendente;
    }
    public void setEnderecoEmail(String enderecoEmail) {
        this.enderecoEmail = enderecoEmail;
    }
    public String getEnderecoEmail() {
        return enderecoEmail;
    }
}
