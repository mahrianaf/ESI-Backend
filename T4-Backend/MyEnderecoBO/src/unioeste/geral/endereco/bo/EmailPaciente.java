package unioeste.geral.endereco.bo;
import java.io.Serializable;

public class EmailPaciente implements Serializable{
    private Paciente paciente;
    private String enderecoEmail;

    public EmailPaciente(){}

    public void setPaciente(Paciente paciente) {
        this.paciente = paciente;
    }
    public Paciente getPaciente() {
        return paciente;
    }
    public void setEnderecoEmail(String enderecoEmail) {
        this.enderecoEmail = enderecoEmail;
    }
    public String getEnderecoEmail() {
        return enderecoEmail;
    }
}
