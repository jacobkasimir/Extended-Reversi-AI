package control;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

import validation.MoveVerifier;

/**
 * TCPClient establishes a TCP connection to the game server and handles the network protocol.
 * It interprets incoming messages and delegates game logic to GameClient.
 */


public class TCPClient {

    /**
     * Connects to the game server and processes the message protocol.
     * @param server The server's IP address.
     * @param port The port number.
     * @throws IOException In case of connection or stream errors.
     */
    public static void connect(InetAddress server, int port, boolean compare, int depthLimit) throws IOException {
        try (Socket socket = new Socket(server, port)) {
            System.out.println("Verbunden mit Server " + server + ":" + port);

            DataInputStream in = new DataInputStream(socket.getInputStream());
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());

            GameClient game = new GameClient();

            out.writeByte(1); out.writeInt(1); out.writeByte(Constants.GROUP_NUMBER);
            System.out.println("Gruppe gesendet: " + Constants.GROUP_NUMBER);
            
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
                    case 2 -> {
                        byte[] mapBytes = new byte[length];
                        in.readFully(mapBytes);
                        String map = new String(mapBytes);
                        String[] lines = map.split("\\R");
                        game.init(lines, compare, depthLimit);
                    }

                    case 3 -> {
                        byte playerId = in.readByte();
                        game.setOurPlayerId(playerId);
                    }

                    case 4 -> {
                        int timeLimit = in.readInt();
                        byte maxDepth = in.readByte();
                        System.out.println("Move Request erhalten - Zeit: " + timeLimit + ", Tiefe: " + maxDepth);

                        game.setCalculateParameters(timeLimit, maxDepth);
                        
                        int move = game.calculateMove();

                        out.writeByte(5);
                        out.writeInt(5);
                        out.writeShort(MoveVerifier.decodeX(move));
                        out.writeShort(MoveVerifier.decodeY(move));
                        out.writeByte(MoveVerifier.decodeSpecial(move));
                        System.out.println("Zug gesendet: (" + MoveVerifier.decodeX(move) + "," + MoveVerifier.decodeY(move) + "), Spezial: " + MoveVerifier.decodeSpecial(move));
                    }

                    case 6 -> {
                        short mx = in.readShort();
                        short my = in.readShort();
                        byte mspecial = in.readByte();
                        byte mplayerId = in.readByte();
                        int MadeMove = MoveVerifier.encodeMove(mx, my, mspecial, mplayerId);
                        game.updateGame(MadeMove);
                        System.out.println("Spieler " + mplayerId + " zog auf (" + mx + "," + my + ") mit Spezialwert " + mspecial);
                    }

                    case 7 -> {
                        byte disqualifiedId = in.readByte();
                        game.playerDisqualified(disqualifiedId);
                        System.out.println("Spieler " + disqualifiedId + " wurde disqualifiziert.");
                    }

                    case 8 -> {
                        game.endPhase();
                        System.out.println(">>> Phase 1 beendet");
                    }

                    case 9 -> {
                        game.endGame();
                        System.out.println(">>> Spiel beendet");
                        socket.close();
                        return;
                    }

                    default -> {
                        System.out.println("Unbekannter Typ: " + type + ", " + length + " Bytes übersprungen.");
                        in.skipBytes(length);
                    }
                }
            }
        }
    }
}