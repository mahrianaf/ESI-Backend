package unioeste.geral.endereco.bo;
import java.io.Serializable;

public class Servico implements Serializable{
    private String codServico;
    private String nomeTipo;
    private float valor;

    public Servico(){}

    public void setCodServico(String codServico) {
        this.codServico = codServico;
    }
    public String getCodServico() {
        return codServico;
    }
    public void setNomeTipo(String nomeTipo) {
        this.nomeTipo = nomeTipo;
    }
    public String getNomeTipo() {
        return nomeTipo;
    }
    public void setValor(float valor) {
        this.valor = valor;
    }
    public float getValor() {
        return valor;
    }
}
