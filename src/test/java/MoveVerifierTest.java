import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class MoveVerifierTest {

    private MapController map;
    private GameClient gameClient;
    private GameAttributes gameAttributes1;
    private GameAttributes gameAttributes1NoOverwrites;
    private GameAttributes gameAttributes2;
    private GameAttributes gameAttributes2NoBombs;

    @BeforeEach
    public void setUpGame(){
        gameClient = new GameClient();
        gameClient.initTransitions(new String[]{
            "7 7 2 <-> 0 0 6", 
            "7 0 1 <-> 0 7 5"}, 8,8);
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
            gameClient.getTransitions(), 
            8, 
            8, 
            0);

        gameAttributes1 = new GameAttributes(2, 1, 1, 3, (byte) 1);
        gameAttributes1NoOverwrites = new GameAttributes(2, 0, 1, 3, (byte) 1);
        gameAttributes2 = new GameAttributes(2, 1, 1, 3, (byte) 2);
        gameAttributes2NoBombs = new GameAttributes(2, 0, 0, 3, (byte) 2);
    }

    @Test
    public void stoneOnOccupiedField(){
        int move = MoveVerifier.encodeMove(3, 3, 0, 1);

        boolean result = MoveVerifier.isValid(move, map, gameAttributes1);

        assertFalse(result);
    }

    @Test
    public void stoneHole(){
        int move = MoveVerifier.encodeMove(3, 0, 0, 1);

        boolean result = MoveVerifier.isValid(move, map, gameAttributes1);

        assertFalse(result);
    }

    @Test
    public void simpleCapturing(){
        int moveTrue = MoveVerifier.encodeMove(2, 5, 0, 1);
        int moveFalse = MoveVerifier.encodeMove(2, 2, 0, 1);

        boolean resultTrue = MoveVerifier.isValid(moveTrue, map, gameAttributes1);
        boolean resultFalse = MoveVerifier.isValid(moveFalse, map, gameAttributes1);

        assertTrue(resultTrue);
        assertFalse(resultFalse);
    }

    @Test
    public void complexCapturing(){
        GameClient gc = new GameClient();
        gc.initTransitions(new String[]{
            "0 3 6 <-> 5 0 7", 
            "4 7 4 <-> 7 2 3",
        }, 8, 8);
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
            gc.getTransitions(), 
            8, 
            8, 
            0);

        int move = MoveVerifier.encodeMove(1, 3, 0, 1);

        boolean result = MoveVerifier.isValid(move, map2, gameAttributes1);

        assertTrue(result);
    }

    @Test
    public void capturingWithOneStone(){
        GameClient gc = new GameClient();
        gc.initTransitions(new String[]{
            "7 7 2 <-> 0 0 6", 
            "7 0 1 <-> 0 7 5",
            "0 0 6 <-> 7 0 2"
        }, 8, 8);

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
            gc.getTransitions(), 
            8, 
            8, 
            0);

        int move = MoveVerifier.encodeMove(4, 0, 0, 1);

        boolean result = MoveVerifier.isValid(move, map2, gameAttributes1);

        assertFalse(result);
    }

    @Test
    public void overwriteTest(){
        GameClient gc = new GameClient();
        gc.initTransitions( new String[]{
            "7 7 2 <-> 0 0 6", 
            "7 0 1 <-> 0 7 5",
            "0 0 6 <-> 7 0 2"
        }, 8, 8);

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
           gc.getTransitions(), 
            8, 
            8, 
            0
        );

        int moveTrue = MoveVerifier.encodeMove(6, 3, 0, 1);
        int moveFalse = MoveVerifier.encodeMove(3, 4, 0, 1);

        boolean resultTrue = MoveVerifier.isValid(moveTrue, map2, gameAttributes1);
        boolean resultFalse1 = MoveVerifier.isValid(moveFalse, map2, gameAttributes1);
        boolean resultFalse2 = MoveVerifier.isValid(moveTrue, map2, gameAttributes1NoOverwrites);
        
        assertTrue(resultTrue);
        assertFalse(resultFalse1);
        assertFalse(resultFalse2);
    }

    @Test
    public void expansionRule(){
        int moveTrue = MoveVerifier.encodeMove(7, 6, 0, 1);
        int moveFalse = MoveVerifier.encodeMove(7, 6, 0, 1);

        boolean resultTrue = MoveVerifier.isValid(moveTrue, map, gameAttributes1);
        boolean resultFalse = MoveVerifier.isValid(moveFalse, map, gameAttributes1NoOverwrites);

        assertTrue(resultTrue);
        assertFalse(resultFalse);
    }

    @Test
    public void bombPhase(){
        int move = MoveVerifier.encodeMove(3, 6, 0, 1);
        int moveFalse = MoveVerifier.encodeMove(0, 0, 0, 1);

        boolean resultTrue = MoveVerifier.isValid(move, map, gameAttributes2);
        boolean resultFalse1 = MoveVerifier.isValid(move, map, gameAttributes2NoBombs);
        boolean resultFalse2 = MoveVerifier.isValid(moveFalse, map, gameAttributes2);

        assertTrue(resultTrue);
        assertFalse(resultFalse1);
        assertFalse(resultFalse2);
    }

    @Test
    public void allMovesPhase1_easy(){
        IntLinkedList test = new IntLinkedList();
        GameAttributes gamestateTest = gameAttributes1;
        gamestateTest.setOurPlayerId((byte) 1);
        map.resetTiles(1, gamestateTest);
        test.add(MoveVerifier.encodeMove(2, 4, 0, 1));
        test.add(MoveVerifier.encodeMove(2, 5, 0, 1));
        test.add(MoveVerifier.encodeMove(3, 4, 0, 1));
        test.add(MoveVerifier.encodeMove(3, 5, 0, 1));
        test.add(MoveVerifier.encodeMove(4, 3, 0, 1));
        test.add(MoveVerifier.encodeMove(5, 3, 0, 1));
        test.add(MoveVerifier.encodeMove(4, 2, 0, 1));
        test.add(MoveVerifier.encodeMove(5,6,0,1));
        test.add(MoveVerifier.encodeMove(7,6,0,1));
        IntLinkedList res = MoveVerifier.allMovesPhase1(map, gamestateTest, gamestateTest.getOurPlayerId());
        IntLinkedList brutforce = calculateAllMoves_Bruteforce(map, gamestateTest);
        assertEquals(test, res);
        assertEquals(test, brutforce);
    }

    @Test
    public void allMovesPhase1_bonus(){
        IntLinkedList test = new IntLinkedList();
        GameAttributes gamestateTest = gameAttributes1;
        gamestateTest.setOurPlayerId((byte) 1);

        GameClient gc = new GameClient();
        gc.initTransitions( new String[]{
            "7 7 2 <-> 0 0 6", 
            "7 1 1 <-> 0 7 5"}, 8, 8);

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
            gc.getTransitions(), 
            8, 
            8, 
            0
        );
        map2.resetTiles(gamestateTest.getOurPlayerId(), gamestateTest);
        test.add(MoveVerifier.encodeMove(2, 4, 0, 1));
        test.add(MoveVerifier.encodeMove(2, 5, 0, 1));
        test.add(MoveVerifier.encodeMove(3, 4, 0, 1));
        test.add(MoveVerifier.encodeMove(3, 5, 0, 1));
        test.add(MoveVerifier.encodeMove(4, 3, 0, 1));
        test.add(MoveVerifier.encodeMove(5, 3, 20, 1));
        test.add(MoveVerifier.encodeMove(5, 3, 21, 1));
        test.add(MoveVerifier.encodeMove(4, 2, 0, 1));
        test.add(MoveVerifier.encodeMove(5,6,0,1));
        test.add(MoveVerifier.encodeMove(7,6,0,1));
        test.add(MoveVerifier.encodeMove(0,7,0,1));
        IntLinkedList res = MoveVerifier.allMovesPhase1(map2, gamestateTest, gamestateTest.getOurPlayerId());
        IntLinkedList brutforce = calculateAllMoves_Bruteforce(map2, gamestateTest);
        assertEquals(test, res);
        assertEquals(test, brutforce);
    }

    @Test
    public void allMovesPhase1_random(){
        IntLinkedList test = new IntLinkedList();
        GameAttributes gamestateTest = gameAttributes1;
        gamestateTest.setOurPlayerId((byte) 1);

        GameClient gc = new GameClient();
        gc.initTransitions( new String[]{
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
        }, 8, 8);

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
            gc.getTransitions(),
            8, 
            8, 
            0
        );
        map3.resetTiles(gamestateTest.getOurPlayerId(), gamestateTest);;
        IntLinkedList res = MoveVerifier.allMovesPhase1(map3, gamestateTest, gamestateTest.getOurPlayerId());
        IntLinkedList brutforce = calculateAllMoves_Bruteforce(map3, gamestateTest);
        assertEquals(brutforce, res);
    }

    @Test
    public void allMovesPhase1_ExpansionStones(){
        IntLinkedList test = new IntLinkedList();
        IntLinkedList tester = new IntLinkedList();
        GameAttributes gamestateTest =         gameAttributes1 = new GameAttributes(8, 0, 1, 3, (byte) 1);
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
            new Transitions(19,19),
            19, 
            19, 
            0
        );
        map3.resetTiles(gamestateTest.getOurPlayerId(), gamestateTest);;
        tester.add(MoveVerifier.encodeMove(11, 7, 0, 5));
        tester.add(MoveVerifier.encodeMove(11, 8, 0, 5));
        tester.add(MoveVerifier.encodeMove(11, 9, 0, 5));
        tester.add(MoveVerifier.encodeMove(11, 10, 0, 5));
        IntLinkedList res = MoveVerifier.allMovesPhase1(map3, gamestateTest, gamestateTest.getOurPlayerId());
        IntLinkedList brutforce = calculateAllMoves_Bruteforce(map3, gamestateTest);
        assertEquals(brutforce, res);
        assertEquals(tester, res);
    }

    @Test
    public void testFailedTimeout01(){
        GameClient gc = new GameClient();
        gc.initTransitions( new String[]{
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
        }, 15, 15);
        
        GameAttributes gamestateTest = gameAttributes1 = new GameAttributes(2, 0, 9, 0, (byte) 1);
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
                gc.getTransitions(),
                15, 
                15, 
                0
            );
            map3.resetTiles(gamestateTest.getOurPlayerId(), gamestateTest);
            IntLinkedList res = MoveVerifier.allMovesPhase1(map3, gamestateTest, gamestateTest.getOurPlayerId());
            IntLinkedList brutforce = calculateAllMoves_Bruteforce(map3, gamestateTest);
            assertEquals(brutforce, res);

    }

    private static IntLinkedList calculateAllMoves_Bruteforce(MapController controller, GameAttributes state) {

        IntLinkedList moves = new IntLinkedList();
        int width   = controller.getWidth();
        int height  = controller.getHeight();
        int ownId   = state.getOurPlayerId();
        int numPlayers = state.getNumberPlayers();
    
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                char tile = (char) controller.getTile(x, y);
    
                //Bonus zwei für Bombe und Override
                if (tile == 'b') {
                    int mBomb = MoveVerifier.encodeMove(x, y, 20, ownId);
                    if (MoveVerifier.isValid(mBomb, controller, state)) {
                        moves.add(mBomb);
                    }
                    int mOverride = MoveVerifier.encodeMove(x, y, 21, ownId);
                    if (MoveVerifier.isValid(mOverride, controller, state)) {
                        moves.add(mOverride);
                    }
                }
                //Choice für jede Player-ID einmal
                else if (tile == 'c') {
                    for (int choice = 1; choice <= numPlayers; choice++) {
                        int m = MoveVerifier.encodeMove(x, y, choice, ownId);
                        if (MoveVerifier.isValid(m, controller, state)) {
                            moves.add(m);
                        }
                    }
                }
                // Normales Feld, Expansion, Override 
                else {
                    int m = MoveVerifier.encodeMove(x, y, 0, ownId);
                    if (MoveVerifier.isValid(m, controller, state)) {
                        moves.add(m);
                    }
                }
            }
        }

        return moves;
    }
}
