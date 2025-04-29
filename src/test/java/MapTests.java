import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

public class MapTests {

    MapController mapController = new MapController(new String[]{"0 0 0 0 0 0 0 0", "0 0 0 0 0 0 0 0", "0 0 0 0 0 0 0 0", "0 0 0 1 2 0 0 0",
        "0 0 0 2 1 0 0 0","0 0 0 0 0 0 0 0", "0 0 0 0 0 0 0 b", "0 0 0 0 0 0 0 0"}, 
        new String[]{"7 7 2 <-> 0 0 6", "7 0 1 <-> 0 7 5"}, 8, 8, 0);
    
    MapController mapController2 = new MapController(new String[]{"0 0 b 0 0 0 0 0", "0 0 0 0 0 0 0 0", "0 0 0 0 0 0 0 0", "0 0 0 1 2 0 0 0",
        "0 0 0 2 1 0 0 0","0 0 0 0 0 0 0 0", "0 0 0 0 0 0 0 0"}, 
        new String[]{}, 8, 7, 0);
    
    @Test 
    public void mapInitTest(){
        assertEquals(mapController.getTile(3, 3), '1');
        assertEquals(mapController.getTile(2, 2), '0');
        assertEquals(mapController.getTile(7, 6), 'b');
        assertEquals(mapController2.getTile(2, 0), 'b');
    }

    @Test 
    public void nextTileTest(){
        assertEquals(mapController.getNextTile(new Position(7, 7, 2)), new Position(0, 0, 2));
        assertEquals(mapController.getNextTile(new Position(0, 7, 5)), new Position(7, 0, 5));
        assertEquals(mapController.getNextTile(new Position(0, 0, 3)), new Position(1, 1, 3));
    }
    


    //UPDATE MAP TESTS
    @Test 
    public void basicCapturingTest(){
        //checks for basic capturing
        MapController basicTestController = new MapController(
            new String[]{
                "0 0 0 0 0 0 0 0", 
                "0 0 0 0 0 0 0 0", 
                "0 0 0 0 0 0 0 0", 
                "0 0 0 1 2 0 0 0",
                "0 0 0 2 1 0 0 0",
                "0 0 0 0 0 0 0 0", 
                "0 0 0 0 0 0 0 0", 
                "0 0 0 0 0 0 0 0"
            }, 
            new String[]{}, 
            8, 8, 0);

        basicTestController.updateMap(new Move(2, 4, 0, 1), true, 
        new GameState(2,0,0,0,(byte)1));

        assertEquals(basicTestController, new MapController(
            new String[]{
                "0 0 0 0 0 0 0 0", 
                "0 0 0 0 0 0 0 0", 
                "0 0 0 0 0 0 0 0", 
                "0 0 0 1 2 0 0 0",
                "0 0 1 1 1 0 0 0",
                "0 0 0 0 0 0 0 0", 
                "0 0 0 0 0 0 0 0", 
                "0 0 0 0 0 0 0 0"
            }, 
            new String[]{}, 
            8, 8, 0));
    }

    @Test 
    public void transitionCapturingTest(){
        //checks for basic capturing over a transition
        MapController transitionTestController = new MapController(
            new String[]{
                "2 1 0 0 0 0 0 2", 
                "0 0 0 0 0 0 0 0", 
                "0 0 0 0 0 0 0 0", 
                "0 0 0 0 0 0 0 0",
                "0 0 0 0 0 0 0 0",
                "0 0 0 0 0 0 0 0", 
                "0 0 0 0 0 0 0 0", 
                "0 0 0 0 0 0 0 0"
            }, 
            new String[]{"0 0 6 <-> 7 0 2"}, 
            8, 8, 0);

            transitionTestController.updateMap(new Move(6, 0, 0, 1), true, new GameState(2,0,0,0,(byte)1));

            assertEquals(transitionTestController, new MapController(
                new String[]{
                    "1 1 0 0 0 0 1 1", 
                    "0 0 0 0 0 0 0 0", 
                    "0 0 0 0 0 0 0 0", 
                    "0 0 0 0 0 0 0 0",
                    "0 0 0 0 0 0 0 0",
                    "0 0 0 0 0 0 0 0", 
                    "0 0 0 0 0 0 0 0", 
                    "0 0 0 0 0 0 0 0"
                }, 
                new String[]{"0 0 6 <-> 7 0 2"}, 
                8, 8, 0));
    } 

