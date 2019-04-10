package network.message;

import network.remoteNode.RemoteNode;
import node.LocalNode;
import node.Node;
import node.exceptions.NodeNotFoundException;

public class RequestMessage extends Message{
    public static final long serialVersionUID = 46513505L;

    RequestMessage(){}

    public RequestMessage(int method) {
        super(method);
    }

    public RequestMessage(int method, int id) {
        super(method, id);
    }

    public RequestMessage(int method, String ip, int port, int id) {
        super(method, ip, port, id);
    }

    public ReplyMessage handleRequest(LocalNode node){
        ReplyMessage reply=new ReplyMessage();
        Node n;
        switch (method) {
            case Message.FIND_SUCCESSOR:
                try {
                    n = node.findSuccessor(id);
                    reply = new ReplyMessage(method, n.getIp(), n.getPort(), n.getId());
                    reply.setRequestId(getRequestId());
                } catch (NodeNotFoundException e) {
                    e.printStackTrace();
                }
                break;
            case Message.GET_PREDECESSOR:
                n=node.getPredecessor();
                reply = new ReplyMessage(method, n.getIp(), n.getPort(), n.getId());
                reply.setRequestId(getRequestId());
                break;
            case Message.GET_SUCCESSOR:
                n = node.getSuccessor();
                reply = new ReplyMessage(method, n.getIp(), n.getPort(), n.getId());
                reply.setRequestId(getRequestId());
                break;
            case Message.NOTIFY_PREDECESSOR:
                n = new RemoteNode(id, ip, port);
                node.notifyPredecessor(n);
                break;
        }
        reply.setRequestId(getRequestId());
        return reply;
    }
}
