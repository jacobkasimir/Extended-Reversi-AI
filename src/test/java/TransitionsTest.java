import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

public class TransitionsTest {

    @Test
    public void initTransitionsTest(){
        Transitions.getInstance().init(8, 8);

        assertEquals(Transitions.getInstance().getWidth(), 8);
        assertEquals(Transitions.getInstance().getHeight(), 8);
    }


    @Test
    public void setGetTransitionTest(){
        Transitions.getInstance().init(8, 8);
        Transitions.getInstance().setTransition(1, 1, 3, 4, 5, 2);
        assertEquals(Transitions.getInstance().getHeight(), 8);
        assertEquals(Transitions.getInstance().getTransition(1, 1, 3), new Position(4,5,2));
        assertEquals(Transitions.getInstance().getTransition(4, 5, 2), new Position(1,1,3));
    
    }

    
    
}
