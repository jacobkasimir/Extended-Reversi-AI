import control.Coder;
import control.GameClient;
import gamestate.GameState;
import gamestate.attributes.GameAttributes;
import gamestate.map.MapController;
import gamestate.map.Transitions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import validation.MoveVerifier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MapTests {
    MapController mapController;
    MapController mapController2;
    GameClient gameClient;

    @BeforeEach
    public void init() {
        gameClient = new GameClient();
        gameClient.initTransitions(new String[]{"7 7 2 <-> 0 0 6", "7 0 1 <-> 0 7 5"}, 8, 8);

        mapController = new MapController(
                new String[]{"0 0 0 0 0 0 0 0", "0 0 0 0 0 0 0 0", "0 0 0 0 0 0 0 0", "0 0 0 1 2 0 0 0",
                        "0 0 0 2 1 0 0 0", "0 0 0 0 0 0 0 0", "0 0 0 0 0 0 0 b", "0 0 0 0 0 0 0 0"},
                gameClient.getTransitions(), 8, 8, 0);

        mapController2 = new MapController(
                new String[]{"0 0 b 0 0 0 0 0", "0 0 0 0 0 0 0 0", "0 0 0 0 0 0 0 0", "0 0 0 1 2 0 0 0",
                        "0 0 0 2 1 0 0 0", "0 0 0 0 0 0 0 0", "0 0 0 0 0 0 0 0"},
                new Transitions(8, 8), 8, 7, 0);
    }

    @Test
    public void mapInitTest() {
        assertEquals(mapController.getTile(3, 3), '1');
        assertEquals(mapController.getTile(2, 2), '0');
        assertEquals(mapController.getTile(7, 6), 'b');
        assertEquals(mapController2.getTile(2, 0), 'b');
    }

    @Test
    public void nextTileTest() {
        assertEquals(mapController.getNextTile(Coder.encodePosition(7, 7, 2)), Coder.encodePosition(0, 0, 2));
        assertEquals(mapController.getNextTile(Coder.encodePosition(0, 7, 5)), Coder.encodePosition(7, 0, 5));
        assertEquals(mapController.getNextTile(Coder.encodePosition(0, 0, 3)), Coder.encodePosition(1, 1, 3));
    }

    @Test
    public void basicCapturingTest() {
        // checks for basic capturing

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
                new Transitions(8, 8),
                8, 8, 0);

        basicTestController.updateMap(Coder.encodeMove(2, 4, 0, 1),
                new GameAttributes(2, 0, 0, 0, (byte) 1));

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
                new Transitions(8, 8),
                8, 8, 0));
    }

    @Test
    public void transitionCapturingTest() {
        // checks for basic capturing over a transition
        GameClient client = new GameClient();
        client.initTransitions(new String[]{"0 0 6 <-> 7 0 2"}, 8, 8);

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
                client.getTransitions(),
                8, 8, 0);

        transitionTestController.updateMap(Coder.encodeMove(6, 0, 0, 1),
                new GameAttributes(2, 0, 0, 0, (byte) 1));

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
                client.getTransitions(),
                8, 8, 0));
    }

    @Test
    public void choiceTileTest() {
        // checks for correct choice tile handling
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
                new Transitions(8, 8),
                8, 8, 0);

        GameState gameState = new GameState(choiceTestController, new GameAttributes(2, 2, 0, 0, 1));
        assertTrue(MoveVerifier.isValid(Coder.encodeMove(5, 3, 2, 1), gameState));

        choiceTestController.updateMap(Coder.encodeMove(5, 3, 2, 1),
                new GameAttributes(2, 0, 0, 0, (byte) 1));

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
                new Transitions(8, 8),
                8, 8, 0));
    }

    @Test
    public void choiceHandlingTest() {
        // checks for correct choice tile handling
        MapController choiceTestController = new MapController(
                new String[]{
                        "0 0 0 0 0 0 0 0",
                        "0 0 0 0 0 0 0 0",
                        "0 0 0 2 0 0 0 0",
                        "0 0 0 1 1 1 0 0",
                        "0 0 0 2 1 0 0 0",
                        "0 0 0 2 0 0 0 0",
                        "0 0 0 0 0 0 0 0",
                        "0 0 0 0 0 0 0 0"
                },
                new Transitions(8, 8),
                8, 8, 0);

        choiceTestController.choiceHandling(Coder.encodeMove(5, 3, 2, 1));

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
                new Transitions(8, 8),
                8, 8, 0));
    }

    @Test
    public void inversionTileTest() {
        // checks for correct inversion tile handling
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
                new Transitions(8, 8),
                8, 8, 0);

        inversionTestController.updateMap(Coder.encodeMove(5, 3, 0, 1),
                new GameAttributes(3, 0, 0, 0, (byte) 1));

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
                new Transitions(8, 8),
                8, 8, 0));
    }

    @Test
    public void overrideTileTest() {
        // checks for correct override stone handling
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
                new Transitions(8, 8),
                8, 8, 0);

        overrideTileController.updateMap(Coder.encodeMove(3, 3, 0, 2),
                new GameAttributes(2, 1, 0, 0, (byte) 1));

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
                new Transitions(8, 8),
                8, 8, 0));
    }

    @Test
    public void expansionTileTest() {
        // checks for correct expansion stone handling
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
                new Transitions(8, 8),
                8, 8, 0);

        expansionTileController.updateMap(Coder.encodeMove(6, 3, 0, 1),
                new GameAttributes(2, 0, 0, 0, (byte) 1));

        assertEquals(expansionTileController, new MapController(
                new String[]{
                        "0 0 0 0 0 0 0 0",
                        "0 0 0 0 0 0 0 0",
                        "0 0 0 0 0 0 0 0",
                        "0 0 0 1 1 1 1 0",
                        "0 0 0 2 1 1 1 0",
                        "0 0 0 0 1 0 1 0",
                        "0 0 0 0 0 0 1 0",
                        "0 0 0 0 0 0 0 0"
                },
                new Transitions(8, 8),
                8, 8, 0));
    }

    @Test
    public void bombTest() {
        // checks for correct bomb handling
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
                new Transitions(8, 8),
                8, 8, 1);

        bombController.updateMap(Coder.encodeMove(3, 4, 0, 1),
                new GameAttributes(2, 0, 2, 1, (byte) 2));

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
                new Transitions(8, 8),
                8, 8, 1));
    }

    @Test
    public void bombTestWithBorder() {
        // checks for correct bomb handling
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
                new Transitions(8, 8),
                8, 8, 3);

        bombController.updateMap(Coder.encodeMove(2, 5, 0, 1),
                new GameAttributes(2, 0, 2, 3, (byte) 2));

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
                new Transitions(8, 8),
                8, 8, 3));
    }

    @Test
    public void bombTestWithBorderAndTransition() {
        // checks for correct bomb handling

        GameClient client = new GameClient();
        client.initTransitions(new String[]{"0 7 5 <-> 7 0 1"}, 8, 8);

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
                client.getTransitions(),
                8, 8, 3);

        bombController.updateMap(Coder.encodeMove(2, 5, 0, 1),
                new GameAttributes(2, 0, 2, 3, (byte) 2));

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
                client.getTransitions(),
                8, 8, 3));
    }

    @Test
    public void bombOnlyReachableTiles() {
        GameClient client = new GameClient();
        client.initTransitions(new String[]{"0 7 5 <-> 7 0 1"}, 8, 8);

        MapController bombController = new MapController(
                new String[]{
                        "0 0 0 0 0 0 0 0",
                        "0 0 0 0 0 0 0 0",
                        "0 0 0 0 0 0 0 0",
                        "0 0 0 0 0 0 0 0",
                        "0 0 0 0 x 0 0 0",
                        "0 0 0 0 - - - -",
                        "0 0 0 0 - 0 0 -",
                        "0 0 0 0 - - - -"
                }, client.getTransitions(),
                8, 8, 3);

        bombController.updateMap(Coder.encodeMove(4, 4, 0, 1),
                new GameAttributes(2, 0, 2, 3, (byte) 2));

        assertEquals(bombController, new MapController(
                new String[]{
                        "0 0 0 0 0 0 0 0",
                        "0 - - - - - - -",
                        "0 - - - - - - -",
                        "0 - - - - - - -",
                        "0 - - - - - - -",
                        "0 - - - - - - -",
                        "0 - - - - 0 0 -",
                        "0 - - - - - - -"
                }, client.getTransitions(),
                8, 8, 3));
    }

    @Test
    public void overlappingTest() {
        // checks for overlapping capturing

        GameClient client = new GameClient();
        client.initTransitions(new String[]{"6 0 0 <-> 7 4 2"}, 8, 8);

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
                client.getTransitions(),
                8, 8, 0);

        overlappingController.updateMap(Coder.encodeMove(6, 7, 0, 2),
                new GameAttributes(2, 0, 0, 0, (byte) 1));

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
                client.getTransitions(),
                8, 8, 0));
    }

    @Test
    public void allDirectionsCapturingOverlappingTest() {
        // checks for complex capturing in all directions with overlapping

        GameClient client = new GameClient();
        client.initTransitions(new String[]{"0 3 6 <-> 1 0 0",
                "6 0 0 <-> 7 3 2"}, 8, 8);

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
                client.getTransitions(),
                8, 8, 0);

        allDirectionsController.updateMap(Coder.encodeMove(4, 3, 0, 2),
                new GameAttributes(2, 0, 0, 0, (byte) 1));

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
                client.getTransitions(),
                8, 8, 0));
    }

}
