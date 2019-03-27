package network.remoteNode;

import network.message.ReplyMessage;

class Request {

    private static final long REQUEST_TIMEOUT=5000L;

    private final int requestId;
    private ReplyMessage replyMessage=null;
    private boolean done=false;
    private boolean failed=false;

    Request(int requestId){
        this.requestId=requestId;
        new Thread(this::timer).start();
    }

    int getRequestId() {
        return requestId;
    }

    synchronized void setReplyMessage(ReplyMessage replyMessage) {
        if(!done) {
            this.replyMessage = replyMessage;
            this.done = true;
            notifyAll();
        }
    }

    boolean isDone() {
        return done;
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
            this.done = true;
            notifyAll();
        }
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
            e.printStackTrace();
        }

        delete();
    }
}
