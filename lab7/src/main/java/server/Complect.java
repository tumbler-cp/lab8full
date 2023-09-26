package server;

import comm.Request;

import java.net.SocketAddress;

public record Complect(SocketAddress src, Request alloc, Request data) {
}
