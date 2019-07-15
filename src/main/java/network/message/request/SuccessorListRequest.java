package network.message.request;

import network.message.reply.SuccessorListReply;
import node.LocalNode;

public class SuccessorListRequest extends RequestMessage {
    @Override
    public SuccessorListReply handleRequest(LocalNode node) {
        SuccessorListReply reply = new SuccessorListReply(node.getSuccessorsList());
        reply.setRequestId(getRequestId());
        return reply;
    }
}
