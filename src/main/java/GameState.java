public record GameState(MapController mapController, GameAttributes attributes) {
    

    public void updateGameState(int move){
        this.attributes().updateState(move, this.mapController());
        this.mapController().updateMap(move, this.attributes());
    }

    @Override
    public GameState clone(){
        return new GameState(new MapController(this.mapController()), new GameAttributes(this.attributes()));
    }
}
