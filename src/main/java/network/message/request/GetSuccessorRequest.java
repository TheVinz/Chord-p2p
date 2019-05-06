package network.message.request;

import network.exceptions.NetworkFailureException;
import network.message.reply.NodeReply;
import node.LocalNode;

public class GetSuccessorRequest extends RequestMessage {

    @Override
    public NodeReply handleRequest(LocalNode node) {
        NodeReply reply = null;
        try {
            reply = new NodeReply(node.getSuccessor());
            reply.setRequestId(getRequestId());
        } catch (NetworkFailureException e) {
            e.printStackTrace();
        }
        return reply;
    }
}
