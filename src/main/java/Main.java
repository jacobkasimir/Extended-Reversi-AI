public class Main {
    public static void main(String[] args) throws Exception {
        String server = "localhost";
        int port = 12345;

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
                    server = args[++i];
                    break;
                case "-p":
                case "--port":
                    port = Integer.parseInt(args[++i]);
                    break;
            }
        }
        
        TCPClient.connect(server, port);
    }
}
