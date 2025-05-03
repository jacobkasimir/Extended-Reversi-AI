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
                "0 0 b 0 0 0 0 0", 
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

        GameState state = new GameState(2, 3, 1, 2, (byte) 2);
        Move move = new Move(4, 4, 0, 2);

        assertFalse(state.isPhase1());

        if (!MoveVerifier.isValid(move, map, state)) {
            throw new Error("Test not valid.");
        }

        state.updateState(move, map);

        assertEquals(1-1, state.getPlayer(2).getNumberBombs());
    }

    @Test
    public void bonusIncrementBombs(){

        GameState state = new GameState(2, 3, 1, 2, (byte) 1);
        Move move = new Move(2, 5, 20, 1);

        if (!MoveVerifier.isValid(move, map, state)) {
            throw new Error("Test not valid.");
        }

        state.updateState(move, map);

        assertEquals(1+1, state.getPlayer(1).getNumberBombs());
    }

    @Test
    public void bonusIncrementOverwrites(){

        GameState state = new GameState(2, 3, 1, 2, (byte) 1);
        Move move = new Move(2, 5, 21, 1);

        if (!MoveVerifier.isValid(move, map, state)) {
            throw new Error("Test not valid.");
        }

        state.updateState(move, map);

        assertEquals(3+1, state.getPlayer(1).getNumberOverwrites());
    }

}
