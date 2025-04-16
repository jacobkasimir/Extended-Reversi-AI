
public class Player {
    private int numberBombs;
    private int numberOverwrites;
    private int playerNumber;
    private boolean inGame = true;


    public Player(int bombs, int overwrites, int number, boolean in){
        this.numberBombs = bombs;
        this.numberOverwrites = overwrites;
        this.playerNumber = number;
        this.inGame = in;
    }

    public boolean isInGame() {
        return inGame;
    }

    public void setInGame(boolean inGame) {
        this.inGame = inGame;
    }

    public int getPlayerNumber() {
        return playerNumber;
    }

    public void setPlayerNumber(int playerNumber) {
        this.playerNumber = playerNumber;
    }
    public int getNumberOverwrites() {
        return numberOverwrites;
    }

    public void setNumberOverwrites(int numberOverwrites) {
        this.numberOverwrites = numberOverwrites;
    }

    public int getNumberBombs() {
        return numberBombs;
    }

    public void setNumberBombs(int numberBombs) {
        this.numberBombs = numberBombs;
    }
}
