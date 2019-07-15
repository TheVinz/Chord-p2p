package network.message.reply;

public class PingReply extends ReplyMessage {
    private boolean hasFailed;


    public PingReply() {
        hasFailed = false;
    }

    public boolean isHasFailed() {
        return hasFailed;
    }
}
