package client;

import comm.Request;
import org.apache.commons.lang3.SerializationUtils;

import java.io.IOException;

public class Receiver extends Thread {
    private final ClientNetwork network;
    private final ClientHandler handler;
    public Receiver(ClientNetwork clientNetwork, ClientHandler clientHandler){
        network = clientNetwork;
        handler = clientHandler;
    }
    @Override
    public void run() {
        try {
            network.receive();
        } catch (IOException e) {
        }
        Request response = SerializationUtils.deserialize(network.buffer());
        handler.setObj(response);
        try {
            handler.run();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
