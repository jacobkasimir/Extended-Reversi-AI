public class GameStatus {
    private Player[] players;
    private int numberPlayers;

    public GameStatus(int numberPlayers){
        this.numberPlayers = numberPlayers;
        players = new Player[numberPlayers];
    }
}
