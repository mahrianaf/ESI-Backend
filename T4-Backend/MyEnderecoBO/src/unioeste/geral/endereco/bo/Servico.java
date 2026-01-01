package unioeste.geral.endereco.bo;
import java.io.Serializable;

public class Servico implements Serializable{
    private int codServico;
    private String nomeTipo;
    private float valor;
    private OrdemServico ordemServico;

    public Servico(){}

    public void setCodServico(int codServico) {
        this.codServico = codServico;
    }
    public int getCodServico() {
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
    public void setOrdemservico(OrdemServico ordemservico) {
        this.ordemServico = ordemservico;
    }
    public OrdemServico getOrdemservico() {
        return ordemServico;
    }
}
