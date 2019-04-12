package network.remoteNode;

import network.message.reply.ReplyMessage;

import java.util.concurrent.Semaphore;


class Request {

    private static final long REQUEST_TIMEOUT=10000L;

    private final int requestId;
    private ReplyMessage replyMessage=null;
    private boolean done=false;
    private boolean failed=false;
    private Thread timeout;

    Request(int requestId){
        this.requestId=requestId;
        timeout = new Thread(this::timer);
        timeout.start();
    }

    int getRequestId() {
        return requestId;
    }

    synchronized void setReplyMessage(ReplyMessage replyMessage) {
        if(!done) {
            this.replyMessage = replyMessage;
            setDone();
            timeout.interrupt();
            notifyAll();
        }
    }

    boolean isFailed() {
        return failed;
    }

    ReplyMessage getReplyMessage() {
        return replyMessage;
    }

    private synchronized void delete(){
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

    private void timer(){
        try {
            Thread.sleep(REQUEST_TIMEOUT);
        } catch (InterruptedException e) {
            return;
        }

        delete();
    }
}
