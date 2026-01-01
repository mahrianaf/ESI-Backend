package unioeste.geral.endereco.bo;

public interface Pessoa {
    int getID();
    void setID(int id);
    String getNome();
    String getCPF();
    int getNroMoradia();
    String getComplemento();
    Endereco getEndereco();
    void setEndereco(Endereco endereco);
    void setNome(String nome);
    void setCPF(String cpf);
    void setNroMoradia(int nro);
    void setComplemento(String complemento);
}