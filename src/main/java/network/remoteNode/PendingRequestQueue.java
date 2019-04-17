package network.remoteNode;

import network.exeptions.NetworkFailureException;
import network.message.reply.ReplyMessage;

import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedDeque;

class PendingRequestQueue {

    private ConcurrentLinkedDeque<Request> pendingRequests = new ConcurrentLinkedDeque<>();

    void handleReplyMessage(ReplyMessage msg){
        Iterator<Request> iterator = pendingRequests.descendingIterator();
        Request req;
        while(iterator.hasNext()){
            req=iterator.next();
            if(req.getRequestId()==msg.getRequestId()){
                req.setReplyMessage(msg);
                return;
            }
        }
    }

    ReplyMessage submitRequest(Request request) throws NetworkFailureException {

        pendingRequests.addFirst(request);

        try {
            request.waitingLoop();
        } catch (InterruptedException e) {
            e.printStackTrace();
            pendingRequests.remove(request);
            return null;
        }

        pendingRequests.remove(request);

        if(request.isFailed())
            throw new NetworkFailureException();
        else
            return request.getReplyMessage();
    }

    void close(){
        for(Request r : pendingRequests)
            r.delete();
    }



}
