package network.exeptions;

public class NetworkFailureException extends Throwable {
    private String message;

    public NetworkFailureException(){
        super();
    }

    public NetworkFailureException(String message){
        this.message=message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
