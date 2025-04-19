/**
 * Main class to launch the TCP client for the board game.
 * Handles command line argument parsing and initiates the server connection.
 */
import java.net.InetAddress;

public class Main {
    public static void main(String[] args) {
        String server = "localhost";
        int port = 12345;

        try {
            for (int i = 0; i < args.length; i++) {
                switch (args[i]) {
                    case "-h":
                    case "--help":
                        System.out.println("Verwendung:");
                        System.out.println("  -s, --server <Host>     Adresse des Servers");
                        System.out.println("  -p, --port <Port>       Portnummer (Standard: 12345)");
                        System.out.println("  -h, --help              Hilfe anzeigen");
                        return;
                    case "-s":
                    case "--server":
                        if (i + 1 < args.length) {
                            server = args[++i];
                        } else {
                            System.err.println("Fehler: -s erfordert eine Serveradresse");
                            return;
                        }
                        break;
                    case "-p":
                    case "--port":
                        if (i + 1 < args.length) {
                            try {
                                port = Integer.parseInt(args[++i]);
                            } catch (NumberFormatException e) {
                                System.err.println("Fehler: Port muss eine Zahl sein.");
                                return;
                            }
                        } else {
                            System.err.println("Fehler: -p erfordert eine Portnummer");
                            return;
                        }
                        break;
                    default:
                        System.err.println("Unbekannter Parameter: " + args[i]);
                        return;
                }
            }

            InetAddress inetAddress = InetAddress.getByName(server);
            System.out.println("Verbinde zu " + inetAddress.getHostAddress() + ":" + port);
            TCPClient.connect(inetAddress, port);

        } catch (Exception e) {
            System.err.println("Fehler: " + e.getMessage());
        }
    }
}
