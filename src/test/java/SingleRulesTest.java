import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class SingleRulesTest {
    
    private MapController map;
    private GameState gameState1;
    private GameState gameState2;
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

        gameState1 = new GameAttributes(2, 1, 1, 3, (byte) 1);
        gameState2 = new GameAttributes(2, 0, 0, 3, (byte) 1);
        gameState2.playerDisqualified((byte) 2);
    }

    @Test
    public void isNotHoleTest(){
        Move moveFalse = new Move(0, 0, 0, 0);
        Move moveTrue = new Move(1, 1, 0, 0);

        boolean resultFalse = GameRules.isNotHole(moveFalse, map);
        boolean resultTrue = GameRules.isNotHole(moveTrue, map);

        assertFalse(resultFalse);
        assertTrue(resultTrue);
    }

    @Test
    public void insideMapTest(){
        Move moveFalse1 = new Move(9, 0, 0, 0);
        Move moveFalse2 = new Move(-1, 0, 0, 0);
        Move moveFalse3 = new Move(0, 9, 0, 0);
        Move moveFalse4 = new Move(0, -1, 0, 0);
        Move moveTrue = new Move(1, 1, 0, 0);

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
        Move moveFalse1 = new Move(2, 3, 0, 0);
        Move moveFalse2 = new Move(5, 3, 0, 0);
        Move moveTrue = new Move(3, 3, 0, 0);

        boolean resultFalse1 = GameRules.occupied(moveFalse1, map);
        boolean resultFalse2 = GameRules.occupied(moveFalse2, map);
        boolean resultTrue = GameRules.occupied(moveTrue, map);

        assertFalse(resultFalse1);
        assertFalse(resultFalse2);
        assertTrue(resultTrue);
    }

    @Test
    public void hasBombTest(){
        Move move = new Move(4, 4, 0, 1);

        boolean resultTrue = GameRules.hasBomb(gameState1, move);
        boolean resultFalse = GameRules.hasBomb(gameState2, move);

        assertTrue(resultTrue);
        assertFalse(resultFalse);
    }

    @Test
    public void hasOverwriteTest(){
        Move move = new Move(4, 4, 0, 1);

        boolean resultTrue = GameRules.hasOverwrite(gameState1, move);
        boolean resultFalse = GameRules.hasOverwrite(gameState2, move);

        assertTrue(resultTrue);
        assertFalse(resultFalse);
    }

    @Test
    public void validMoveTest(){
        Move moveTrue = new Move(1, 1, 0, 1);
        Move moveFalse1 = new Move(1, 1, 0, 2);
        Move moveFalse2 = new Move(1, 1, 0, 3);
        Move moveFalse3 = new Move(1, 1, 0, 0);
        Move moveFalse4 = new Move(1, 1, 0, 10);

        boolean resultTrue = GameRules.validMove(moveTrue, gameState2);
        boolean resultFalse1 = GameRules.validMove(moveFalse1, gameState2);
        boolean resultFalse2 = GameRules.validMove(moveFalse2, gameState2);
        boolean resultFalse3 = GameRules.validMove(moveFalse3, gameState2);
        boolean resultFalse4 = GameRules.validMove(moveFalse4, gameState2);

        assertTrue(resultTrue);
        assertFalse(resultFalse1);
        assertFalse(resultFalse2);
        assertFalse(resultFalse3);
        assertFalse(resultFalse4);
    }

    @Test
    public void isExpansionTest(){
        Move moveTrue1 = new Move(5, 6, 0, 1);
        Move moveTrue2 = new Move(7, 6, 0, 1);
        Move moveFalse1 = new Move(6, 6, 0, 1);
        Move moveFalse2 = new Move(7, 5, 0, 1);

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
        Move moveTrue1 = new Move(3, 7, 20, 1);
        Move moveTrue2 = new Move(3, 7, 21, 1);
        Move moveFalse1 = new Move(3, 7, 0, 1);
        Move moveFalse2 = new Move(4, 5, 20, 1);

        boolean resultTrue1 = GameRules.checkSpecial(gameState1, moveTrue1, map);
        boolean resultTrue2 = GameRules.checkSpecial(gameState1, moveTrue2, map);
        boolean resultFalse1 = GameRules.checkSpecial(gameState1, moveFalse1, map);
        boolean resultFalse2 = GameRules.checkSpecial(gameState1, moveFalse2, map);

        assertTrue(resultTrue1);
        assertTrue(resultTrue2);
        assertFalse(resultFalse1);
        assertFalse(resultFalse2);
    }

    @Test
    public void checkSpecialTestChoose(){
        Move moveTrue1 = new Move(3, 6, 2, 1);
        Move moveTrue2 = new Move(3, 6, 1, 1);
        Move moveFalse1 = new Move(3, 6, 0, 1);
        Move moveFalse2 = new Move(2, 3, 1, 1);

        boolean resultTrue1 = GameRules.checkSpecial(gameState1, moveTrue1, map);
        boolean resultTrue2 = GameRules.checkSpecial(gameState1, moveTrue2, map);
        boolean resultFalse1 = GameRules.checkSpecial(gameState1, moveFalse1, map);
        boolean resultFalse2 = GameRules.checkSpecial(gameState1, moveFalse2, map);

        assertTrue(resultTrue1);
        assertTrue(resultTrue2);
        assertFalse(resultFalse1);
        assertFalse(resultFalse2);
    }

    @Test
    public void capturingTest(){
        Move moveTrue1 = new Move(2, 4, 0, 1);
        Move moveTrue2 = new Move(2, 3, 0, 2);
        Move moveFalse1 = new Move(1, 1, 0, 1);
        Move moveFalse2 = new Move(4, 5, 1, 1);

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
