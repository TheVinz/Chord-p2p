package network.message.request;

import network.exeptions.NetworkFailureException;
import network.message.reply.NodeReply;
import node.LocalNode;
import node.Node;

public class GetPredecessorRequest extends RequestMessage{

    @Override
    public NodeReply handleRequest(LocalNode node) {
        Node n = null;
        try {
            n = node.getPredecessor();
        } catch (NetworkFailureException e) {
            e.printStackTrace();
        }
        NodeReply reply = new NodeReply(n);
        reply.setRequestId(getRequestId());
        return reply;
    }
}
