import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Main class to launch the TCP client for the board game.
 * Handles command line argument parsing and initiates the server connection.
 * Supports IPv4, IPv6 and hostnames.
 */
public class Main {
    public static void main(String[] args) {
        String serverInput = "localhost";
        int port = 12345;

        try {
            for (int i = 0; i < args.length; i++) {
                switch (args[i]) {
                    case "-h":
                    case "--help":
                        System.out.println("Usage:");
                        System.out.println("  -s, --server <Host/IP>  Server address (IPv4, IPv6 or hostname)");
                        System.out.println("  -p, --port <Port>       Port number (default: 12345)");
                        System.out.println("  -h, --help              Show this help");
                        return;
                    case "-s":
                    case "--server":
                        if (i + 1 < args.length) {
                            serverInput = args[++i];
                        } else {
                            System.err.println("Error: -s requires a server address.");
                            return;
                        }
                        break;
                    case "-p":
                    case "--port":
                        if (i + 1 < args.length) {
                            try {
                                port = Integer.parseInt(args[++i]);
                            } catch (NumberFormatException e) {
                                System.err.println("Error: Port must be a number.");
                                return;
                            }
                        } else {
                            System.err.println("Error: -p requires a port number.");
                            return;
                        }
                        break;
                    default:
                        System.err.println("Unknown parameter: " + args[i]);
                        return;
                }
            }

            // Handles adress/hostname resolution
            InetAddress[] addresses = InetAddress.getAllByName(serverInput);

            if (addresses.length == 0) {
                System.err.println("Error: No IP addresses found for " + serverInput);
                return;
            }

            InetAddress selectedAddress = addresses[0]; // simple strategy: first match
            System.out.println("Connecting to " + selectedAddress.getHostAddress() + " (" + selectedAddress.getHostName() + ") on port " + port);

            TCPClient.connect(selectedAddress, port);

        } catch (UnknownHostException e) {
            System.err.println("Error: Could not resolve host '" + serverInput + "'.");
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
        }
    }
}
