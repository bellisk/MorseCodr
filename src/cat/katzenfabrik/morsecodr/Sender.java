package cat.katzenfabrik.morsecodr;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class Sender {
    public static final int PORT = 7121;
    private Socket s;
    private BufferedReader r;
    private PrintWriter w;
    public Sender(String ip) throws UnknownHostException, IOException {
        this(new Socket(ip, PORT));
    }

    public Sender(Socket s) throws IOException {
        this.s = s;
        r = new BufferedReader(new InputStreamReader(s.getInputStream(), "UTF-8"));
        w = new PrintWriter(new OutputStreamWriter(s.getOutputStream(), "UTF-8"));
    }
    public String read() throws IOException {
        String str = r.readLine();
        //System.out.println("< " + str);
        return str;
    }
    public void write(String s) {
        w.println(s);
        //System.out.println("> " + s);
        w.flush();
    }
}
