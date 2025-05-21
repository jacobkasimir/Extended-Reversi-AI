import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import gamestate.map.MapController;
import gamestate.attributes.GameAttributes;
import control.GameClient;
import validation.MoveVerifier;
import validation.GameRules;

public class SingleRulesTest {
    
    private MapController map;
    private GameAttributes gameAttributes1;
    private GameAttributes gameAttributes2;
    private GameClient gc;


    @BeforeEach
    public void setUpMap(){
        gc = new GameClient();
        gc.initTransitions(new String[]{
            "7 7 2 <-> 0 0 6", 
            "7 0 1 <-> 0 7 5"}, 8, 8);

        map = new MapController(
            new String[]{
                "- - - - - - - -", 
                "0 0 0 0 0 0 0 0", 
                "0 0 0 0 0 0 0 0", 
                "0 0 0 1 2 0 0 0",
                "0 0 0 2 1 0 0 0",
                "0 0 0 0 0 0 0 0", 
                "0 0 0 c 0 x 0 x", 
                "0 0 0 b 0 0 0 0"}, 
            gc.getTransitions(), 
            8, 
            8, 
            0);

        gameAttributes1 = new GameAttributes(2, 1, 1, 3, (byte) 1);
        gameAttributes2 = new GameAttributes(2, 0, 0, 3, (byte) 1);
        gameAttributes2.playerDisqualified((byte) 2);
    }

    @Test
    public void isNotHoleTest(){
        int moveFalse = MoveVerifier.encodeMove(0, 0, 0, 0);
        int moveTrue = MoveVerifier.encodeMove(1, 1, 0, 0);

        boolean resultFalse = GameRules.isNotHole(moveFalse, map);
        boolean resultTrue = GameRules.isNotHole(moveTrue, map);

        assertFalse(resultFalse);
        assertTrue(resultTrue);
    }

    @Test
    public void insideMapTest(){
        int moveFalse1 = MoveVerifier.encodeMove(9, 0, 0, 0);
        int moveFalse2 = MoveVerifier.encodeMove(-1, 0, 0, 0);
        int moveFalse3 = MoveVerifier.encodeMove(0, 9, 0, 0);
        int moveFalse4 = MoveVerifier.encodeMove(0, -1, 0, 0);
        int moveTrue = MoveVerifier.encodeMove(1, 1, 0, 0);

        boolean resultFalse1 = GameRules.insideMap(moveFalse1, map);
        boolean resultFalse2 = GameRules.insideMap(moveFalse2, map);
        boolean resultFalse3 = GameRules.insideMap(moveFalse3, map);
        boolean resultFalse4 = GameRules.insideMap(moveFalse4, map);
        boolean resultTrue = GameRules.isNotHole(moveTrue, map);

        assertFalse(resultFalse1);
        assertFalse(resultFalse2);
        assertFalse(resultFalse3);
        assertFalse(resultFalse4);
        assertTrue(resultTrue);
    }

    @Test
    public void occupiedTest(){
        int moveFalse1 = MoveVerifier.encodeMove(2, 3, 0, 0);
        int moveFalse2 = MoveVerifier.encodeMove(5, 3, 0, 0);
        int moveTrue = MoveVerifier.encodeMove(3, 3, 0, 0);

        boolean resultFalse1 = GameRules.occupied(moveFalse1, map);
        boolean resultFalse2 = GameRules.occupied(moveFalse2, map);
        boolean resultTrue = GameRules.occupied(moveTrue, map);

        assertFalse(resultFalse1);
        assertFalse(resultFalse2);
        assertTrue(resultTrue);
    }

    @Test
    public void hasBombTest(){
        int move = MoveVerifier.encodeMove(4, 4, 0, 1);

        boolean resultTrue = GameRules.hasBomb(gameAttributes1, move);
        boolean resultFalse = GameRules.hasBomb(gameAttributes2, move);

        assertTrue(resultTrue);
        assertFalse(resultFalse);
    }

    @Test
    public void hasOverrideTest(){
        int move = MoveVerifier.encodeMove(4, 4, 0, 1);

        boolean resultTrue = GameRules.hasOverride(gameAttributes1, move);
        boolean resultFalse = GameRules.hasOverride(gameAttributes2, move);

        assertTrue(resultTrue);
        assertFalse(resultFalse);
    }

