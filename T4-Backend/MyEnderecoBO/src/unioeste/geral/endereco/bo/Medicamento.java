package unioeste.geral.endereco.bo;
import java.io.Serializable;

public class Medicamento implements Serializable{
    private String codMedicamento;
    private String nome;

    public Medicamento(){}

    public void setCodMedicamento(String codMedicamento) {
        this.codMedicamento = codMedicamento;
    }
    public String getCodMedicamento() {
        return codMedicamento;
    }
    public void setNome(String nome) {
        this.nome = nome;
    }
    public String getNome() {
        return nome;
    }
}
