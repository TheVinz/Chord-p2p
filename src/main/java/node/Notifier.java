package node;

import network.exeptions.NetworkFailureException;

public interface Notifier extends Node{
    void notifyPredecessor(Node n) throws NetworkFailureException;
}
