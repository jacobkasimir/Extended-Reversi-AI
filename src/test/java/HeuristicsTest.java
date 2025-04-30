import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class HeuristicsTest {

    @Test
    public void amountOfOwnTilesTest1(){
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

        assertEquals(0.5, Heuristics.amountOfOwnTiles(map, state));
    }

    @Test
    public void amountOfOwnTilesTest2(){
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

        assertEquals(0.1, Heuristics.amountOfOwnTiles(map, state));
    }

    @Test
    public void amountOfOwnTilesTest3(){
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

        assertEquals(6.0/64, Heuristics.amountOfOwnTiles(map, state));
    }

}
