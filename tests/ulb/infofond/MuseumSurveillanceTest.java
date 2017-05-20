package ulb.infofond;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Created by hboulahy on 18/05/17.
 */
class MuseumSurveillanceTest {

    private static MuseumSurveillance solver;

    @BeforeAll
    public static void setUp() {
        solver = new MuseumSurveillance("input/test.txt");
    }

    @Test
    void isEmpty() {
        assertEquals(solver.isEmpty(0, 0), false);
        assertEquals(solver.isEmpty(1, 1), true);
        assertEquals(solver.isEmpty(3, 0), false);
        assertEquals(solver.isEmpty(3, 1), true);
        assertEquals(solver.isEmpty(4, 4), true);
    }

    // TODO those 4 methods have been tested MANUALLY and should work, however could be good to have real tests
    @Test
    void getNorthElements() {
    }
    @Test
    void getSouthElements() {
    }

    @Test
    void getWestElements() {
    }

    @Test
    void getEastElements() {
    }

    @Test
    void directionsContent(){
        for(Integer element : solver.getEmptyElements()){
            // System.out.println(element + " - " + solver.getDirectionTable(element));
        }
    }

    @Test
    void getCol() {
        assertEquals(8, solver.getCol(8));
        assertEquals(1, solver.getCol(10));
        assertEquals(4, solver.getCol(31));
        assertEquals(4, solver.getCol(40));
    }

    @Test
    void getRow() {
        assertEquals(0, solver.getRow(0));
        assertEquals(1, solver.getRow(10));
        assertEquals(3, solver.getRow(31));
        assertEquals(4, solver.getRow(40));
    }

    @Test
    void getElement() {
        assertEquals(31, solver.getElement(3, 4));
    }

    @Test
    void getCoordinates() {
    }

    @Test
    void getNumberOfElements(){
        assertEquals(63, solver.getNumberOfElements());
    }

    @Test
    void getEmptyElements() {
        List<Integer> emptyElements = solver.getEmptyElements();
        assertEquals(22, emptyElements.size());
        int[] actuals = {solver.getElement(1, 1), solver.getElement(2, 1),
                solver.getElement(2, 2), solver.getElement(2, 3), solver.getElement(4, 4),
                solver.getElement(4, 7)};
        for (int i = 0; i < actuals.length; i++) {
            emptyElements.contains(actuals[i]);
        }
    }

}