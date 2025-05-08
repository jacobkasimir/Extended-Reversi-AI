import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
        Move move = new Move(3, 6, 0, 1);
        Move moveFalse = new Move(0, 0, 0, 1);

        boolean resultTrue = MoveVerifier.isValid(move, map, gameState2);
        boolean resultFalse1 = MoveVerifier.isValid(move, map, gameState2NoBombs);
        boolean resultFalse2 = MoveVerifier.isValid(moveFalse, map, gameState2);

        assertTrue(resultTrue);
        assertFalse(resultFalse1);
        assertFalse(resultFalse2);
    }

    @Test
    public void allMovesPhase1_easy(){
        ArrayList<Move> test = new ArrayList<>();
        GameState gamestateTest = gameState1;
        gamestateTest.setOurPlayerId((byte) 1);
        map.resetOurTiles(1);
        test.add(new Move(2, 4, 0, 1));
        test.add(new Move(2, 5, 0, 1));
        test.add(new Move(3, 4, 0, 1));
        test.add(new Move(3, 5, 0, 1));
        test.add(new Move(4, 3, 0, 1));
        test.add(new Move(5, 3, 0, 1));
        test.add(new Move(4, 2, 0, 1));
        test.add(new Move(5,6,0,1));
        test.add(new Move(7,6,0,1));
        ArrayList<Move> res = MoveVerifier.allMovesPhase1(map, gamestateTest);
        ArrayList<Move> brutforce = calculateAllMoves_Bruteforce(map, gamestateTest);
        assertEquals(new HashSet<>(test), new HashSet<>(res));
        assertEquals(new HashSet<>(test), new HashSet<>(brutforce));
    }

    @Test
    public void allMovesPhase1_bonus(){
        ArrayList<Move> test = new ArrayList<>();
        GameState gamestateTest = gameState1;
        gamestateTest.setOurPlayerId((byte) 1);
        MapController map2 = new MapController(
            new String[]{
                "- - - - - - - -", 
                "0 0 0 0 0 0 1 2", 
                "0 0 0 0 0 2 1 0", 
                "0 0 0 1 2 b 0 0",
                "0 0 0 2 1 0 0 0",
                "0 0 0 0 0 0 0 0", 
                "0 0 0 c 0 x 0 x", 
                "0 0 0 b 0 0 0 0"}, 
            new String[]{
                "7 7 2 <-> 0 0 6", 
                "7 1 1 <-> 0 7 5"}, 
            8, 
            8, 
            0
        );
        map2.resetOurTiles(1);
        test.add(new Move(2, 4, 0, 1));
        test.add(new Move(2, 5, 0, 1));
        test.add(new Move(3, 4, 0, 1));
        test.add(new Move(3, 5, 0, 1));
        test.add(new Move(4, 3, 0, 1));
        test.add(new Move(5, 3, 20, 1));
        test.add(new Move(5, 3, 21, 1));
        test.add(new Move(4, 2, 0, 1));
        test.add(new Move(5,6,0,1));
        test.add(new Move(7,6,0,1));
        test.add(new Move(0,7,0,1));
        ArrayList<Move> res = MoveVerifier.allMovesPhase1(map2, gamestateTest);
        ArrayList<Move> brutforce = calculateAllMoves_Bruteforce(map2, gamestateTest);
        assertEquals(new HashSet<>(test), new HashSet<>(res));
        assertEquals(new HashSet<>(test), new HashSet<>(brutforce));
    }

    @Test
    public void allMovesPhase1_random(){
        ArrayList<Move> test = new ArrayList<>();
        GameState gamestateTest = gameState1;
        gamestateTest.setOurPlayerId((byte) 1);
        MapController map3 = new MapController(
            new String[]{
                "0 0 0 0 0 0 0 0",
                "0 1 2 0 0 0 3 0",
                "0 0 c 0 4 0 0 0",
                "0 0 0 i 0 0 0 0",
                "0 0 5 0 0 0 0 0",
                "0 6 0 0 0 x 0 0",
                "0 0 0 0 7 0 0 0",
                "0 0 0 0 0 0 0 0"}, 
            new String[]{
                "0 1 0 <-> 7 7 2",
                "1 6 0 <-> 0 6 2",
                "5 2 3 <-> 5 5 4",
                "2 2 0 <-> 2 5 0",
                "3 3 2 <-> 7 1 6",
                "0 7 4 <-> 4 7 0",
                "7 0 6 <-> 7 3 2",
                "4 4 1 <-> 6 6 5",
                "2 7 3 <-> 2 4 7",
                "7 2 5 <-> 3 2 1"
            },
            8, 
            8, 
            0
        );
        map3.resetOurTiles(1);
        ArrayList<Move> res = MoveVerifier.allMovesPhase1(map3, gamestateTest);
        ArrayList<Move> brutforce = calculateAllMoves_Bruteforce(map3, gamestateTest);
        assertEquals(new HashSet<>(brutforce), new HashSet<>(res));
    }

    @Test
    public void allMovesPhase1_ExpansionStones(){
        ArrayList<Move> test = new ArrayList<>();
        ArrayList<Move> tester = new ArrayList<>();
        GameState gamestateTest =         gameState1 = new GameState(8, 0, 1, 3, (byte) 1);
        gamestateTest.setOurPlayerId((byte) 5);
        MapController map3 = new MapController(
            new String[]{
"0 0 2 1 1 1 - - - - - - - - 0 3 4 3 0 0",
"0 0 2 1 0 0 - - - - - - - - 0 0 4 3 0 0",
"0 0 2 1 0 0 - - - - - - - - 0 0 4 3 0 0",
"0 0 0 0 0 0 - - - - - - - - 0 3 4 0 0 0",
"0 0 0 0 0 0 0 - - - - - - 0 0 0 0 0 0 0",
"- - - - - 0 b 0 0 0 0 0 0 b 0 - - - - -",
"- - - - - - 0 0 0 0 0 0 0 0 - - - - - -",
"- - - - - - 0 0 0 0 0 0 0 0 - - - - - -",
"- - - - - - 0 0 0 5 x 0 0 0 - - - - - -",
"- - - - - - 0 0 0 5 x 0 0 0 - - - - - -",
"- - - - - - 0 0 0 0 0 0 0 0 - - - - - -",
"- - - - - - 0 0 0 0 0 0 0 0 - - - - - -",
"- - - - - 0 b 0 0 0 0 0 0 b 0 - - - - -",
"0 0 0 0 0 0 0 - - - - - - 0 0 0 0 0 0 0",
"0 0 6 0 0 0 - - - - - - - - 0 0 0 0 0 0",
"0 0 6 6 0 0 - - - - - - - - 0 8 7 7 7 0",
"0 0 6 6 0 0 - - - - - - - - 0 8 8 7 0 0",
"0 0 0 6 0 0 - - - - - - - - 0 8 0 0 0 0",
"0 0 0 0 0 0 - - - - - - - - 0 0 0 0 0 0"}, 
            new String[]{},
            19, 
            19, 
            0
        );
        map3.resetOurTiles(5);
        tester.add(new Move(11, 7, 0, 5));
        tester.add(new Move(11, 8, 0, 5));
        tester.add(new Move(11, 9, 0, 5));
        tester.add(new Move(11, 10, 0, 5));
        ArrayList<Move> res = MoveVerifier.allMovesPhase1(map3, gamestateTest);
        ArrayList<Move> brutforce = calculateAllMoves_Bruteforce(map3, gamestateTest);
        assertEquals(new HashSet<>(brutforce), new HashSet<>(res));
        assertEquals(new HashSet<>(tester), new HashSet<>(res));
    }

    @Test
    public void testFailedTimeout01(){
        GameState gamestateTest =         gameState1 = new GameState(2, 0, 9, 0, (byte) 1);
            gamestateTest.setOurPlayerId((byte) 1);
            MapController map3 = new MapController(
                new String[]{
                    "1 1 i i i i i i i i i i i i i",
                    "1 1 c c c c c c c c c c c c i",
                    "1 1 1 1 1 1 b x b b b b b c i",
                    "1 1 1 1 1 b b x b b 1 1 1 c i",
                    "1 1 1 1 2 b 1 x b b 2 1 1 c i",
                    "1 1 2 2 b b b 1 b 2 b b 1 c i",
                    "1 2 1 2 b b x x 2 b b b 1 c i",
                    "2 2 2 2 2 1 1 2 2 2 2 2 2 2 2",
                    "1 2 2 2 b 2 2 1 x b b b 1 c i",
                    "1 1 2 b b 2 b 1 b b b 1 b c i",
                    "1 1 2 2 2 b b 1 b b 2 1 b c i",
                    "1 1 1 1 1 b b 1 b b 2 2 b c i",
                    "1 1 1 b b b b 1 b b 2 b b c i",
                    "1 1 2 c c c c 1 c c c c c c i",
                    "1 1 2 i i i i i i i i i i i i"}, 
                new String[]{
                    "0 0 0 <-> 0 14 4",
    "1 0 0 <-> 1 14 4",
    "2 0 0 <-> 2 14 4",
    "3 0 0 <-> 3 14 4",
    "4 0 0 <-> 4 14 4",
    "5 0 0 <-> 5 14 4",
    "6 0 0 <-> 6 14 4",
    "7 0 0 <-> 7 14 4",
    "8 0 0 <-> 8 14 4",
    "9 0 0 <-> 9 14 4",
    "10 0 0 <-> 10 14 4",
    "11 0 0 <-> 11 14 4",
    "12 0 0 <-> 12 14 4",
    "13 0 0 <-> 13 14 4",
    "14 0 0 <-> 14 14 4",
    "0 0 6 <-> 14 0 2",
    "0 1 6 <-> 14 1 2",
    "0 2 6 <-> 14 2 2",
    "0 3 6 <-> 14 3 2",
    "0 4 6 <-> 14 4 2",
    "0 5 6 <-> 14 5 2",
    "0 6 6 <-> 14 6 2",
    "0 7 6 <-> 14 7 2",
    "0 8 6 <-> 14 8 2",
    "0 9 6 <-> 14 9 2",
    "0 10 6 <-> 14 10 2",
    "0 11 6 <-> 14 11 2",
    "0 12 6 <-> 14 12 2",
    "0 13 6 <-> 14 13 2",
    "0 14 6 <-> 14 14 2"
                },
                15, 
                15, 
                0
            );
            map3.resetOurTiles(1);
            ArrayList<Move> res = MoveVerifier.allMovesPhase1(map3, gamestateTest);
            ArrayList<Move> brutforce = calculateAllMoves_Bruteforce(map3, gamestateTest);
            assertEquals(new HashSet<>(brutforce), new HashSet<>(res));

    }

    private static ArrayList<Move> calculateAllMoves_Bruteforce(MapController controller, GameState state) {

        Set<Move> moves = new LinkedHashSet<>();
        int width   = controller.getWidth();
        int height  = controller.getHeight();
        int ownId   = state.getOurPlayerId();
        int numPlayers = state.getNumberPlayers();
    
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                char tile = (char) controller.getTile(x, y);
    
                //Bonus zwei für Bombe und Override
                if (tile == 'b') {
                    Move mBomb = new Move(x, y, 20, ownId);
                    if (MoveVerifier.isValid(mBomb, controller, state)) {
                        moves.add(mBomb);
                    }
                    Move mOverride = new Move(x, y, 21, ownId);
                    if (MoveVerifier.isValid(mOverride, controller, state)) {
                        moves.add(mOverride);
                    }
                }
                //Choice für jede Player-ID einmal
                else if (tile == 'c') {
                    for (int choice = 1; choice <= numPlayers; choice++) {
                        Move m = new Move(x, y, choice, ownId);
                        if (MoveVerifier.isValid(m, controller, state)) {
                            moves.add(m);
                        }
                    }
                }
                // Normales Feld, Expansion, Override 
                else {
                    Move m = new Move(x, y, 0, ownId);
                    if (MoveVerifier.isValid(m, controller, state)) {
                        moves.add(m);
                    }
                }
            }
        }

        return new ArrayList<>(moves);
    }
}
