package unioeste.geral.endereco.bo;
import java.io.Serializable;

public class Medicamento implements Serializable{
    private int codMedicamento;
    private String nome;

    public Medicamento(){}

    public void setCodMedicamento(int codMedicamento) {
        this.codMedicamento = codMedicamento;
    }
    public int getCodMedicamento() {
        return codMedicamento;
    }
    public void setNome(String nome) {
        this.nome = nome;
    }
    public String getNome() {
        return nome;
    }
}
