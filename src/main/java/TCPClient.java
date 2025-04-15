import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class TCPClient {

    public static void connect(String server, int port) throws IOException {

        Socket socket = new Socket(server, port);

        System.out.println("Verbunden mit Server " + server + ":" + port);

        DataInputStream in = new DataInputStream(socket.getInputStream());
        DataOutputStream out = new DataOutputStream(socket.getOutputStream());

        GameClient game = new GameClient();

        byte groupNumber = 5;
        out.writeByte(1); out.writeInt(1); out.writeByte(groupNumber);
        System.out.println("Gruppe gesendet: " + groupNumber);

        while (true) {
            
            byte type;
            try {
                type = in.readByte();
            } catch (IOException e) {
                System.out.println("Verbindung beendet.");
                break;
            }

            int length = in.readInt();

            switch (type) {

                case 2: //GameClient Init
                    byte[] mapBytes = new byte[length];
                    in.readFully(mapBytes);
                    String map = new String(mapBytes);
                    String[] lines = map.split("\\R");
                    game.init(lines);
                    break;

                case 3: // Player ID setzen
                    byte playerId = in.readByte();
                    game.setPlayerId(playerId);
                    break;

                case 4: // Move Request
                    int timeLimit = in.readInt();
                    byte maxDepth = in.readByte();
                    System.out.println("Move Request erhalten – Zeit: " + timeLimit + ", Tiefe: " + maxDepth);

                    //Move berechnen und zurückgeben

                    game.setCalculateParameters(timeLimit,maxDepth);

                    Move move = game.calculateMove(timeLimit, maxDepth);

                    out.writeByte(5);
                    out.writeInt(5);
                    out.writeShort(move.x);
                    out.writeShort(move.y);
                    out.writeByte(move.special);
                    System.out.println("Zug gesendet: (" + move.x + "," + move.y + "), Spezial: " + move.special);
                    break;

                case 6: // Move Announcement
                    short mx = in.readShort();
                    short my = in.readShort();
                    byte mspecial = in.readByte();
                    byte mplayerId = in.readByte();
                    Move MadeMove = new Move(mx, my, mspecial, mplayerId);
                    game.updateGame(MadeMove);
                    System.out.println("Spieler " + mplayerId + " zog auf (" + mx + "," + my + ") mit Spezialwert " + mspecial);
                    break;

                case 7: // Disqualifikation
                    byte disqualifiedId = in.readByte();
                    game.playerDisqualified(disqualifiedId);
                    System.out.println("Spieler " + disqualifiedId + " wurde disqualifiziert.");
                    break;

                case 8:
                    game.endPhase();
                    System.out.println(">>> Phase 1 beendet");
                    break;

                case 9:
                    game.endGame();
                    System.out.println(">>> Spiel beendet");
                    socket.close();
                    return;

                default:
                    System.out.println("Unbekannter Typ: " + type + ", " + length + " Bytes übersprungen.");
                    in.skipBytes(length);
                    break;
            }
        }
    }
}

