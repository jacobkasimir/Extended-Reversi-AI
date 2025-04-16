import org.w3c.dom.ls.LSOutput;

public class Player {
    private int numberBombs;
    private int numberOverwrites;
    private int playerNumber;
    private boolean inGame = true;


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

