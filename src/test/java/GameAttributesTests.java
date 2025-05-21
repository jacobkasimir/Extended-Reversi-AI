import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class GameAttributesTests {

    private MapController map;

    @BeforeEach
    public void setup(){
        GameClient gc = new GameClient();
        gc.initTransitions(new String[]{
            "7 7 2 <-> 0 0 6", 
            "7 0 1 <-> 0 7 5"}, 8, 8);

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
            gc.getTransitions(), 
            8, 
            8, 
            0);
    }

    @Test
    public void decrementOverwriteStones(){
        GameState gameState = new GameState(map, new GameAttributes(2, 3, 1, 1, 1));
        int move = MoveVerifier.encodeMove(5, 6, 0, 1);

        gameState.attributes().updateState(move, map);

        if (!MoveVerifier.isValid(move, gameState)) {
            throw new Error("Test not valid.");
        }

        assertEquals(3-1, gameState.attributes().getPlayer(1).getNumberOverrides());
    }

    @Test
    public void decrementBombs(){
        GameState gameState = new GameState(map, new GameAttributes(2, 3, 1, 2, 2));
        int move = MoveVerifier.encodeMove(4, 4, 0, 2);

        assertFalse(gameState.attributes().isPhase1());

        if (!MoveVerifier.isValid(move, gameState)) {
            throw new Error("Test not valid.");
        }

        gameState.attributes().updateState(move, map);

        assertEquals(1-1, gameState.attributes().getPlayer(2).getNumberBombs());
    }

    @Test
    public void bonusIncrementBombs(){
        GameState gameState = new GameState(map, new GameAttributes(2, 3, 1, 2, 1));
        int move = MoveVerifier.encodeMove(2, 5, 20, 1);

        if (!MoveVerifier.isValid(move, gameState)) {
            throw new Error("Test not valid.");
        }

        gameState.attributes().updateState(move, map);

        assertEquals(1+1, gameState.attributes().getPlayer(1).getNumberBombs());
    }

    @Test
    public void bonusIncrementOverwrites(){
        GameState gameState = new GameState(map, new GameAttributes(2, 3, 1, 2,  1));
        int move = MoveVerifier.encodeMove(2, 5, 21, 1);

        if (!MoveVerifier.isValid(move, gameState)) {
            throw new Error("Test not valid.");
        }

        gameState.attributes().updateState(move, map);

        assertEquals(3+1, state.getPlayer(1).getNumberOverwrites());
    }

}
