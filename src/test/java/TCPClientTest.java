import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class TCPClientTest {

    private static ServerSocket serverSocket;
    private static Socket clientSocket;
    private static DataInputStream in;
    private static DataOutputStream out;

    @BeforeAll
    public static void init() throws Exception {
        serverSocket = new ServerSocket(7777);

        // Starte Client-Verbindung in separatem Thread, damit accept() nicht blockiert
        Thread clientThread = new Thread(() -> {
            try {
                Thread.sleep(100); // kurz warten, damit Server bereit ist
                TCPClient.connect("127.0.0.1", 7777);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        clientThread.start();

        // Server wartet auf Verbindung
        clientSocket = serverSocket.accept();

        out = new DataOutputStream(clientSocket.getOutputStream());
        in = new DataInputStream(clientSocket.getInputStream());
    }

    @Test
    public void GameClientInitTest() throws Exception {
        // Simuliere Nachrichten vom Server an den Client
        out.writeByte(2);
        out.writeInt(1);
        out.writeUTF("2 \n 0\n1 1 \n2 6 \n- - b x 2 - \n- 0 x 1 - - \n 1 1 5 <−> 4 0 1");

        out.writeByte(3);
        out.writeInt(1);
        out.writeByte(1);

        out.writeByte(4);
        out.writeInt(5);
        out.writeLong(30720000);

        // Client sollte hier etwas antworten
        byte type = in.readByte();
        int len = in.readInt();
        long message = in.readLong();

        assertEquals(65537, message);
        assertEquals(1, type);
        assertEquals(8, len);
    }
}