package network.message.request;

import network.exeptions.NetworkFailureException;
import network.message.reply.SuccessorListReply;
import node.LocalNode;

public class SuccessorListRequest extends RequestMessage {
    @Override
    public SuccessorListReply handleRequest(LocalNode node) {
        SuccessorListReply reply = null;
        try {
            reply = new SuccessorListReply(node.getSuccessorsList());
            reply.setRequestId(getRequestId());
        } catch (NetworkFailureException e) {
            e.printStackTrace();
        }
        return reply;
    }
}
