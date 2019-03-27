package network.message;

public class RequestMessage extends Message{
    public static final long serialVersionUID = 46513505L;

    public RequestMessage(int method) {
        super(method);
    }

    public RequestMessage(int method, int id) {
        super(method, id);
    }

    public RequestMessage(int method, String ip, int port, int id) {
        super(method, ip, port, id);
    }
}
