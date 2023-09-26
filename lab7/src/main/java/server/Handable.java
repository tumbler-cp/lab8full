package server;

import comm.Request;

import java.io.Serializable;
import java.net.SocketAddress;

public class Handable implements Serializable {
    public SocketAddress src;
    public Request request;
    public Handable(SocketAddress src, Request request){
        this.src = src;
        this.request = request;
    }
}
