import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

public class MapTests {

    MapController mapController = new MapController(new String[]{"0 0 0 0 0 0 0 0", "0 0 0 0 0 0 0 0", "0 0 0 0 0 0 0 0", "0 0 0 1 2 0 0 0",
        "0 0 0 2 1 0 0 0","0 0 0 0 0 0 0 0", "0 0 0 0 0 0 0 b", "0 0 0 0 0 0 0 0"}, 
        new String[]{"7 7 2 <-> 0 0 6", "7 0 1 <-> 0 7 5"}, 8, 8, 0);
    
    @Test 
    public void mapInitTest(){
        assertEquals(mapController.getTile(3, 3), '1');
        assertEquals(mapController.getTile(2, 2), '0');
        assertEquals(mapController.getTile(7, 6), 'b');
    }

    @Test 
    public void nextTileTest(){
        assertEquals(mapController.getNextTile(new Position(7, 7, 2)), new Position(0, 0, 2));
        assertEquals(mapController.getNextTile(new Position(0, 7, 5)), new Position(7, 0, 5));
        assertEquals(mapController.getNextTile(new Position(0, 0, 3)), new Position(1, 1, 3));
    }
    
}

