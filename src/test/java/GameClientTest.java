import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

public class GameClientTest {

    @Test
    public void init(){
        String input = "2\n0\n1 1\n2 6\n- - b x 2 -\n- 0 x 1 - -\n1 1 5 <-> 4 0 1";
        String[] lines = input.split("\\R");

        GameClient game = new GameClient();
        int startPosition = MoveVerifier.encodePosition(1, 1, 5);
        int endPosition =  MoveVerifier.encodePosition(4, 0, 1+4);

        game.init(lines,false,1);

        assertEquals(game.getCurrentGameAttributes().getNumberPlayers(), 2);
        assertEquals(game.getCurrentGameAttributes().getPlayer(1).getNumberOverwrites(), 0);
        assertEquals(game.getCurrentGameAttributes().getPlayer(1).getNumberBombs(), 1);
        assertEquals(game.getCurrentMapController().getBombRadius(), 1);
        assertEquals(game.getCurrentMapController().getHeight(), 2);
        assertEquals(game.getCurrentMapController().getWidth(), 6);
        assertEquals(game.getCurrentMapController().getTile(0, 0) , '-');
        assertEquals(game.getCurrentMapController().getTile(2, 0) , 'b');
        assertEquals(game.getCurrentMapController().getTile(1, 1) , '0');
        assertEquals(game.getCurrentMapController().getTile(3, 1) , '1');
        assertEquals(game.getCurrentMapController().getNextTile(startPosition), endPosition);
    }


}
