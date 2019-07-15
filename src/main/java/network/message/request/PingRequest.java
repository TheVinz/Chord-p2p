package network.message.request;

import network.message.reply.PingReply;
import network.message.reply.ReplyMessage;
import node.LocalNode;

public class PingRequest extends RequestMessage{

    @Override
    public ReplyMessage handleRequest(LocalNode node) {
        return new PingReply();
    }
}
