package unioeste.geral.endereco.bo;
import java.io.Serializable;

public class ServicoOS implements Serializable{
    private Servico servico;
    private OrdemServico ordemservico;

    public ServicoOS(){}

    public void setServico(Servico servico) {
        this.servico = servico;
    }
    public Servico getServico() {
        return servico;
    }
    public void setOrdemservico(OrdemServico ordemservico) {
        this.ordemservico = ordemservico;
    }
    public OrdemServico getOrdemservico() {
        return ordemservico;
    }
}
