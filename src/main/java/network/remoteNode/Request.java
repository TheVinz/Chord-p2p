package network.remoteNode;

import network.message.reply.ReplyMessage;

import java.util.Calendar;


class Request {

    private final int requestId;
    private ReplyMessage replyMessage=null;
    private boolean done=false;
    private boolean failed=false;
    private final long timestamp;

    Request(int requestId){
        this.requestId=requestId;
        timestamp=Calendar.getInstance().getTimeInMillis();
    }

    int getRequestId() {
        return requestId;
    }

    synchronized void setReplyMessage(ReplyMessage replyMessage) {
        if(!done) {
            this.replyMessage = replyMessage;
            setDone();
            //timeout.interrupt();
            notifyAll();
        }
    }

    boolean isFailed() {
        return failed;
    }

    ReplyMessage getReplyMessage() {
        return replyMessage;
    }

    synchronized void delete(){
        if(!done) {
            this.failed = true;
            setDone();
            notifyAll();
        }
    }

    private void setDone() {
        this.done=true;
    }

    synchronized void waitingLoop() throws InterruptedException {
        while(!done){
            wait();
        }
    }

    public long getTimestamp() {
        return timestamp;
    }
}
