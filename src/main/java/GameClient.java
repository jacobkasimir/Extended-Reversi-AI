public class GameClient {
    private MapController mapController;
    private GameRules gameRules;
    private GameStatus gameStatus;

    public void init(String[] lines) {
        int playerCount = Integer.valueOf(lines[0]);
        int overrideCount = Integer.valueOf(lines[1]);
        String[] bombInfo = lines[2].split(" ");
        int bombCount = Integer.valueOf(bombInfo[0]);
        int bombRadius = Integer.valueOf(bombInfo[1]);
        String[] mapInfo = lines[3].split(" ");
        int height = Integer.valueOf(mapInfo[0]);
        int width = Integer.valueOf(mapInfo[1]);
        String[] map = 
        String[] transitions = 
        String[] widthHelp = lines
        this.gameStatus = new GameStatus(playerCount, overrideCount, bombCount, bombRadius, width, height);
        this. mapController =  new MapController(map, transitions, width, depth, bombRadius);
        this.gameStatus.
        System.out.println("Map gespeichert.");
    }

    public void refreshMap()

    public void setPlayerId(int id) {
        //this.gameStatus.playerId = id;
        System.out.println("Spieler-ID erhalten: " + id);
    }

    public void setPlayerCount(byte playerCount){
        //this.gameStatus.playerCount = playerCount;
    }

    public Move calculateMove(int timeLimit, int maxDepth) {
        // TODO: Besserer Algorithmus
        return new Move(4, 2, 0); // Dummy-Zug mit Bonus
    }

    private int splitString(String splitter){
        int res = 0;
        return res;
    }
}
