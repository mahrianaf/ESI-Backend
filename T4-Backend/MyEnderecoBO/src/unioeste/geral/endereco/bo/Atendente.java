package unioeste.geral.endereco.bo;
import java.io.Serializable;

public class Atendente implements Serializable{
    private int codAtendente;
    private String nome;

    public Atendente(){}

    public void setCodAtendente(int codAtendente) {
        this.codAtendente = codAtendente;
    }
    public int getCodAtendente() {
        return codAtendente;
    }
    public void setNome(String nome) {
        this.nome = nome;
    }
    public String getNome() {
        return nome;
    }
}
