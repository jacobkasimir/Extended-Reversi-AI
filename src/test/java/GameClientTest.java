import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

public class GameClientTest {

    @Test
    public void init(){
        String input = "2\n0\n1 1\n2 6\n- - b x 2 -\n- 0 x 1 - -\n1 1 5 <-> 4 0 1";
        String[] lines = input.split("\\R");

        GameClient game = new GameClient();
        Position startPosition = new Position(1, 1, 5);
        Position endPosition = new Position(4, 0, 1);

        game.init(lines);

        assertEquals(game.getGameState().getNumberPlayers(), 2);
        assertEquals(game.getGameState().getPlayer(0).getNumberOverwrites(), 0);
        assertEquals(game.getGameState().getPlayer(0).getNumberBombs(), 1);
        assertEquals(game.getMapController().getBombRadius(), 1);
        assertEquals(game.getMapController().getHeight(), 2);
        assertEquals(game.getMapController().getWidth(), 6);
        assertEquals(game.getMapController().getTile(0, 0) , '-');
        assertEquals(game.getMapController().getTile(0, 2) , 'b');
        assertEquals(game.getMapController().getTile(1, 1) , '0');
        assertEquals(game.getMapController().getTile(1, 3) , '1');
        assertEquals(game.getMapController().getNextTile(startPosition), endPosition);
    }


}
