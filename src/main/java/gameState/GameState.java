package gamestate;

import gamestate.attributes.GameAttributes;
import gamestate.map.MapController;

/**
 * Represents the current state of the game, containing the map controller and game attributes.
 * This class is implemented as a record to maintain immutability of its components and efiiciency.
 * 
 * @param mapController The controller managing the game map
 * @param attributes The attributes and properties of the current game
 */
public record GameState(MapController mapController, GameAttributes attributes) {
    
    /**
     * Updates the current game state based on the specified move.
     * This method updates both the game attributes and the map controller.
     *
     * @param move The move to be processed and applied to the game state
     */
    public void updateGameState(int move){
        this.attributes().updateState(move, this.mapController());
        this.mapController().updateMap(move, this.attributes());
    }

    /**
     * Creates and returns a deep copy of the current game state.
     * Both the MapController and GameAttributes are cloned to ensure complete isolation.
     *
     * @return A new GameState instance with cloned components
     */
    @Override
    public GameState clone(){
        return new GameState(new MapController(this.mapController()), new GameAttributes(this.attributes()));
    }
}