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
    public static boolean isNotHole(int move, MapController map ){
        return map.getTile(MoveVerifier.decodeX(move), MoveVerifier.decodeY(move)) != '-';
    }

    /**
     * Checks if the move is inside the map.
     * @param move Represents a move made by a player on the board.
     * @param map Instance of a Map Controller
     * @return true if the x - and y - coordinate are inside the map.
     */
    public static boolean insideMap(int move, MapController map){
        return map.getWidth() > MoveVerifier.decodeX(move) && MoveVerifier.decodeX(move) >= 0 && map.getHeight() > MoveVerifier.decodeY(move) && MoveVerifier.decodeY(move) >= 0;
    }

    /**
     * Checks whether the tile the player wants to move to on is already occupied.
     * @param move Represents a move made by a player on the board.
     * @param map Instance of a Map Controller
     * @return true, if the tile is occupied by another player or an extension stone
     */
    public static boolean occupied(int move, MapController map){
        return !(map.getTile(MoveVerifier.decodeX(move), MoveVerifier.decodeY(move)) == '0' || map.getTile(MoveVerifier.decodeX(move), MoveVerifier.decodeY(move)) == 'b' || 
        map.getTile(MoveVerifier.decodeX(move), MoveVerifier.decodeY(move)) == 'i' || map.getTile(MoveVerifier.decodeX(move), MoveVerifier.decodeY(move)) == 'c');
    }

    /**
     * Checks if the player, who is making a move, still has at least one bomb.
     * @param state Gamestate: captures the phase, number of players, and the player objects
     * @param move Represents a move made by a player on the board.
     * @return true if the player, who is making a move, has more than 0 bombs.
     */
    public static boolean hasBomb(GameAttributes state, int move){
        return state.getPlayer(MoveVerifier.decodePlayerId(move)).getNumberBombs() > 0;
    }

    /**
     * Checks whether the moving player has still an overwrite.
     * @param state Gamestate: captures the phase, number of players, and the player objects
     * @param move Represents a move made by a player on the board.
     * @return true, if the moving player has still an override
     */
    public static boolean hasOverwrite(GameAttributes state, int move){
        return state.getPlayer(MoveVerifier.decodePlayerId(move)).getNumberOverwrites() > 0;
    }

    /**
     * Checks if the player number of the player, who is taking a turn, is valid.
     * @param move Represents a move made by a player on the board.
     * @param state Gamestate: captures the phase, number of players, and the player objects
     * @return true, if the player number is valid, meaning the player is participating
     */
    public static boolean validMove(int move, GameAttributes state){
        if(MoveVerifier.decodePlayerId(move) < 1 || MoveVerifier.decodePlayerId(move) > state.getNumberPlayers()) return false;
        return state.getPlayer(MoveVerifier.decodePlayerId(move)).isInGame();
    }

    /**
     * Checks whether the tile, the player wants to move to is occupied by an expansion stone.
     * @param map Instance of a Map Controller.
     * @param move Represents a move made by a player on the board.
     * @return true, if the tile is occupied by an expansion stone.
     */
    public static boolean isExpansion(MapController map, int move){
        return map.getTile(MoveVerifier.decodeX(move), MoveVerifier.decodeY(move)) == 'x';
    }

    /**
     * If the tile is a special tile, the move object must be checked to ensure it contains a valid entry.
     * @param state Gamestate: captures the phase, number of players, and the player instances.
     * @param move Represents a move made by a player on the board.
     * @param map Instance of a Map Controller.
     * @return true, if all entries in the "special"-field of the move object are valid.
     */
    public static boolean checkSpecial(GameAttributes state, int move, MapController map){
        if(!state.isPhase1()) return MoveVerifier.decodePlayerId(move) == 0;
        return switch (map.getTile(MoveVerifier.decodeX(move), MoveVerifier.decodeY(move))) {
            case 'b' -> MoveVerifier.decodeSpecial(move) == 20 || MoveVerifier.decodeSpecial(move) == 21;
            case 'c' ->
                    MoveVerifier.decodeSpecial(move) >= 1 && MoveVerifier.decodeSpecial(move) <= state.getNumberPlayers();
            default -> MoveVerifier.decodeSpecial(move) == 0;
        };
    }

    /**
     * A player can only occupy a tile if he can enclose stones from other players (or expansion stones) with his new occupied tile and
     * another tile that is occupied by this player. This method checks if this capturing is valid.
     * @param move Represents a move made by a player on the board.
     * @param map Instance of a Map Controller.
     * @return true if the player occupies a tile that results in capturing one or more other tiles (if he makes a valid move).
     */
    public static boolean capturing(int move, MapController map){
        int tilesPassed;
        boolean search;
        int currentPosition;
        for(int i = 0; i < 8; i++) {
            currentPosition = MoveVerifier.encodePosition(MoveVerifier.decodeX(move), MoveVerifier.decodeY(move), i);
            search = true;
            tilesPassed = 0;
            while(search){
                currentPosition = map.getNextTile(currentPosition);
                if(currentPosition < 0) break;
                if(String.valueOf(MoveVerifier.decodePlayerId(move)).equals(String.valueOf(map.getTile(MoveVerifier.decodePosX(currentPosition), MoveVerifier.decodePosY(currentPosition))))){
                    if(tilesPassed > 0) {
                        if (MoveVerifier.decodePosX(currentPosition) != MoveVerifier.decodeX(move) || MoveVerifier.decodePosY(currentPosition) != MoveVerifier.decodeY(move)) return true; //diese Abfrage muss eher und vor dem tile ckeck bzw. jetzt kann weg vllt
                        search = false;
                    }
                    else search = false;
                } 
                else if (helpCompare('0', currentPosition, map)) search = false;
                else if (helpCompare('c', currentPosition, map)) search = false;
                else if (helpCompare('i', currentPosition, map)) search = false;
                else if (helpCompare('b', currentPosition, map)) search = false;
                else if (helpCompare('-', currentPosition, map)) search = false;
                else if (MoveVerifier.decodePosX(currentPosition) == MoveVerifier.decodeX(move) && MoveVerifier.decodePosY(currentPosition) == MoveVerifier.decodeY(move)) search = false;
                else {
                    tilesPassed++;
                }
            }
        }
        return false;
    }

    private static boolean helpCompare(char c, int currentPosition, MapController map){
        return (map.getTile(MoveVerifier.decodePosX(currentPosition), MoveVerifier.decodePosY(currentPosition)) == c);
    }

}


