package cat.katzenfabrik.morsecodr;

import java.io.IOException;
import java.net.ServerSocket;

public class Receiver {
    public static Sender receive() throws IOException {
        ServerSocket ss = new ServerSocket(Sender.PORT);
        return new Sender(ss.accept());
    }
}
