package network.message;

public class ReplyMessage extends Message{
    public static final long serialVersionUID = 46513505L;

    public ReplyMessage(int method) {
        super(method);
    }

    public ReplyMessage(int method, String ip, int port, int id) {
        super(method, ip, port, id);
    }
}
