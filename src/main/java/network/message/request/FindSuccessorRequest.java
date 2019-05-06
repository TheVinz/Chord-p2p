package network.message.request;

import network.exceptions.NetworkFailureException;
import network.message.reply.NodeReply;
import node.LocalNode;
import node.Node;

public class FindSuccessorRequest extends RequestMessage {

    private int id;

    public FindSuccessorRequest(int id){
        this.id=id;
    }

    public int getId() {
        return id;
    }

    @Override
    public NodeReply handleRequest(LocalNode node) {
        try {
            Node n = node.findSuccessor(id);
            NodeReply reply = new NodeReply(n);
            reply.setRequestId(getRequestId());
            return reply;
        } catch (NetworkFailureException e) {
            e.printStackTrace();
            return null;
        }
    }

}
