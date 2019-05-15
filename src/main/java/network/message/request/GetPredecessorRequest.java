package network.message.request;

import network.message.reply.NodeReply;
import node.LocalNode;
import node.Node;

public class GetPredecessorRequest extends RequestMessage{

    @Override
    public NodeReply handleRequest(LocalNode node) {
        Node n = node._getPredecessor();
        NodeReply reply;
        if(n == null)
            reply = new NodeReply();
        else
            reply = new NodeReply(n);
        reply.setRequestId(getRequestId());
        return reply;
    }
}
