package network.message.reply;

public class NotifyPropagationReply extends ReplyMessage {
    private boolean present;

    public NotifyPropagationReply(boolean present) {
        this.present = present;
    }

    public boolean isPresent() {
        return present;
    }
}