    @Test 
    public void choiceTileTest(){
        //checks for correct choice tile handling
        MapController choiceTestController = new MapController(
            new String[]{
                "0 0 0 0 0 0 0 0", 
                "0 0 0 0 0 0 0 0", 
                "0 0 0 2 0 0 0 0", 
                "0 0 0 1 2 c 0 0",
                "0 0 0 2 1 0 0 0",
                "0 0 0 2 0 0 0 0", 
                "0 0 0 0 0 0 0 0", 
                "0 0 0 0 0 0 0 0"
            }, 
            new String[]{}, 
            8, 8, 0);

            choiceTestController.updateMap(new Move(5, 3, 2, 1), true,
            new GameState(2,0,0,0,(byte)1));

            assertEquals(choiceTestController, new MapController(
                new String[]{
                    "0 0 0 0 0 0 0 0", 
                    "0 0 0 0 0 0 0 0", 
                    "0 0 0 1 0 0 0 0", 
                    "0 0 0 2 2 2 0 0",
                    "0 0 0 1 2 0 0 0",
                    "0 0 0 1 0 0 0 0", 
                    "0 0 0 0 0 0 0 0", 
                    "0 0 0 0 0 0 0 0"
                }, 
                new String[]{}, 
                8, 8, 0));
    } 
    
    @Test 
    public void inversionTileTest(){
        //checks for correct inversion tile handling
        MapController inversionTestController = new MapController(
            new String[]{
                "0 0 0 0 0 0 0 0", 
                "0 0 0 0 0 0 0 0", 
                "0 0 0 2 3 0 0 0", 
                "0 0 3 1 2 i 0 0",
                "0 0 0 2 1 3 0 0",
                "0 0 3 2 0 1 0 0", 
                "0 0 0 0 0 0 0 0", 
                "0 0 0 0 0 0 0 0"
            }, 
            new String[]{}, 
            8, 8, 0);

            inversionTestController.updateMap(new Move(5, 3, 0, 1), true, 
            new GameState(3,0,0,0,(byte)1));

            assertEquals(inversionTestController, new MapController(
                new String[]{
                    "0 0 0 0 0 0 0 0", 
                    "0 0 0 0 0 0 0 0", 
                    "0 0 0 3 1 0 0 0", 
                    "0 0 1 2 2 2 0 0",
                    "0 0 0 3 2 2 0 0",
                    "0 0 1 3 0 2 0 0", 
                    "0 0 0 0 0 0 0 0", 
                    "0 0 0 0 0 0 0 0"
                }, 
                new String[]{}, 
                8, 8, 0));
    }
 
    
    @Test
    public void overrideTileTest(){
        //checks for correct override stone handling
        MapController overrideTileController = new MapController(
            new String[]{
                "0 0 0 0 0 0 0 0", 
                "0 0 0 0 0 0 0 0", 
                "0 0 0 0 0 0 0 0", 
                "0 0 0 1 2 0 0 0",
                "0 0 0 2 1 0 0 0",
                "0 0 0 0 0 0 0 0", 
                "0 0 0 0 0 0 0 0", 
                "0 0 0 0 0 0 0 0"
            }, 
            new String[]{},
            8, 8, 0);

            overrideTileController.updateMap(new Move(3, 3, 0, 2), true, 
            new GameState(2,1,0,0,(byte)1));

            assertEquals(overrideTileController, new MapController(
                new String[]{
                    "0 0 0 0 0 0 0 0", 
                    "0 0 0 0 0 0 0 0", 
                    "0 0 0 0 0 0 0 0", 
                    "0 0 0 2 2 0 0 0",
                    "0 0 0 2 1 0 0 0",
                    "0 0 0 0 0 0 0 0", 
                    "0 0 0 0 0 0 0 0", 
                    "0 0 0 0 0 0 0 0"
                }, 
                new String[]{}, 
                8, 8, 0));
    }
 
    
    @Test
    public void expansionTileTest(){
        //checks for correct expansion stone handling
        MapController expansionTileController = new MapController(
            new String[]{
                "0 0 0 0 0 0 0 0", 
                "0 0 0 0 0 0 0 0", 
                "0 0 0 0 0 0 0 0", 
                "0 0 0 1 2 2 0 0",
                "0 0 0 2 1 2 x 0",
                "0 0 0 0 1 0 2 0", 
                "0 0 0 0 0 0 1 0", 
                "0 0 0 0 0 0 0 0"
            }, 
            new String[]{}, 
            8, 8, 0);                                                                                                                                               

            expansionTileController.updateMap(new Move(6, 3, 0, 1), true, 
            new GameState(2,0,0,0,(byte)1));

            assertEquals(expansionTileController, new MapController(
                new String[]{
                    "0 0 0 0 0 0 0 0", 
                    "0 0 0 0 0 0 0 0", 
                    "0 0 0 0 0 0 0 0", 
                    "0 0 0 1 1 1 1 0",
                    "0 0 0 2 1 1 x 0",
                    "0 0 0 0 1 0 1 0", 
                    "0 0 0 0 0 0 1 0", 
                    "0 0 0 0 0 0 0 0"
                }, 
                new String[]{}, 
                8, 8, 0));
    }
 
