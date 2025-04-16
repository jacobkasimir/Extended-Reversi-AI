import java.util.Dictionary;


public class GameState {
    private Dictionary<Integer, Player> players;
    private int numberPlayers;
    private boolean phase1;                 //Phase 1: true; Phase 2: false
    private byte ourPlayerId;

    public GameState(int numberPlayers, int overwrites, int bombs, int bombRadius, byte gamePhase){
        this.numberPlayers = numberPlayers;
        this.setGamePhase(gamePhase);
        for (int i = 1; i <= numberPlayers; i++) {
            Player p = new Player(bombs, overwrites, i, phase1);
            this.setPlayer(p, i);
        }
    }


    private void setPlayer(Player p, int number){
        players.put(number, p);
    }

    public int getNumberPlayers() {
        return numberPlayers;
    }

    public void setNumberPlayers(int numberPlayers) {
        this.numberPlayers = numberPlayers;
    }

    public void setPhase1(boolean phase1) {
        this.phase1 = phase1;
    }

    public boolean isPhase1() {
        return phase1;
    }

    public Player getPlayer(int number){
        return players.get(number);
    }

    public byte getOurPlayerId(){
        return ourPlayerId;
    }

    public void setOurPlayerId(byte id){
        ourPlayerId = id;
    }

    public void playerDisqualified(byte id){
        this.getPlayer(id).setInGame(false);
    }

    public void setGamePhase(byte phase){
        if (phase == 1) {
            this.setPhase1(true);
        } else if (phase == 2) {
            this.setPhase1(false);
        }
    }

    public byte getGamePhase(){
        if(this.isPhase1()) return 1;
        else return 2;
    }

    public void refreshState(Move move){
        //TODO in future exercises
    }
}
