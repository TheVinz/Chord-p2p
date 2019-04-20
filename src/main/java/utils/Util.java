package utils;

import network.remoteNode.RemoteNode;
import node.FingerTable;
import node.LocalNode;
import node.Node;

import java.io.IOException;
import java.util.function.Consumer;

public abstract class Util {

    public static final int M = 8;

    public static boolean isInsideInterval(int id, int start, int end){
        if(start > end){
            return id > start || id < end;
        }
        else if(start < end) return id > start && id < end;
        else return id!=start; //
    }

}