    @Test
    public void bombTest(){
        //checks for correct bomb handling
        MapController bombController = new MapController(
            new String[]{
                "0 0 0 0 0 0 0 0", 
                "0 0 0 0 0 0 0 0", 
                "0 0 0 0 0 0 0 0", 
                "0 0 0 1 2 0 0 0",
                "0 0 2 2 1 0 0 0",
                "0 0 1 2 0 1 0 0", 
                "0 0 2 2 1 2 0 0", 
                "0 0 0 0 0 0 0 0"
            }, 
            new String[]{},
            8, 8, 1);

            bombController.updateMap(new Move(3, 4, 0, 1), false, 
            new GameState(2,0,2,1,(byte)2));

            assertEquals(bombController, new MapController(
                new String[]{
                    "0 0 0 0 0 0 0 0", 
                    "0 0 0 0 0 0 0 0", 
                    "0 0 0 0 0 0 0 0", 
                    "0 0 - - - 0 0 0",
                    "0 0 - - - 0 0 0",
                    "0 0 - - - 1 0 0", 
                    "0 0 2 2 1 2 0 0", 
                    "0 0 0 0 0 0 0 0"
                }, 
                new String[]{}, 
                8, 8, 1));
    }

    @Test
    public void bombTestWithBorder(){
        //checks for correct bomb handling
        MapController bombController = new MapController(
            new String[]{
                "0 0 0 0 0 0 0 0", 
                "0 0 0 0 0 0 0 0", 
                "0 0 0 0 0 0 0 0", 
                "0 0 0 1 2 0 0 0",
                "0 0 2 2 1 0 0 0",
                "0 0 1 2 0 1 0 0", 
                "0 0 2 2 1 2 0 0", 
                "0 0 0 0 0 0 0 0"
            }, 
            new String[]{},
            8, 8, 3);

            bombController.updateMap(new Move(2, 5, 0, 1), false, 
            new GameState(2,0,2,3,(byte)2));

            assertEquals(bombController, new MapController(
                new String[]{
                    "0 0 0 0 0 0 0 0", 
                    "0 0 0 0 0 0 0 0", 
                    "- - - - - - 0 0", 
                    "- - - - - - 0 0",
                    "- - - - - - 0 0",
                    "- - - - - - 0 0", 
                    "- - - - - - 0 0", 
                    "- - - - - - 0 0"
                }, 
                new String[]{}, 
                8, 8, 3));
    }
    
