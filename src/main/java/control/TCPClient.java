package control;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

/**
 * TCPClient establishes a TCP connection to the game server and handles the network protocol.
 * It interprets incoming messages and delegates game logic to GameClient.
 */


public class TCPClient {

    /**
     * Connects to the game server and processes the message protocol.
     *
     * @param server The server's IP address.
     * @param port   The port number.
     * @throws IOException In case of connection or stream errors.
     */
    public static void connect(InetAddress server, int port, boolean compare, int depthLimit, boolean movesorting) throws IOException {
        try (Socket socket = new Socket(server, port)) {
            System.out.println("Connected to server " + server + ":" + port);

            DataInputStream in = new DataInputStream(socket.getInputStream());
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());

            GameClient game = new GameClient();

            out.writeByte(1);
            out.writeInt(1);
            out.writeByte(Constants.GROUP_NUMBER);
            System.out.println("Group number was send: " + Constants.GROUP_NUMBER);

            while (true) {
                byte type;
                try {
                    type = in.readByte();
                } catch (IOException e) {
                    System.out.println("Connection terminated.");
                    break;
                }

                int length = in.readInt();

                switch (type) {
                    case 2 -> {
                        byte[] mapBytes = new byte[length];
                        in.readFully(mapBytes);
                        String map = new String(mapBytes);
                        String[] lines = map.split("\\R");
                        game.init(lines, compare, depthLimit, movesorting);
                    }

                    case 3 -> {
                        byte playerId = in.readByte();
                        game.setOurPlayerId(playerId);
                    }

                    case 4 -> {
                        int timeLimit = in.readInt();
                        byte maxDepth = in.readByte();
                        System.out.println("Move Request received - Time: " + timeLimit + ", Depth: " + maxDepth);

                        game.setCalculateParameters(timeLimit, maxDepth);

                        int move = game.calculateMove();

                        out.writeByte(5);
                        out.writeInt(5);
                        out.writeShort(Coder.decodeX(move));
                        out.writeShort(Coder.decodeY(move));
                        out.writeByte(Coder.decodeSpecial(move));
                        System.out.println("Move sent: (" + Coder.decodeX(move) + "," + Coder.decodeY(move) + "), special: " + Coder.decodeSpecial(move));
                    }

                    case 6 -> {
                        short mx = in.readShort();
                        short my = in.readShort();
                        byte mspecial = in.readByte();
                        byte mplayerId = in.readByte();
                        int MadeMove = Coder.encodeMove(mx, my, mspecial, mplayerId);
                        game.updateGame(MadeMove);
                        System.out.println("Player " + mplayerId + " moved to (" + mx + "," + my + ") with special value " + mspecial);
                    }

                    case 7 -> {
                        byte disqualifiedId = in.readByte();
                        game.playerDisqualified(disqualifiedId);
                        System.out.println("Player " + disqualifiedId + " was disqualified.");
                    }

                    case 8 -> {
                        game.endPhase();
                        System.out.println(">>> Phase 1 finished");
                    }

                    case 9 -> {
                        game.endGame();
                        System.out.println(">>> Game finished");
                        socket.close();
                        return;
                    }

                    default -> {
                        System.out.println("Unknown Type: " + type + ", " + length + " bytes skipped.");
                        in.skipBytes(length);
                    }
                }
            }
        }
    }
}