package network.message.request;

import network.message.reply.NodeReply;
import node.LocalNode;

public class GetSuccessorRequest extends RequestMessage{

    @Override
    public NodeReply handleRequest(LocalNode node) {
        NodeReply reply = new NodeReply(node.getSuccessor());
        reply.setRequestId(getRequestId());
        return reply;
    }
}
