package thw.edu.javaII.port.warehouse.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import thw.edu.javaII.port.warehouse.model.common.Info;

public class Server {
	private static final java.util.logging.Logger LOGGER = java.util.logging.Logger.getLogger(Server.class.getName());
	public static boolean run = true;
	
	public static void main(String[] args) {
        try (ServerSocket server = new ServerSocket(Info.PORT_SERVER)) {
            System.out.println("Lagerverwaltungsserver läuft");
            while (run) {
                Socket sock = server.accept();
                new Service(sock).start();
            }
        } catch (IOException e) {
			LOGGER.log(java.util.logging.Level.SEVERE, "Fehler im Server aufgetreten", e);
        }
	}
	
}
