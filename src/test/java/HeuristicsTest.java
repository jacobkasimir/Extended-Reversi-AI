import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class HeuristicsTest {

    @Test
    public void amountOfOwnTilesOverallTest1(){
        MapController map = new MapController(
            new String[]{
                "1 1 1 1 1 1 1 1", 
                "2 2 2 2 2 2 2 2", 
                "1 1 1 1 1 1 1 1", 
                "2 2 2 2 2 2 2 2", 
                "1 1 1 1 1 1 1 1", 
                "2 2 2 2 2 2 2 2",  
                "1 1 1 1 1 1 1 1", 
                "2 2 2 2 2 2 2 2" 
            }, 
            new String[]{},
            8, 8, 1);

        GameState state = new GameState(2, 1, 1, 1, (byte) 1);
        state.setOurPlayerId((byte) 1);

        assertEquals(0.5, Heuristics.amountOfOwnTilesOverall(map, state));
    }

    @Test
    public void amountOfOwnTilesOverallTest2(){
        MapController map = new MapController(
            new String[]{
                "1 1 1 1 1 1 1 1", 
                "2 2 2 2 2 2 2 2", 
                "3 3 3 3 3 3 3 3", 
                "4 4 4 4 4 4 4 4", 
                "5 5 5 5 5 5 5 5", 
                "6 6 6 6 6 6 6 6",  
                "7 7 7 7 7 7 7 7", 
                "8 8 8 8 8 8 8 8" 
            }, 
            new String[]{},
            8, 8, 1);

        GameState state = new GameState(8, 1, 1, 1, (byte) 1);
        state.setOurPlayerId((byte) 6);

        assertEquals(1.0/8, Heuristics.amountOfOwnTilesOverall(map, state));
    }

    @Test
    public void amountOfOwnTilesOverallTest3(){
        MapController map = new MapController(
            new String[]{
                "1 - 1 0 2 3 3 1", 
                "2 2 2 2 2 2 2 2", 
                "3 3 3 x 3 3 i 3", 
                "4 - - 4 3 4 c 4", 
                "5 0 5 0 5 5 1 b", 
                "4 6 6 6 - 6 6 6",  
                "7 7 - 7 7 7 - -", 
                "0 0 8 x 8 4 8 -" 
            }, 
            new String[]{},
            8, 8, 1);

        GameState state = new GameState(8, 1, 1, 1, (byte) 1);
        state.setOurPlayerId((byte) 4);

        assertEquals(6.0/64, Heuristics.amountOfOwnTilesOverall(map, state));
    }

    @Test
    public void amountOfOwnTilesToOthersTest1(){
        MapController map = new MapController(
            new String[]{
                "1 1 1 1 1 1 1 1", 
                "2 2 2 2 2 2 2 2", 
                "3 3 3 3 3 3 3 3", 
                "4 4 4 4 4 4 4 4", 
                "5 5 5 5 5 5 5 5", 
                "6 6 6 6 6 6 6 6",  
                "7 7 7 7 7 7 7 7", 
                "8 8 8 8 8 8 8 8" 
            }, 
            new String[]{},
            8, 8, 1);

        GameState state = new GameState(8, 1, 1, 1, (byte) 1);
        state.setOurPlayerId((byte) 6);

        assertEquals(8.0/56, Heuristics.amountOfOwnTilesToOthers(map, state));
    }

    @Test
    public void amountOfOwnTilesToOthersTest2(){
        MapController map = new MapController(
            new String[]{
                "1 1 1 1 1 1 1 1", 
                "2 2 2 2 2 2 2 2", 
                "1 1 1 1 1 1 1 1", 
                "2 2 2 2 2 2 2 2", 
                "1 1 1 1 1 1 1 1", 
                "2 2 2 2 2 2 2 2",  
                "1 1 1 1 1 1 1 1", 
                "2 2 2 2 2 2 2 2" 
            }, 
            new String[]{},
            8, 8, 1);

        GameState state = new GameState(2, 1, 1, 1, (byte) 1);
        state.setOurPlayerId((byte) 1);

        assertEquals(1.0, Heuristics.amountOfOwnTilesToOthers(map, state));
    }

    @Test
    public void amountOfOwnTilesToOthersTest3(){
        MapController map = new MapController(
            new String[]{
                "1 - 1 0 2 3 3 1", 
                "2 2 2 2 2 2 2 2", 
                "3 3 3 x 3 3 i 3", 
                "4 - - 4 3 4 c 4", 
                "5 0 5 0 5 5 1 b", 
                "4 6 6 6 - 6 6 6",  
                "7 7 - 7 7 7 - -", 
                "0 0 8 x 8 4 8 -" 
            }, 
            new String[]{},
            8, 8, 1);

        GameState state = new GameState(8, 1, 1, 1, (byte) 1);
        state.setOurPlayerId((byte) 4);

        assertEquals(6.0/40, Heuristics.amountOfOwnTilesToOthers(map, state));
    }

}
