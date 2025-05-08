/**
 * In this class, there are all rules, that has to be checked in the first and second phase.
 */
public class GameRules {

    /**
     * Checks if the move ends up on a tile with a hole.
     * @param move Represents a move made by a player on the board.
     * @param map Instance of a Map Controller
     * @return true if move is not a hole, false otherwise
     */
    public static boolean isNotHole(Move move, MapController map ){
        return map.getTile(move.getX(), move.getY()) != '-';
    }

    /**
     * Checks if the move is inside the map.
     * @param move Represents a move made by a player on the board.
     * @param map Instance of a Map Controller
     * @return true if the x - and y - coordinate are inside the map.
     */
    public static boolean insideMap(Move move, MapController map){
        return map.getWidth() > move.getX() && move.getX() >= 0 && map.getHeight() > move.getY() && move.getY() >= 0;
    }

    /**
     * Checks whether the tile the player wants to move to on is already occupied.
     * @param move Represents a move made by a player on the board.
     * @param map Instance of a Map Controller
     * @return true, if the tile is occupied by another player or an extension stone
     */
    public static boolean occupied(Move move, MapController map){
        return !(map.getTile(move.getX(), move.getY()) == '0' || map.getTile(move.getX(), move.getY()) == 'b' || 
        map.getTile(move.getX(), move.getY()) == 'i' || map.getTile(move.getX(), move.getY()) == 'c');
    }

    /**
     * Checks if the player, who is making a move, still has at least one bomb.
     * @param state Gamestate: captures the phase, amount of players, and the player objects
     * @param move Represents a move made by a player on the board.
     * @return true if the player, who is making a move, has more than 0 bombs.
     */
    public static boolean hasBomb(GameState state, Move move){
        return state.getPlayer(move.getPlayer()).getNumberBombs() > 0;
    }

    /**
     * Checks whether the moving player has still an overwrite.
     * @param state Gamestate: captures the phase, amount of players, and the player objects
     * @param move Represents a move made by a player on the board.
     * @return true, if the moving player has still an override
     */
    public static boolean hasOverwrite(GameState state, Move move){
        return state.getPlayer(move.getPlayer()).getNumberOverwrites() > 0;
    }

    /**
     * Checks if the player number of the player, who is taking a turn, is valid.
     * @param move Represents a move made by a player on the board.
     * @param state Gamestate: captures the phase, amount of players, and the player objects
     * @return true, if the player number is valid, meaning the player is participating
     */
    public static boolean validMove(Move move, GameState state){
        if(move.getPlayer() < 1 || move.getPlayer() > state.getNumberPlayers()) return false;
        return state.getPlayer(move.getPlayer()).isInGame();
    }

    /**
     * Checks whether the tile, the player wants to move to, is occupied by an expansion stone.
     * @param map Instance of a Map Controller.
     * @param move Represents a move made by a player on the board.
     * @return true, if the tile is occupied by an expansion stone.
     */
    public static boolean isExpansion(MapController map, Move move){
        return map.getTile(move.getX(), move.getY()) == 'x';
    }

    /**
     * If the tile is a special tile, the move object must be checked to ensure it contains a valid entry.
     * @param state Gamestate: captures the phase, amount of players, and the player instances.
     * @param move Represents a move made by a player on the board.
     * @param map Instance of a Map Controller.
     * @return true, if all entries in the "special"-field of the move object are valid.
     */
    public static boolean checkSpecial(GameState state, Move move, MapController map){
        if(!state.isPhase1()) return move.getSpecial() == 0;
        switch(map.getTile(move.getX(), move.getY())){
            case 'b': {
                return move.getSpecial() == 20 || move.getSpecial() == 21;
            }
            case 'c': {
                return move.getSpecial() >= 1 && move.getSpecial() <= state.getNumberPlayers();
            }
            default: return move.getSpecial() == 0;
        }
    }

    /**
     * A player can only occupy a tile, if he can enclose stones from other players (or expansion stones) with his new occupied tile and
     * another tile that is occupied by this player. This method checks, if this capturing is valid.
     * @param move Represents a move made by a player on the board.
     * @param map Instance of a Map Controller.
     * @return true if the player occupies a tile that results in capturing one or more other tiles (if he makes a valid move).
     */
    public static boolean capturing(Move move, MapController map){
        int tilesPassed;
        boolean search;
        Position currentPosition;
        for(int i = 0; i < 8; i++) {
            currentPosition = new Position(move.getX(), move.getY(), i);
            search = true;
            tilesPassed = 0;
            while(search){
                currentPosition = map.getNextTile(currentPosition);
                if (currentPosition == null) break;
                if(String.valueOf(move.getPlayer()).equals(String.valueOf(map.getTile(currentPosition.x(), currentPosition.y())))){
                    if(tilesPassed > 0) {
                        if (currentPosition.x() != move.getX() || currentPosition.y() != move.getY()) return true; //diese Abfrage muss eher und vor dem tile ckeck bzw. jetzt kann weg vllt
                        search = false;
                    }
                    else search = false;
                } 
                else if (helpCompare('0', currentPosition, map)) search = false;
                else if (helpCompare('c', currentPosition, map)) search = false;
                else if (helpCompare('i', currentPosition, map)) search = false;
                else if (helpCompare('b', currentPosition, map)) search = false;
                else if (helpCompare('-', currentPosition, map)) search = false;
                else if (currentPosition.x() == move.getX() || currentPosition.y() == move.getY()) search = false; //added to fix bug self capturing
                else {
                    tilesPassed++;
                }
            }
        }
        return false;
    }

    private static boolean helpCompare(char c, Position currentPosition, MapController map){
        return (map.getTile(currentPosition.x(), currentPosition.y()) == c);
    }

}


