package algorithms.strategies;
import gamestate.GameState;

public record Score(int move, Integer score, GameState gameState) implements Comparable<Score>{
    @Override
    public int compareTo(Score o) {
        return this.score.compareTo(o.score());
    }

}
