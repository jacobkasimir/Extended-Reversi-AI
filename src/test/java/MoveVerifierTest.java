import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

public class MoveVerifierTest {

    private MapController map;
    private GameState gameState1;
    private GameState gameState1NoOverwtites;

    @BeforeEach
    public void setUpGame(){
        map = new MapController(
            new String[]{
                "- - - - - - - -", 
                "0 0 0 0 0 0 1 0", 
                "0 0 0 0 0 2 0 0", 
                "0 0 0 1 2 0 0 0",
                "0 0 0 2 1 0 0 0",
                "0 0 0 0 0 0 0 0", 
                "0 0 0 c 0 x 0 x", 
                "0 0 0 b 0 0 0 0"}, 
            new String[]{
                "7 7 2 <-> 0 0 6", 
                "7 0 1 <-> 0 7 5"}, 
            8, 
            8, 
            0);

        gameState1 = new GameState(2, 1, 1, 3, (byte) 1);
        gameState1NoOverwtites = new GameState(2, 0, 1, 3, (byte) 1);
    }

    @Test
    public void stoneOnOccupiedField(){
        Move move = new Move(3, 3, 0, 1);

        boolean result = MoveVerifier.isValid(move, map, gameState1);

        assertFalse(result);
    }

    @Test
    public void stoneHole(){
        Move move = new Move(3, 0, 0, 1);

        boolean result = MoveVerifier.isValid(move, map, gameState1);

        assertFalse(result);
    }

    @Test
    public void simpleCapturing(){
        Move moveTrue = new Move(2, 5, 0, 1);
        Move moveFalse = new Move(2, 2, 0, 1);

        boolean resultTrue = MoveVerifier.isValid(moveTrue, map, gameState1);
        boolean resultFalse = MoveVerifier.isValid(moveFalse, map, gameState1);

        assertTrue(resultTrue);
        assertFalse(resultFalse);
    }

    @Test
    public void complexCapturing(){
        //TODO
        //assertTrue(false, "test method not finished yet");
    }

    @Test
    public void capturingWithOneStone(){
        MapController map2 = new MapController(
            new String[]{
                "2 2 2 2 0 2 2 2", 
                "0 0 0 0 0 0 1 0", 
                "0 0 0 0 0 2 0 0", 
                "0 0 0 1 2 0 0 0",
                "0 0 0 2 1 0 0 0",
                "0 0 0 0 0 0 0 0", 
                "0 0 0 c 0 x 0 x", 
                "0 0 0 b 0 0 0 0"
            }, 
            new String[]{
                "7 7 2 <-> 0 0 6", 
                "7 0 1 <-> 0 7 5",
                "0 0 6 <-> 7 0 2"
            }, 
            8, 
            8, 
            0);

        Move move = new Move(4, 0, 0, 1);

        boolean result = MoveVerifier.isValid(move, map2, gameState1);

        assertFalse(result);
    }

    @Test
    public void overwriteTest(){
        //TODO
        //assertTrue(false, "test method not finished yet");
    }

    /*
     * Expansion Rule
     * Bomb Phase
     */
}
