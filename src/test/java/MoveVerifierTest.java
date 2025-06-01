import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import control.GameClient;
import control.Coder;
import gamestate.GameState;
import gamestate.attributes.GameAttributes;
import gamestate.map.MapController;
import validation.MoveVerifier;

public class MoveVerifierTest {

    private MapController map;
    private GameClient gameClient;
    private GameAttributes gameAttributes1;
    private GameAttributes gameAttributes1NoOverwrites;
    private GameAttributes gameAttributes2;
    private GameAttributes gameAttributes2NoBombs;
    private GameState gameState1;
    private GameState gameState2;
    private GameState gameStateNoBombs;
    private GameState gameStateNoOverrides;

    @BeforeEach
    public void setUpGame() {
        gameClient = new GameClient();
        gameClient.initTransitions(new String[] {
                "7 7 2 <-> 0 0 6",
                "7 0 1 <-> 0 7 5" }, 8, 8);
        map = new MapController(
                new String[] {
                        "- - - - - - - -",
                        "0 0 0 0 0 0 1 0",
                        "0 0 0 0 0 2 0 0",
                        "0 0 0 1 2 0 0 0",
                        "0 0 0 2 1 0 0 0",
                        "0 0 0 0 0 0 0 0",
                        "0 0 0 c 0 x 0 x",
                        "0 0 0 b 0 0 0 0" },
                gameClient.getTransitions(),
                8,
                8,
                0);

        gameAttributes1 = new GameAttributes(2, 1, 1, 3, 1);
        gameAttributes1NoOverwrites = new GameAttributes(2, 0, 1, 3, 1);
        gameAttributes2 = new GameAttributes(2, 1, 1, 3, 2);
        gameAttributes2NoBombs = new GameAttributes(2, 0, 0, 3, 2);
        gameState1 = new GameState(map, gameAttributes1);
        gameState2 = new GameState(map, gameAttributes2);
        gameStateNoBombs = new GameState(map, gameAttributes2NoBombs);
        gameStateNoOverrides = new GameState(map, gameAttributes1NoOverwrites);
    }

    @Test
    public void stoneOnOccupiedField() {
        int move = Coder.encodeMove(3, 3, 0, 1);

        boolean result = MoveVerifier.isValid(move, gameState1);

        assertFalse(result);
    }

    @Test
    public void stoneHole() {
        int move = Coder.encodeMove(3, 0, 0, 1);

        boolean result = MoveVerifier.isValid(move, gameState1);

        assertFalse(result);
    }

    @Test
    public void simpleCapturing() {
        int moveTrue = Coder.encodeMove(2, 5, 0, 1);
        int moveFalse = Coder.encodeMove(2, 2, 0, 1);

        boolean resultTrue = MoveVerifier.isValid(moveTrue, gameState1);
        boolean resultFalse = MoveVerifier.isValid(moveFalse, gameState1);

        assertTrue(resultTrue);
        assertFalse(resultFalse);
    }

    @Test
    public void complexCapturing() {
        GameClient gc = new GameClient();
        gc.initTransitions(new String[] {
                "0 3 6 <-> 5 0 7",
                "4 7 4 <-> 7 2 3",
        }, 8, 8);
        MapController map2 = new MapController(
                new String[] {
                        "0 0 0 0 0 2 0 0",
                        "0 0 0 0 0 0 2 0",
                        "0 0 0 0 0 0 0 2",
                        "2 0 0 0 0 0 0 0",
                        "0 0 0 0 0 0 0 0",
                        "0 0 0 0 1 0 0 0",
                        "0 0 0 0 2 0 0 0",
                        "0 0 0 0 2 0 0 0"
                },
                gc.getTransitions(),
                8,
                8,
                0);

        int move = Coder.encodeMove(1, 3, 0, 1);
        GameState gameState = new GameState(map2, gameAttributes1);
        boolean result = MoveVerifier.isValid(move, gameState);

        assertTrue(result);
    }

    @Test
    public void capturingWithOneStone() {
        GameClient gc = new GameClient();
        gc.initTransitions(new String[] {
                "7 7 2 <-> 0 0 6",
                "7 0 1 <-> 0 7 5",
                "0 0 6 <-> 7 0 2"
        }, 8, 8);

        MapController map2 = new MapController(
                new String[] {
                        "2 2 2 2 0 2 2 2",
                        "0 0 0 0 0 0 1 0",
                        "0 0 0 0 0 2 0 0",
                        "0 0 0 1 2 0 0 0",
                        "0 0 0 2 1 0 0 0",
                        "0 0 0 0 0 0 0 0",
                        "0 0 0 c 0 x 0 x",
                        "0 0 0 b 0 0 0 0"
                },
                gc.getTransitions(),
                8,
                8,
                0);

        int move = Coder.encodeMove(4, 0, 0, 1);
        GameState gameState = new GameState(map2, gameAttributes1);
        boolean result = MoveVerifier.isValid(move, gameState);

        assertFalse(result);
    }

    @Test
    public void overwriteTest() {
        GameClient gc = new GameClient();
        gc.initTransitions(new String[] {
                "7 7 2 <-> 0 0 6",
                "7 0 1 <-> 0 7 5",
                "0 0 6 <-> 7 0 2"
        }, 8, 8);

        MapController map2 = new MapController(
                new String[] {
                        "0 0 0 0 0 0 0 0",
                        "0 0 0 0 0 0 0 0",
                        "0 0 0 0 0 0 0 0",
                        "0 1 2 2 2 2 1 0",
                        "0 0 0 2 0 0 0 0",
                        "0 0 0 0 0 0 0 0",
                        "0 0 0 0 0 0 0 0",
                        "0 0 0 0 0 0 0 0"
                },
                gc.getTransitions(),
                8,
                8,
                0);

        int moveTrue = Coder.encodeMove(6, 3, 0, 1);
        int moveFalse = Coder.encodeMove(3, 4, 0, 1);
        GameState gameState3 = new GameState(map2, gameAttributes1);
        GameState gameState4 = new GameState(map2, gameAttributes1NoOverwrites);

        boolean resultTrue = MoveVerifier.isValid(moveTrue, gameState3);
        boolean resultFalse1 = MoveVerifier.isValid(moveFalse, gameState3);
        boolean resultFalse2 = MoveVerifier.isValid(moveTrue, gameState4);

        assertTrue(resultTrue);
        assertFalse(resultFalse1);
        assertFalse(resultFalse2);
    }

    @Test
    public void expansionRule() {
        int moveTrue = Coder.encodeMove(7, 6, 0, 1);
        int moveFalse = Coder.encodeMove(7, 6, 0, 1);
        gameState1 = new GameState(map, gameAttributes1);
        boolean resultTrue = MoveVerifier.isValid(moveTrue, gameState1);
        boolean resultFalse = MoveVerifier.isValid(moveFalse, gameStateNoOverrides);

        assertTrue(resultTrue);
        assertFalse(resultFalse);
    }

    @Test
    public void bombPhase() {
        int move = Coder.encodeMove(3, 6, 0, 1);
        int moveFalse = Coder.encodeMove(0, 0, 0, 1);

        boolean resultTrue = MoveVerifier.isValid(move, gameState2);
        boolean resultFalse1 = MoveVerifier.isValid(move, gameStateNoBombs);
        boolean resultFalse2 = MoveVerifier.isValid(moveFalse, gameState2);

        assertTrue(resultTrue);
        assertFalse(resultFalse1);
        assertFalse(resultFalse2);
    }

}
