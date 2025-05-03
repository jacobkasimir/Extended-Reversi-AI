import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;

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

    @Test
    public void amountOfCornersCapturedTest1(){
        MapController map = new MapController(
            new String[]{
                "0 0 0 0 0 0 0 0",
                "0 0 0 0 0 0 0 0", 
                "0 0 0 0 0 0 0 0", 
                "0 0 0 0 0 0 0 0", 
                "0 0 0 0 0 0 0 0", 
                "0 0 0 0 0 0 0 0", 
                "0 0 0 0 0 0 0 0", 
                "0 0 0 0 0 0 0 0"
            }, 
            new String[]{},
            8, 8, 1);

        MapController mapOccupied = new MapController(
            new String[]{
                "1 0 0 0 0 0 0 0",
                "0 0 0 0 0 0 0 0", 
                "0 0 0 0 0 0 0 0", 
                "0 0 0 0 0 0 0 0", 
                "0 0 0 0 0 0 0 0", 
                "0 0 0 0 0 0 0 0", 
                "0 0 0 0 0 0 0 0", 
                "0 0 0 0 0 0 0 1"
            }, 
            new String[]{},
            8, 8, 1);

        GameState state = new GameState(4, 1, 1, 1, (byte) 1);
        state.setOurPlayerId((byte)1);
        Heuristics heuristics = new Heuristics(map);

        double result = heuristics.amountOfCornersCaptured(mapOccupied, state);

        assertEquals(result, 0.5);

    }

    @Test
    public void findCornersTest(){
        ArrayList<Coordinate> result = new ArrayList<>();
        result.add(new Coordinate(0, 0));
        result.add(new Coordinate(0, 7));
        result.add(new Coordinate(7, 0));
        result.add(new Coordinate(7, 7));

        MapController map = new MapController(
            new String[]{
                "0 0 0 0 0 0 0 0",
                "0 0 0 0 0 0 0 0", 
                "0 0 0 0 0 0 0 0", 
                "0 0 0 0 0 0 0 0", 
                "0 0 0 0 0 0 0 0", 
                "0 0 0 0 0 0 0 0", 
                "0 0 0 0 0 0 0 0", 
                "0 0 0 0 0 0 0 0"
            }, 
            new String[]{},
            8, 8, 1);

        Heuristics heuristics = new Heuristics(map);

        assertEquals(4, heuristics.findCorners(map).size());
        assertEquals(result, heuristics.findCorners(map));
    } 

    @Test
    public void testForDebugging(){
        MapController map = new MapController(
            new String[]{
                "0 0 0 0 0 0 0 0",
                "0 0 0 0 0 0 0 0", 
                "0 0 0 0 0 0 0 0", 
                "0 0 0 0 0 0 0 0", 
                "0 0 0 0 0 0 0 0", 
                "0 0 0 0 0 0 0 0", 
                "0 0 0 0 0 0 0 0", 
                "0 0 0 0 0 0 0 0"
            }, 
            new String[]{},
            8, 8, 1);

        assertEquals(map.getNextTile(new Position(0, 0, 0)), null);
        assertEquals(map.getNextTile(new Position(0, 0, 1)), null);
        assertEquals(map.getNextTile(new Position(0, 0, 6)), null);
        assertEquals(map.getNextTile(new Position(0, 0, 7)), null);
    }
}
