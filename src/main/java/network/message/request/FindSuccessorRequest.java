package network.message.request;

import network.exeptions.NetworkFailureException;
import network.message.reply.NodeReply;
import node.LocalNode;
import node.Node;
import node.exceptions.NodeNotFoundException;

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
        } catch (NodeNotFoundException | NetworkFailureException e) {
            e.printStackTrace();
            return null;
        }
    }

}
