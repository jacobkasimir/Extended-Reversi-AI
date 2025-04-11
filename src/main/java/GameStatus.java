import java.util.Dictionary;


public class GameStatus {
    private Dictionary<Integer, Player> players;
    private int numberPlayers;
    private boolean phase1;                 //Phase 1: true; Phase 2: false


    public GameStatus(int numberPlayers){
        this.numberPlayers = numberPlayers;
        phase1 = true;
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
}
