package unioeste.geral.endereco.bo;
import java.io.Serializable;

public class EmailMedico implements Serializable{
    private Medico medico;
    private String enderecoEmail;

    public EmailMedico(){}

    public void setMedico(Medico medico) {
        this.medico = medico;
    }
    public Medico getMedico() {
        return medico;
    }
    public void setEnderecoEmail(String enderecoEmail) {
        this.enderecoEmail = enderecoEmail;
    }
    public String getEnderecoEmail() {
        return enderecoEmail;
    }
}
