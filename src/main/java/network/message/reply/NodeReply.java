package network.message.reply;

import node.Node;

public class NodeReply extends ReplyMessage {

    private final int id;
    private final String ip;
    private final int port;

    public NodeReply(Node n){
        this.id=n.getId();
        this.ip=n.getIp();
        this.port=n.getPort();
    }

    public int getId() {
        return id;
    }

    public int getPort() {
        return port;
    }

    public String getIp() {
        return ip;
    }
}
