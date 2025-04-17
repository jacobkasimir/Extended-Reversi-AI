import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TCPClientTest {
    
    private ServerSocket serverSocket;
    private Socket clientSocket;
    DataInputStream in;
    DataOutputStream out;

    @BeforeEach
    public void init() throws Exception{
        serverSocket = new ServerSocket(7777);
        clientSocket = serverSocket.accept();
        out = new DataOutputStream(clientSocket.getOutputStream());
        in = new DataInputStream(clientSocket.getInputStream());

        TCPClient.connect("127.0.0.1", 7777);
    }

    @Test
    public void GameClientInitTest() throws Exception{
        out.writeByte(2); out.writeInt(1); out.writeUTF("2 \n 0\n1 1 \n2 6 \n- - b x 2 - \n- 0 x 1 - - \n 1 1 5 <−> 4 0 1");
        out.writeByte(3); out.writeInt(1); out.writeByte(1);
        out.writeByte(4); out.writeInt(5); out.writeLong(30720000);
        byte type = in.readByte();
        int len = in.readInt();
        long message = in.readLong();
        assertEquals(message, 65537);
    }
}

