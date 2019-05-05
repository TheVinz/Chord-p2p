package network.message.reply;

import node.Node;

public class NodeReply extends ReplyMessage {

    private final int id;
    private final String ip;
    private final int port;
    private final boolean notFound;

    public NodeReply(Node n){
        this.id=n.getId();
        this.ip=n.getIp();
        this.port=n.getPort();
        this.notFound = false;
    }

    public NodeReply(){
        this.notFound = true;
        this.id=-1;
        this.ip=null;
        this.port=-1;
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

    public boolean isNotFound(){
        return notFound;
    }
}
