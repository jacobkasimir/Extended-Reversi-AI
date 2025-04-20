import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

public class MoveVerifierTest {

    private MapController map;
    private GameState gameState1;
    private GameState gameState1NoOverwrites;
    private GameState gameState2;
    private GameState gameState2NoBombs;

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
        gameState1NoOverwrites = new GameState(2, 0, 1, 3, (byte) 1);
        gameState2 = new GameState(2, 1, 1, 3, (byte) 2);
        gameState2NoBombs = new GameState(2, 0, 0, 3, (byte) 2);
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
        MapController map2 = new MapController(
            new String[]{
                "0 0 0 0 0 2 0 0", 
                "0 0 0 0 0 0 2 0", 
                "0 0 0 0 0 0 0 2", 
                "2 0 0 0 0 0 0 0",
                "0 0 0 0 0 0 0 0",
                "0 0 0 0 1 0 0 0", 
                "0 0 0 0 2 0 0 0", 
                "0 0 0 0 2 0 0 0"
            }, 
            new String[]{
                "0 3 6 <-> 5 0 7", 
                "4 7 4 <-> 7 2 3",
            }, 
            8, 
            8, 
            0);

        Move move = new Move(1, 3, 0, 1);

        boolean result = MoveVerifier.isValid(move, map2, gameState1);

        assertTrue(result);
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
        MapController map2 = new MapController(
            new String[]{
                "0 0 0 0 0 0 0 0", 
                "0 0 0 0 0 0 0 0", 
                "0 0 0 0 0 0 0 0", 
                "0 1 2 2 2 2 1 0",
                "0 0 0 2 0 0 0 0",
                "0 0 0 0 0 0 0 0", 
                "0 0 0 0 0 0 0 0", 
                "0 0 0 0 0 0 0 0"
            }, 
            new String[]{
                "7 7 2 <-> 0 0 6", 
                "7 0 1 <-> 0 7 5",
                "0 0 6 <-> 7 0 2"
            }, 
            8, 
            8, 
            0
        );

        Move moveTrue = new Move(6, 3, 0, 1);
        Move moveFalse = new Move(3, 4, 0, 1);

        boolean resultTrue = MoveVerifier.isValid(moveTrue, map2, gameState1);
        boolean resultFalse1 = MoveVerifier.isValid(moveFalse, map2, gameState1);
        boolean resultFalse2 = MoveVerifier.isValid(moveTrue, map2, gameState1NoOverwrites);
        
        assertTrue(resultTrue);
        assertFalse(resultFalse1);
        assertFalse(resultFalse2);
    }

    @Test
    public void expansionRule(){
        Move moveTrue = new Move(7, 6, 0, 1);
        Move moveFalse = new Move(7, 6, 0, 1);

        boolean resultTrue = MoveVerifier.isValid(moveTrue, map, gameState1);
        boolean resultFalse = MoveVerifier.isValid(moveFalse, map, gameState1NoOverwrites);

        assertTrue(resultTrue);
        assertFalse(resultFalse);
    }

    @Test
    public void bombPhase(){
        Move move = new Move(3, 3, 0, 1);
        Move moveFalse = new Move(0, 0, 0, 1);

        boolean resultTrue = MoveVerifier.isValid(move, map, gameState2);
        boolean resultFalse1 = MoveVerifier.isValid(move, map, gameState2NoBombs);
        boolean resultFalse2 = MoveVerifier.isValid(moveFalse, map, gameState2);
        
        //debugging:
        assertFalse(gameState2.isPhase1());
        assertTrue(GameRules.hasBomb(gameState2, move));


        //assertTrue(resultTrue);
        assertFalse(resultFalse1);
        assertFalse(resultFalse2);
    }
}
