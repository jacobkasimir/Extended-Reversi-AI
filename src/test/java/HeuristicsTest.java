import java.util.ArrayList;

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
            new Transitions(8,8),
            8, 8, 1);

        GameAttributes state = new GameAttributes(2, 1, 1, 1, (byte) 1);
        state.setOurPlayerId((byte) 1);

        assertEquals(0.5, Heuristics.amountOfOwnTilesOverall(map, (byte) 1));
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
            new Transitions(8,8),
            8, 8, 1);

        GameAttributes state = new GameAttributes(8, 1, 1, 1, (byte) 1);
        state.setOurPlayerId((byte) 6);

        assertEquals(1.0/8, Heuristics.amountOfOwnTilesOverall(map, (byte) 6));
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
            new Transitions(8,8),
            8, 8, 1);

        GameAttributes state = new GameAttributes(8, 1, 1, 1, (byte) 1);
        state.setOurPlayerId((byte) 4);

        assertEquals(6.0/64, Heuristics.amountOfOwnTilesOverall(map, (byte) 4));
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
            new Transitions(8,8),
            8, 8, 1);

        GameAttributes state = new GameAttributes(8, 1, 1, 1, (byte) 1);
        state.setOurPlayerId((byte) 6);

        assertEquals(8.0/56, Heuristics.amountOfOwnTilesToOthers(map, (byte) 6));
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
            new Transitions(8,8),
            8, 8, 1);

        GameAttributes state = new GameAttributes(2, 1, 1, 1, (byte) 1);
        state.setOurPlayerId((byte) 1);

        assertEquals(1.0, Heuristics.amountOfOwnTilesToOthers(map, (byte) 1));
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
            new Transitions(8,8),
            8, 8, 1);

        GameAttributes state = new GameAttributes(8, 1, 1, 1, (byte) 1);
        state.setOurPlayerId((byte) 4);

        assertEquals(6.0/40, Heuristics.amountOfOwnTilesToOthers(map, (byte) 4));
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
            new Transitions(8,8),
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
            new Transitions(8,8),
            8, 8, 1);

        GameAttributes state = new GameAttributes(4, 1, 1, 1, (byte) 1);
        state.setOurPlayerId((byte)1);
        Heuristics heuristics = new Heuristics(map);

        double result = heuristics.amountOfCornersCaptured(mapOccupied, (byte) 1);

        assertEquals(result, 0.5);

    }

    public void amountOfCornersCapturedTest2(){
        MapController map = new MapController(
            new String[]{
                "0 0 0 0 0 0 0 0",
                "0 0 0 0 0 0 0 0", 
                "0 0 0 0 0 0 0 0", 
                "0 0 0 0 0 0 0 0", 
                "0 0 0 0 0 0 0 0", 
                "0 0 0 - 0 - 0 0", 
                "0 0 0 0 0 0 0 0", 
                "1 0 0 0 0 0 0 1"
            }, 
            new Transitions(8,8),
            8, 8, 1);

        MapController mapOccupied = new MapController(
            new String[]{
                "1 0 0 1 0 0 0 0",
                "0 0 0 0 0 0 0 0", 
                "0 0 0 0 0 0 0 0", 
                "0 0 0 0 0 0 0 0", 
                "0 0 0 0 0 0 0 0", 
                "0 1 0 - 1 - 0 0", 
                "0 0 0 0 0 0 0 0", 
                "1 0 0 0 0 0 0 1"
            }, 
            new Transitions(8,8),
            8, 8, 1);

        GameAttributes state = new GameAttributes(4, 1, 1, 1, (byte) 1);
        state.setOurPlayerId((byte)1);
        Heuristics heuristics = new Heuristics(map);

        double result = heuristics.amountOfCornersCaptured(mapOccupied, (byte) 1);

        assertEquals(result, 0.75);

    }

    public void amountOfCornersCapturedTest3(){
        MapController map = new MapController(
            new String[]{
                "2 0 0 0 0 0 0 0",
                "0 0 0 0 0 0 0 0", 
                "0 0 0 0 0 0 4 0", 
                "0 0 0 - - 0 0 0", 
                "0 0 0 0 - 0 0 0", 
                "0 0 0 0 - 0 0 0", 
                "0 0 0 0 0 0 0 0", 
                "0 0 0 0 0 0 0 1"
            }, 
            new Transitions(8,8),
            8, 8, 1);

        MapController mapOccupied = new MapController(
            new String[]{
                "1 0 0 0 0 0 0 0",
                "0 0 0 0 0 0 0 0", 
                "0 0 0 0 0 0 0 0", 
                "0 0 0 - - 0 0 0", 
                "0 0 0 1 - 0 0 0", 
                "0 0 0 0 - 0 0 0", 
                "0 0 0 0 0 0 0 0", 
                "0 0 0 0 0 0 0 1"
            }, 
            new Transitions(8,8),
            8, 8, 1);

        GameAttributes state = new GameAttributes(4, 1, 1, 1, (byte) 1);
        state.setOurPlayerId((byte)1);
        Heuristics heuristics = new Heuristics(map);

        double result = heuristics.amountOfCornersCaptured(mapOccupied, (byte) 1);

        assertEquals(result, 3.0 / 5);

    }

    public void amountOfCornersCapturedTest4(){
        MapController map = new MapController(
            new String[]{
                "1 0 0 0 0 0 0 4",
                "0 0 0 0 0 0 0 0", 
                "0 0 0 0 0 0 0 0", 
                "0 0 0 - - 0 0 0", 
                "0 0 - 3 0 0 0 0", 
                "0 0 - 0 - 0 0 0", 
                "0 0 0 0 0 0 0 0", 
                "3 0 0 0 0 0 0 1"
            }, 
            new Transitions(8,8),
            8, 8, 1);

        MapController mapOccupied = new MapController(
            new String[]{
                "0 0 0 0 0 0 0 4",
                "0 0 0 0 0 0 0 0", 
                "0 0 0 0 0 0 0 0", 
                "0 0 0 - - 0 0 0", 
                "0 0 - 1 0 0 0 0", 
                "0 0 - 0 - 0 0 0", 
                "0 0 0 0 0 0 0 0", 
                "3 0 0 0 0 0 0 2"
            }, 
            new Transitions(8,8),
            8, 8, 1);

        GameAttributes state = new GameAttributes(4, 1, 1, 1, (byte) 1);
        state.setOurPlayerId((byte)1);
        Heuristics heuristics = new Heuristics(map);

        double result = heuristics.amountOfCornersCaptured(mapOccupied, (byte) 1);

        assertEquals(result, 1 / 5.0);

    }

    @Test
    public void findCornersTest(){
        IntLinkedList result = new IntLinkedList();
        result.add(MoveVerifier.encodeCoordinate(0, 0));
        result.add(MoveVerifier.encodeCoordinate(0, 7));
        result.add(MoveVerifier.encodeCoordinate(7, 0));
        result.add(MoveVerifier.encodeCoordinate(7, 7));

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
            new Transitions(8,8),
            8, 8, 1);

        Heuristics heuristics = new Heuristics(map);

        assertEquals(4, heuristics.findCorners(map).getSize());
        assertEquals(result, heuristics.findCorners(map));
    } 

    @Test
    public void numberOfBombsTest(){
        GameAttributes state = new GameAttributes(4, 3, 2, 2, (byte) 1);
        state.getPlayer(1).setNumberBombs(3);

        assertEquals(2, Heuristics.numberOfBombs(state, (byte) 2));
        assertEquals(3, Heuristics.numberOfBombs(state, (byte) 1));
    }

    @Test
    public void numberOfOverridesTest(){
        GameAttributes state = new GameAttributes(4, 3, 2, 2, (byte) 1);
        state.getPlayer(1).setNumberOverwrites(1);

        assertEquals(3, Heuristics.numberOfOverrides(state, (byte) 2));
        assertEquals(1, Heuristics.numberOfOverrides(state, (byte) 1));
    }
}