    @Test
    public void bombTestWithBorderAndTransition(){
        //checks for correct bomb handling
        MapController bombController = new MapController(
            new String[]{
                "0 0 0 0 0 0 0 0", 
                "0 0 0 0 0 0 0 0", 
                "0 0 0 0 0 0 0 0", 
                "0 0 0 1 2 0 0 0",
                "0 0 2 2 1 0 0 0",
                "0 0 1 2 0 1 0 0", 
                "0 0 2 2 1 2 0 0", 
                "0 0 0 0 0 0 0 0"
            }, 
            new String[]{
                "0 7 5 <-> 7 0 1"
            },
            8, 8, 3);

            bombController.updateMap(new Move(2, 5, 0, 1), false, 
            new GameState(2,0,2,3,(byte)2));

            assertEquals(bombController, new MapController(
                new String[]{
                    "0 0 0 0 0 0 0 -", 
                    "0 0 0 0 0 0 0 0", 
                    "- - - - - - 0 0", 
                    "- - - - - - 0 0",
                    "- - - - - - 0 0",
                    "- - - - - - 0 0", 
                    "- - - - - - 0 0", 
                    "- - - - - - 0 0"
                }, 
                new String[]{
                    "0 7 5 <-> 7 0 1"
                }, 
                8, 8, 3));
    }

    @Test
    public void overlappingTest(){
        //checks for overlapping capturing
        MapController overlappingController = new MapController(
            new String[]{
                "0 0 0 0 0 0 1 0", 
                "0 0 0 0 0 0 1 0", 
                "0 2 0 0 0 0 1 0", 
                "0 0 1 0 0 0 1 0",
                "0 0 2 1 1 1 1 1",
                "0 0 0 0 1 0 1 0", 
                "0 0 0 0 0 1 1 0", 
                "0 0 0 0 0 0 0 0"
            }, 
            new String[]{
                "6 0 0 <-> 7 4 2"
            }, 
            8, 8, 0);

            overlappingController.updateMap(new Move(6, 7, 0, 2), true, 
            new GameState(2,0,0,0,(byte)2));

            for (int i = 0; i < 8; i++) {
                for (int j = 0; j < 8; j++) {
                    System.err.print(overlappingController.getTile(i, j));
                }
                System.err.println("");
            }

            assertEquals(overlappingController, new MapController(
                new String[]{
                    "0 0 0 0 0 0 2 0", 
                    "0 0 0 0 0 0 2 0", 
                    "0 2 0 0 0 0 2 0", 
                    "0 0 2 0 0 0 2 0",
                    "0 0 2 2 2 2 2 2",
                    "0 0 0 0 2 0 2 0", 
                    "0 0 0 0 0 2 2 0", 
                    "0 0 0 0 0 0 2 0"
                }, 
                new String[]{
                    "6 0 0 <-> 7 4 2"
                },  
                8, 8, 0));
    }
 
    
    @Test
    public void allDirectionsCapturingOverlappingTest(){
        //checks for complex capturing in all directions with overlapping
        MapController allDirectionsController = new MapController(
            new String[]{
                "0 1 0 0 0 0 1 0", 
                "0 1 2 0 2 0 1 0", 
                "0 1 0 1 1 1 1 0", 
                "1 1 1 1 0 1 1 1",
                "0 2 0 1 1 1 1 0",
                "0 0 0 0 1 0 2 0", 
                "0 0 0 0 2 0 0 0", 
                "0 0 0 0 0 0 0 0"
            }, 
            new String[]{
                "0 3 6 <-> 1 0 0", 
                "6 0 0 <-> 7 3 2"
            }, 
            8, 8, 0);

            allDirectionsController.updateMap(new Move(4, 3, 0, 2), true, 
            new GameState(2,0,0,0,(byte)1));

            assertEquals(allDirectionsController, new MapController(
                new String[]{
                    "0 2 0 0 0 0 2 0", 
                    "0 2 2 0 2 0 2 0", 
                    "0 2 0 2 2 1 2 0", 
                    "2 2 2 2 2 2 2 2",
                    "0 2 0 1 2 2 2 0",
                    "0 0 0 0 2 0 2 0", 
                    "0 0 0 0 2 0 0 0", 
                    "0 0 0 0 0 0 0 0"
                }, 
                new String[]{
                    "0 3 6 <-> 1 0 0", 
                    "6 0 0 <-> 7 3 2"
                }, 
                8, 8, 0));
    }

}

