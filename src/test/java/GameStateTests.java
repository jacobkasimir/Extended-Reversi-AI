import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

public class GameStateTests {

    private MapController map;

    @BeforeEach
    public void setup(){
        map = new MapController(
            new String[]{
                "- - - - - - - -", 
                "0 0 0 0 0 0 1 0", 
                "0 0 0 0 0 2 0 0", 
                "0 0 0 1 2 0 0 0",
                "0 0 0 2 1 0 0 0",
                "0 0 c 0 0 0 0 0", 
                "0 0 0 c 0 x 0 x", 
                "0 0 0 b 0 0 0 0"}, 
            new String[]{
                "7 7 2 <-> 0 0 6", 
                "7 0 1 <-> 0 7 5"}, 
            8, 
            8, 
            0);
    }

    @Test
    public void decrementOverwriteStones(){

        GameState state = new GameState(2, 3, 1, 1, (byte) 1);
        Move move = new Move(5, 6, 0, 1);

        state.updateState(move, map);

        if (!MoveVerifier.isValid(move, map, state)) {
            throw new Error("Test not valid.");
        }

        assertEquals(3-1, state.getPlayer(1).getNumberOverwrites());
    }

    @Test
    public void decrementBombs(){

        GameState state = new GameState(3, 3, 1, 2, (byte) 2);
        Move move = new Move(4, 4, 0, 3);

        state.updateState(move, map);

        if (!MoveVerifier.isValid(move, map, state)) {
            throw new Error("Test not valid.");
        }

        assertEquals(1-1, state.getPlayer(3).getNumberBombs());
    }

    @Test
    public void bonusIncrementBombs(){

        GameState state = new GameState(3, 3, 1, 2, (byte) 2);
        Move move = new Move(2, 5, 20, 1);

        state.updateState(move, map);

        if (!MoveVerifier.isValid(move, map, state)) {
            throw new Error("Test not valid.");
        }

        assertEquals(1+1, state.getPlayer(3).getNumberBombs());
    }

    @Test
    public void bonusIncrementOverwrites(){//TODO

        GameState state = new GameState(3, 3, 1, 2, (byte) 2);
        Move move = new Move(2, 5, 21, 3);

        state.updateState(move, map);

        if (!MoveVerifier.isValid(move, map, state)) {
            throw new Error("Test not valid.");
        }

        assertEquals(3+1, state.getPlayer(3).getNumberOverwrites());
    }

}
