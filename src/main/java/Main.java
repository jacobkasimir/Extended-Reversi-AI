import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import control.TCPClient;

/**
 * Main class to launch the TCP client for the board game.
 * Handles command line argument parsing and initiates the server connection.
 * Supports IPv4, IPv6 and hostnames.
 */
public class Main {
    public static void main(String[] args) {
        String serverInput = "localhost";
        int port = 12345;
        boolean compare = false;
        boolean moveSorting = true;
        int depthLimit = 1;

        try {
            for (int i = 0; i < args.length; i++) {
                switch (args[i]) {
                    case "-h", "--help" -> {
                        System.out.println("Usage:");
                        System.out.println("  -s, --server <Host/IP>     Server address (IPv4, IPv6 or hostname)");
                        System.out.println("  -p, --port <Port>          Port number (default: 12345)");
                        System.out.println("  -d, --depth <SearchDepth>  Depth of the search algorithm (default: 1)");
                        System.out.println("  -c, --compare              Compare MiniMax and Alpha-Beta results");
                        System.out.println("  -m, --movesortingOff       Disable move sorting for Alpha Beta Pruning");
                        System.out.println("  -h, --help                 Show this help");
                        return;
                    }
                    case "-s", "--server" -> {
                        if (i + 1 < args.length) {
                            serverInput = args[++i];
                        } else {
                            System.err.println("Error: -s requires a server address.");
                            return;
                        }
                    }
                    case "-p", "--port" -> {
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
                    }
                    case "-d", "--depth" -> {
                        if (i + 1 < args.length) {
                            try {
                                depthLimit = Integer.parseInt(args[++i]);
                            } catch (NumberFormatException e) {
                                System.err.println("Error: Search depth must be a number.");
                                return;
                            }
                        } else {
                            System.err.println("Error: -d requires a depth value.");
                            return;
                        }
                    }
                    case "-c", "--compare" -> compare = true;
                    case "-m", "--movesortingOff" -> moveSorting = false;
                    default -> {
                        System.err.println("Unknown parameter: " + args[i]);
                        return;
                    }
                }
            }

            InetAddress[] addresses = InetAddress.getAllByName(serverInput);

            if (addresses.length == 0) {
                System.err.println("Error: No IP addresses found for " + serverInput);
                return;
            }

            InetAddress selectedAddress = addresses[0];
            System.out.println("Connecting to " + selectedAddress.getHostAddress() + " ("
                    + selectedAddress.getHostName() + ") on port " + port);
            System.out.println("Search depth: " + depthLimit + ", Compare mode: " + (compare ? "enabled" : "disabled")
                    + ", Move sorting: " + (moveSorting ? "enabled" : "disabled"));

            TCPClient.connect(selectedAddress, port, compare, depthLimit, moveSorting);

        } catch (UnknownHostException e) {
            System.err.println("Error: Could not resolve host '" + serverInput + "'.");
        } catch (IOException e) {
            System.err.println("Unexpected error: " + e.getMessage());
        }
    }
}
