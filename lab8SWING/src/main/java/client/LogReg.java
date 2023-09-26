package client;

import comm.Request;
import comm.Signal;
import comm.User;
import org.apache.commons.lang3.SerializationUtils;

import java.io.IOException;

public class LogReg {
    private final ClientNetwork network;
    private User owner;

    public LogReg(ClientNetwork network) {
        this.network = network;
    }
    public boolean logReg(String login, String password, Signal action) throws IOException {
        User user = new User(login, password);
        network.request(action, user);
        network.receive();
        network.receive();
        Request received = SerializationUtils.deserialize(network.buffer());
        if (received.signal() == Signal.ACCEPT){
            System.out.println(received.object());
            owner = user;
            return true;
        } else {
            System.out.println(received.object());
            return false;
        }
    }

    public User user(){
        return owner;
    }
}
