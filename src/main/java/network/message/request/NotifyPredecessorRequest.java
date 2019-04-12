package network.message.request;

import network.message.reply.ReplyMessage;
import network.remoteNode.RemoteNode;
import node.LocalNode;
import node.Node;

public class NotifyPredecessorRequest extends RequestMessage {

    private final int id;
    private final String ip;
    private final int port;

    public NotifyPredecessorRequest(Node n){
        this.id=n.getId();
        this.ip=n.getIp();
        this.port=n.getPort();
    }

    @Override
    public ReplyMessage handleRequest(LocalNode node) {
        node.notifyPredecessor(new RemoteNode(id, ip, port));
        return null;
    }
}
