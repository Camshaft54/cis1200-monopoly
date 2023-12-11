package org.cis1200.monopoly;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class MonopolyClient implements Runnable {
    @Override
    public void run() {
        String hostname = "localhost";
        int portNumber = 80;
        try (
                Socket socket = new Socket(hostname, portNumber);
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))
        ) {
            ClientResponseHandler resHandler = new ClientResponseHandler();
            String fromServer = in.readLine();
            String toServer;
            while ((fromServer != null)) {
                toServer = resHandler.handleResponse(fromServer);
                out.println(toServer);
                fromServer = in.readLine();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
