package interfaces;
import comm.Signal;

import java.io.IOException;

public interface Network {
    void send(byte[] data) throws IOException;
    void receive() throws IOException;

    void request(Signal signal, Object object) throws IOException;
    byte[] buffer();
}