    @Test
    public void validMoveTest(){
        int moveTrue = MoveVerifier.encodeMove(1, 1, 0, 1);
        int moveFalse1 = MoveVerifier.encodeMove(1, 1, 0, 2);
        int moveFalse2 = MoveVerifier.encodeMove(1, 1, 0, 3);
        int moveFalse3 = MoveVerifier.encodeMove(1, 1, 0, 0);
        int moveFalse4 = MoveVerifier.encodeMove(1, 1, 0, 10);

        boolean resultTrue = GameRules.validPlayer(moveTrue, gameAttributes2);
        boolean resultFalse1 = GameRules.validPlayer(moveFalse1, gameAttributes2);
        boolean resultFalse2 = GameRules.validPlayer(moveFalse2, gameAttributes2);
        boolean resultFalse3 = GameRules.validPlayer(moveFalse3, gameAttributes2);
        boolean resultFalse4 = GameRules.validPlayer(moveFalse4, gameAttributes2);

        assertTrue(resultTrue);
        assertFalse(resultFalse1);
        assertFalse(resultFalse2);
        assertFalse(resultFalse3);
        assertFalse(resultFalse4);
    }

    @Test
    public void isExpansionTest(){
        int moveTrue1 = MoveVerifier.encodeMove(5, 6, 0, 1);
        int moveTrue2 = MoveVerifier.encodeMove(7, 6, 0, 1);
        int moveFalse1 = MoveVerifier.encodeMove(6, 6, 0, 1);
        int moveFalse2 = MoveVerifier.encodeMove(7, 5, 0, 1);

        boolean resultTrue1 = GameRules.isExpansion(map, moveTrue1);
        boolean resultTrue2 = GameRules.isExpansion(map, moveTrue2);
        boolean resultFalse1 = GameRules.isExpansion(map, moveFalse1);
        boolean resultFalse2 = GameRules.isExpansion(map, moveFalse2);

        assertTrue(resultTrue1);
        assertTrue(resultTrue2);
        assertFalse(resultFalse1);
        assertFalse(resultFalse2);
    }

    @Test
    public void checkSpecialTestBombs(){
        int moveTrue1 = MoveVerifier.encodeMove(3, 7, 20, 1);
        int moveTrue2 = MoveVerifier.encodeMove(3, 7, 21, 1);
        int moveFalse1 = MoveVerifier.encodeMove(3, 7, 0, 1);
        int moveFalse2 = MoveVerifier.encodeMove(4, 5, 20, 1);

        boolean resultTrue1 = GameRules.checkSpecial(gameAttributes1, moveTrue1, map);
        boolean resultTrue2 = GameRules.checkSpecial(gameAttributes1, moveTrue2, map);
        boolean resultFalse1 = GameRules.checkSpecial(gameAttributes1, moveFalse1, map);
        boolean resultFalse2 = GameRules.checkSpecial(gameAttributes1, moveFalse2, map);

        assertTrue(resultTrue1);
        assertTrue(resultTrue2);
        assertFalse(resultFalse1);
        assertFalse(resultFalse2);
    }

    @Test
    public void checkSpecialTestChoose(){
        int moveTrue1 = MoveVerifier.encodeMove(3, 6, 2, 1);
        int moveTrue2 = MoveVerifier.encodeMove(3, 6, 1, 1);
        int moveFalse1 = MoveVerifier.encodeMove(3, 6, 0, 1);
        int moveFalse2 = MoveVerifier.encodeMove(2, 3, 1, 1);

        boolean resultTrue1 = GameRules.checkSpecial(gameAttributes1, moveTrue1, map);
        boolean resultTrue2 = GameRules.checkSpecial(gameAttributes1, moveTrue2, map);
        boolean resultFalse1 = GameRules.checkSpecial(gameAttributes1, moveFalse1, map);
        boolean resultFalse2 = GameRules.checkSpecial(gameAttributes1, moveFalse2, map);

        assertTrue(resultTrue1);
        assertTrue(resultTrue2);
        assertFalse(resultFalse1);
        assertFalse(resultFalse2);
    }

    @Test
    public void capturingTest(){
        int moveTrue1 = MoveVerifier.encodeMove(2, 4, 0, 1);
        int moveTrue2 = MoveVerifier.encodeMove(2, 3, 0, 2);
        int moveFalse1 = MoveVerifier.encodeMove(1, 1, 0, 1);
        int moveFalse2 = MoveVerifier.encodeMove(4, 5, 1, 1);

        boolean resultTrue1 = GameRules.capturing(moveTrue1, map);
        boolean resultTrue2 = GameRules.capturing(moveTrue2, map);
        boolean resultFalse1 = GameRules.capturing(moveFalse1, map);
        boolean resultFalse2 = GameRules.capturing(moveFalse2, map);

        assertTrue(resultTrue1);
        assertTrue(resultTrue2);
        assertFalse(resultFalse1);
        assertFalse(resultFalse2);
    }
}
