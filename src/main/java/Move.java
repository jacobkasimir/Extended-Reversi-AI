public class Move {
    private final int x, y, special, player;

    public Move(int x, int y, int special, int player) {
        this.x = x;
        this.y = y;
        this.special = special;
        this.player = player;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getSpecial() {
        return special;
    }

    public int getPlayer() {
        return player;
    }
    
}
