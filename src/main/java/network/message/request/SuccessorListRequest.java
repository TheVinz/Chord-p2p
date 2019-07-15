package network.message.request;

import network.message.reply.SuccessorListReply;
import node.LocalNode;
import node.Node;

import java.util.List;

public class SuccessorListRequest extends RequestMessage {
    @Override
    public SuccessorListReply handleRequest(LocalNode node) {
        List<Node> successorList = node.getSuccessorsList();
        SuccessorListReply reply;
        if(successorList == null)
            reply = new SuccessorListReply();
        else
            reply = new SuccessorListReply(successorList);
        reply.setRequestId(getRequestId());
        return reply;
    }
}
