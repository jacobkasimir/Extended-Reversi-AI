import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

import algorithms.datastructures.IntLinkedList;
import algorithms.strategies.Heuristics;
import control.Coder;
import gamestate.GameState;
import gamestate.attributes.GameAttributes;
import gamestate.map.MapController;
import gamestate.map.Transitions;


public class HeuristicsTest {

    @Test
    public void mapRelatedHeuristicsPhase1Test1(){
        
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

        GameAttributes attributes = new GameAttributes( 2, 1, 1, 1, (byte) 1);
        attributes.setOurPlayerId((1));

        Heuristics heuristics = new Heuristics(new GameState(map, attributes), 1, 1, 2);
        double values[] = heuristics.mapRelatedHeuristicsPhase1(map, (byte) 1);

        assertEquals(0.5, values[0]);

        assertEquals(1.0, values[1]);

        assertEquals(1.0 - (48 / ((double) 12*32)), values[2]);
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

        GameAttributes attributes = new GameAttributes((byte)8, 1, 1, 1, (byte) 1);
        attributes.setOurPlayerId((byte) 6);

        Heuristics heuristics = new Heuristics(new GameState(map, attributes), 1, 1, 8);
        double values[] = heuristics.mapRelatedHeuristicsPhase1(map, (byte) 6);

        assertEquals(8/64.0, values[0]);

        assertEquals(8/56.0, values[1]);

        assertEquals(1.0 - (14 / ((double) 12*8)), values[2]);
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

        GameAttributes attributes = new GameAttributes(8, 1, 1, 1, (byte) 1);
        attributes.setOurPlayerId((byte) 4);

        Heuristics heuristics = new Heuristics(new GameState(map, attributes), 1, 1, 8);
        double values[] = heuristics.mapRelatedHeuristicsPhase1(map, (byte) 4);

        assertEquals(6/56.0, values[0]);

        assertEquals(6/46.0, values[1]);

        assertEquals(1.0 - (0 / ((double) 12*6)), values[2]);
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

        GameAttributes attributes = new GameAttributes(4, 1, 1, 1, (byte) 1);
        attributes.setOurPlayerId((byte)1);

        Heuristics heuristics = new Heuristics(new GameState(map, attributes), 1, 1, 4);

        assertEquals(0.0, heuristics.ratioOfCornersCaptured(map, 1));

        assertEquals(0.5, heuristics.ratioOfCornersCaptured(mapOccupied, 1));

    }

    @Test
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

        GameAttributes attributes = new GameAttributes(4, 1, 1, 1, (byte) 1);
        attributes.setOurPlayerId((byte)1);

        Heuristics heuristics = new Heuristics(new GameState(map, attributes), 1, 1, 4);

        assertEquals(0.0, heuristics.ratioOfCornersCaptured(map, 1));

        assertEquals(3 / 4.0 , heuristics.ratioOfCornersCaptured(mapOccupied, 1));
    }

    @Test
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

        GameAttributes attributes = new GameAttributes(4, 1, 1, 1, (byte) 1);
        attributes.setOurPlayerId((byte)1);

        Heuristics heuristics = new Heuristics(new GameState(map, attributes), 1, 1, 4);

        assertEquals(1 / 5.0, heuristics.ratioOfCornersCaptured(map, 1));

        assertEquals(3 / 5.0, heuristics.ratioOfCornersCaptured(mapOccupied, 1));

    }

     @Test
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

        GameAttributes attributes = new GameAttributes(4, 1, 1, 1, (byte) 1);
        attributes.setOurPlayerId((byte)1);

        Heuristics heuristics = new Heuristics(new GameState(map, attributes), 1, 1, 4);

        assertEquals(2 / 5.0, heuristics.ratioOfCornersCaptured(map, 1));

        assertEquals(1 / 5.0, heuristics.ratioOfCornersCaptured(mapOccupied, 1));
    }

    @Test
    public void findCornersTest(){
        IntLinkedList result = new IntLinkedList();
        result.add(Coder.encodeCoordinate(0, 0));
        result.add(Coder.encodeCoordinate(0, 7));
        result.add(Coder.encodeCoordinate(7, 0));
        result.add(Coder.encodeCoordinate(7, 7));

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
        
        GameAttributes attributes = new GameAttributes(4, 1, 1, 1, (byte) 1);
        attributes.setOurPlayerId((byte)1);

        Heuristics heuristics = new Heuristics(new GameState(map, attributes), 1, 1, 4);

        assertEquals(4, heuristics.findCorners(map).getSize());
        assertEquals(result, heuristics.findCorners(map));
    } 

    @Test
    public void numberOfBombsTest(){
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

        GameAttributes attributes = new GameAttributes(4, 3, 2, 2, (byte) 1);
        attributes.getPlayer(1).setNumberBombs(1);

        Heuristics heuristics = new Heuristics(new GameState(map, attributes), 2, 1, 4);

        assertEquals(1, heuristics.ratioBombs(attributes, 2));
        assertEquals(1 / 2.0, heuristics.ratioBombs(attributes, 1));
    }

    @Test
    public void numberOfOverridesTest(){
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

        GameAttributes attributes = new GameAttributes(4, 3, 2, 2, (byte) 1);
        attributes.getPlayer(1).setNumberOverrides(1);

        Heuristics heuristics = new Heuristics(new GameState(map, attributes), 1, 3, 4);

        assertEquals(1.0, heuristics.ratioOverrides(attributes,2));
        assertEquals(1 / 3.0, heuristics.ratioOverrides(attributes,1));
    }
}
