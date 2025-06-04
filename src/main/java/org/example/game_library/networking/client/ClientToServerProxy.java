package org.example.game_library.networking.client;

import lombok.Getter;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;

@Getter
public class ClientToServerProxy {

    private static Socket socket;
    private static ObjectOutputStream out;
    private static ObjectInputStream in;

    private ClientToServerProxy() {
        throw new UnsupportedOperationException("Utility class!");
    }

    public static void init() throws IOException {
        socket = new Socket("localhost", 5000);
        out = new ObjectOutputStream(socket.getOutputStream());
        out.flush();
        in = new ObjectInputStream(socket.getInputStream());
    }

    public static void send(List<String> request) throws IOException{
        out.writeObject(request);
        out.flush();
    }

    public static Object receive() throws IOException, ClassNotFoundException {
        return in.readObject();
    }


    public static void close() throws IOException {
        if (out != null) out.close();
        if (in != null) in.close();
        if (socket != null) socket.close();
    }
}
