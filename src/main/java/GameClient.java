public class GameClient {
    private MapController mapController;
    private GameRules gameRules;
    private GameState gameState;

    public void init(String[] lines) {
        int playerCount = Integer.valueOf(lines[0]);
        int overrideCount = Integer.valueOf(lines[1]);

        String[] bombInfo = lines[2].split(" ");
        int bombCount = Integer.valueOf(bombInfo[0]);
        int bombRadius = Integer.valueOf(bombInfo[1]);

        String[] mapInfo = lines[3].split(" ");
        int height = Integer.valueOf(mapInfo[0]);
        int width = Integer.valueOf(mapInfo[1]);

        String[] map = splitString(lines,4,(4 + height));
        String[] transitions = splitString(lines, (4+height), lines.length);

        int gamePhase = 1;

        this.gameState = new GameState(playerCount, overrideCount, bombCount, bombRadius, width, height, gamePhase);
        this.mapController =  new MapController(map, transitions, width, height, bombRadius);
        this.gameRules = new GameRules();

        System.out.println("Init abgeschlossen");
    }

    public void updateGame(Move move){
        this.mapController.refreshMap(move);
        this.gameState.refreshStatus(move);
    }

    public void setPlayerId(int id) {
        this.gameState.setPlayerId() = id;
        System.out.println("Spieler-ID erhalten: " + id);
    }

    public void playerDisqualified(byte playerId){
        this.gameState.playerDisqualified(playerId);
    }

    public void setCalculateParameters(int timeLimit, byte maxDepth){
        //???
    }

    public Move calculateMove(int timeLimit, int maxDepth) {
        Move possibleMove = generateRandomMove();
        while(!MoveVerifier.isValid(possibleMove, mapController, gameState)){
            possibleMove = generateRandomMove();
        }
        return possibleMove;
    }

    public Move generateRandomMove(){
        int x = Math.random(0,gameState.getWidth());
        int y = Math.random(0,gameState.getHeight());
        Move possibleMove = new Move(x,y,mapController.getMap().getSpecial(),gameState.getPlayerId());
        return possibleMove;
    }

    private String[] splitString(String[] lines, int min, int max){
        String [] res = new String[max-min];
        for(int i = min; i < max; i++){
            res[i-min] = lines[i];
        }
        return res;
    }

    public void endPhase(){
        this.gameState.setGamePhase(2); //Switch-Case in GameRules???
    }

    public void endGame(){
        //???
    }
}
