package unioeste.geral.endereco.exception;

//Estende RuntimeException
public class EnderecoException extends RuntimeException {

    //Recebe a mensagem de erro (motivo do erro de negócio)
    public EnderecoException(String message) {
        super(message);
    }

    //Recebe a mensagem e a causa original
    public EnderecoException(String message, Throwable cause) {
        super(message, cause);
    }
}