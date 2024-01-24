package dai.udp;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class TCPServer implements Runnable {
    private final int port;
    private final IMusiciansView musiciansView;

    public TCPServer(int port, IMusiciansView musiciansView) {
        this.port = port;
        this.musiciansView = musiciansView;
    }

    private void handleClient(Socket socket) {
        try (socket;
                BufferedWriter out = new BufferedWriter(
                        new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8));) {
            String musicians = musiciansView.getMusiciansJsonString();
            out.write(musicians);
            out.flush();
            System.out.println("Client: sent " + musicians);

        } catch (IOException e) {
            System.out.println("Client: exception while using client socket: " + e);
        }
    }

    @Override
    public void run() {

        try (var serverSocket = new ServerSocket(port)) {
            while (true) {
                handleClient(serverSocket.accept());
            }
        } catch (Exception e) {
            System.out.println("Exception while creating Server Socket > " + e);
        }

    }

}
