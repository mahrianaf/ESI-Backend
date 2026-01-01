package unioeste.geral.endereco.bo;
import java.io.Serializable;
import java.time.LocalDate;

public class Prescricao implements Serializable{
    private LocalDate dataUsoInicial;
    private LocalDate dataUsoFinal;
    private String posologia;
    private Medicamento medicamento;
    private ReceitaMedica receitaMedica;

    public Prescricao(){}

    public void setDataUsoInicial(LocalDate dataUsoInicial) {
        this.dataUsoInicial = dataUsoInicial;
    }
    public LocalDate getDataUsoInicial() {
        return dataUsoInicial;
    }
    public void setDataUsoFinal(LocalDate dataUsoFinal) {
        this.dataUsoFinal = dataUsoFinal;
    }
    public LocalDate getDataUsoFinal() {
        return dataUsoFinal;
    }
    public void setPosologia(String posologia) {
        this.posologia = posologia;
    }
    public String getPosologia() {
        return posologia;
    }
    public void setMedicamento(Medicamento medicamento) {
        this.medicamento = medicamento;
    }
    public Medicamento getMedicamento() {
        return medicamento;
    }
    public void setReceitaMedica(ReceitaMedica receitaMedica) {
        this.receitaMedica = receitaMedica;
    }
    public ReceitaMedica getReceitaMedica() {
        return receitaMedica;
    }
}
