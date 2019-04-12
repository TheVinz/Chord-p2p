package network.remoteNode;

import network.exeptions.NetworkFailureException;
import network.message.reply.ReplyMessage;

import java.util.ArrayDeque;
import java.util.Iterator;

class PendingRequestQueue {

    private ArrayDeque<Request> pendingRequests = new ArrayDeque<>();

    void handleReplyMessage(ReplyMessage msg){
        Iterator<Request> iterator = pendingRequests.descendingIterator();
        Request req;
        while(iterator.hasNext()){
            req=iterator.next();
            if(req.getRequestId()==msg.getRequestId()){
                req.setReplyMessage(msg);
                break;
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



}
