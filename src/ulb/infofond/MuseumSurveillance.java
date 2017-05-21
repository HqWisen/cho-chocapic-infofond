package ulb.infofond;

import com.sun.xml.internal.bind.v2.runtime.reflect.opt.Const;
import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.constraints.Constraint;
import org.chocosolver.solver.variables.BoolVar;
import org.chocosolver.solver.variables.IntVar;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by hboulahy on 18/05/17.
 */

public class MuseumSurveillance {
    private final static Logger log = Logger.getLogger(MuseumSurveillance.class.getName());
    private final static Character OBSTACLE = '*';
    private final static int NORTH = 0, SOUTH = 1, WEST = 2, EAST = 3, SELF = 4;
    private final static int[] DIROF_DOMAIN = {NORTH, SOUTH, EAST, WEST, SELF};
    static {
        log.setLevel(Level.OFF);
    }

    private Character[][] map;
    private int numberOfRows, numberOfCols;
    private Model model;
    private BoolVar[] laserVars;
    private Map<Integer, IntVar> dirOfVars;
    private Map<Integer, IntVar> watcherVars;
    // Directions per empty cases variables (in watcherVars) that contains a direction per element
    private Map<Integer, Integer[]> directions;
    private IntVar numberOfLasersVar;
    private List<Integer> emptyElements;
    private Solver solver;

    public MuseumSurveillance(String filename) {
        System.out.println("Creating MuseumSurveillance");
        this.map = new MuseumParser(filename).getMuseumMap().getAsMatrix();
        initSizeAttribute();
        buildEmptyElements();
        this.model = new Model("MuseumSurveillance");
        initWatcherVarsAndDirectionVars();
        this.laserVars = model.boolVarArray("Lasers", getNumberOfElements());
        this.numberOfLasersVar = model.intVar("Lasers", 0, getNumberOfElements(), true);
        this.solver = model.getSolver();
        for (Integer i : watcherVars.keySet()) {
            // log.info("Creating constraint for watcherVar = " + watcherVars.get(i));
            model.element(model.intVar(1), laserVars, watcherVars.get(i), 0).post();
        }
        postDirectionConstraints();
        model.sum(laserVars, "=", numberOfLasersVar).post();
        model.setObjective(false, numberOfLasersVar);

    }

    public boolean solve(){
        boolean solved = solver.solve();
        while (solved) {
            System.out.println("Solution #" + solver.getSolutionCount());
            showMap();
            /*showMapWatchers();
            showMapDirections();
            */
            solved = solver.solve();
        }
        System.out.println("The latest solution is the optimal solution. If nothing shown," +
                "it means that there is no solution.");
        return  solved;
    }

    private void showMapDirections() {
        System.out.println(String.format("[DirOf] MuseumMap size: %dx%d", numberOfRows, numberOfCols));
        for (int i = 0; i < numberOfRows; i++) {
            for (int j = 0; j < numberOfCols; j++) {
                IntVar var = dirOfVars.get(getElement(i, j));
                if(var != null){
                    System.out.print(String.format("%10s", parseDirection(var.getValue())));
                }else{
                    System.out.print(String.format("%10s", map[i][j]));
                }
            }
            System.out.println();
        }
    }

    private String parseDirection(int value) {
        String result = null;
        switch (value){
            case NORTH:
                result = "N";
                break;
            case SOUTH:
                result = "S";
                break;
            case EAST:
                result = "E";
                break;
            case WEST:
                result = "W";
                break;
            case SELF:
                result = "[L]"; // Laser
                break;
        }
        return result;
    }

    private void showMapWatchers() {
        System.out.println(String.format("[Watchers] MuseumMap size: %dx%d", numberOfRows, numberOfCols));
        for (int i = 0; i < numberOfRows; i++) {
            for (int j = 0; j < numberOfCols; j++) {
                IntVar var = watcherVars.get(getElement(i, j));
                if(var != null){
                    System.out.print(String.format("%10s", getCoordStr(var.getValue())));
                }else{
                    System.out.print(String.format("%10s", map[i][j]));
                }
            }
            System.out.println();
        }
    }

    private String getCoordStr(int element) {
        return Arrays.toString(getCoordinates(element));
    }

    private void postDirectionConstraints() {
        postDirectionOfConstraints();
        for(Integer i : getEmptyElements()){
            for(Integer j : getEmptyElements()) {
                model.or(buildAreLasers(i, j),
                         buildDifferentWatcher(i, j),
                         buildSameDirection(i, j)).post();
            }
        }
    }

    private Constraint buildAreLasers(Integer i, Integer j){
        return model.or(model.arithm(dirOfVars.get(i), "=", SELF),
                        model.arithm(dirOfVars.get(j), "=", SELF));
    }

    private Constraint buildDifferentWatcher(Integer i, Integer j){
        return model.arithm(watcherVars.get(i), "!=", watcherVars.get(j));
    }

    private Constraint buildSameDirection(Integer i, Integer j) {
        return model.arithm(dirOfVars.get(i), "=", dirOfVars.get(j));
    }

    private Constraint buildSameWatcher(Integer i, Integer j) {
        return model.arithm(watcherVars.get(i), "=", watcherVars.get(j));
    }

    /**
     *
     * @param i
     * @param j
     * @return a constraint that indicate that i and j must be different that their watcher
     */
    private Constraint buildNotSelfWatcher(Integer i, Integer j) {
        return model.and(model.arithm(watcherVars.get(i), "!=", i),
                         model.arithm(watcherVars.get(j), "!=", j));
    }

    private void postDirectionOfConstraints() {
        for(Integer i : getEmptyElements()){
            model.element(dirOfVars.get(i), getDirectionInTable(i), watcherVars.get(i)).post();
        }
    }

    private int[] getDirectionInTable(Integer element) {
        int[] results = new int[getNumberOfElements()];
        for (int i = 0; i < getNumberOfElements(); i++){
            Integer value = directions.get(element)[i];
            results[i] = value != null ? value : -1;
         }
        return results;
    }

    /**
     * Build to emptyElements list:
     * this list correspond of the set of all empty elements (with no obstacle)
     * of the map
     */
    private void buildEmptyElements() {
        this.emptyElements = new ArrayList<>();
        for (int i = 0; i < numberOfRows; i++) {
            for (int j = 0; j < numberOfCols; j++) {
                if (isEmpty(i, j)) {
                    emptyElements.add(getElement(i, j));
                }
            }
        }
        log.info(String.format("Empty Elements are %d:%s", emptyElements.size(), emptyElements.toString()));
    }

    /**
     * @param i row to check
     * @param j col to check
     * @return true if there is NO obstacle, false otherwise
     */
    public boolean isEmpty(int i, int j) {
        return map[i][j] != OBSTACLE;
    }

    public boolean isEmpty(Integer element) {
        return isEmpty(getRow(element), getCol(element));
    }

    /**
     * Initiliaze the Watchers variables.
     * Those variables correspond to all variables per empty elements.
     * The domain correspond of all elements that can watch this empty element
     * i.e. the elements where a laser can be put to monitor this empty element.
     */
    private void initWatcherVarsAndDirectionVars() {
        log.info("Building Watcher variables and directions data");
        this.directions = new HashMap<>();
        this.watcherVars = new HashMap<>();
        this.dirOfVars = new HashMap<>();
        for (Integer element : getEmptyElements()) {
            this.directions.put(element, new Integer[getNumberOfElements()]);
            this.watcherVars.put(element, buildElementDomain(element));
            this.dirOfVars.put(element, model.intVar(String.format("DirectionOf %d", element),
                    DIROF_DOMAIN));
        }
        log.info("Watcher variables build with their respective domain and respective directions");
    }

    /**
     * This function build to domain for an element.
     * The domain correspond to the set of elements that have the same row
     * and the same column that the element itself. Each element of the
     * set must also be accessible by the element (given as parameter) without
     * any obstacle. The set also contain the element itself since if the element
     * is considered as a laser, it is automatically considered as self-monitored.
     * REMINDER: element is just a value that as row and col values
     * (using method {@link MuseumSurveillance#getRow(int)} or {@link MuseumSurveillance#getCol(int)})
     *
     * @param element domain is build based of the value of this element, it should NOT be an obstacle element
     * @return a model integer variables that as a domain define as the set of element
     * that respect the condition below.
     */
    private IntVar buildElementDomain(Integer element) {
        List<Integer> all = new ArrayList<>();
        all.addAll(getNorthElements(element));
        all.addAll(getSouthElements(element));
        all.addAll(getWestElements(element));
        all.addAll(getEastElements(element));
        // adding the element itself since if it is a laser, it should be count as as already monitored!
        all.add(element);
        directions.get(element)[element] = SELF;
        // TODO to test that this returns an array of the list values
        int[] values = all.stream().mapToInt(i -> i).toArray();
        return model.intVar(String.format("(%d, %d)", getRow(element), getCol(element)), values);
    }

    public List<Integer> getNorthElements(Integer element) {
        int i = getRow(element);
        int j = getCol(element);
        List<Integer> norths = new ArrayList<>();
        for (int k = i - 1; k > 0; k--) {
            if (isEmpty(k, j)) {
                norths.add(getElement(k, j));
                directions.get(element)[getElement(k, j)] = NORTH;
            } else {
                break;
            }
        }
        return norths;
    }

    public List<Integer> getSouthElements(Integer element) {
        int i = getRow(element);
        int j = getCol(element);
        List<Integer> souths = new ArrayList<>();
        for (int k = i + 1; k < numberOfRows; k++) {
            if (isEmpty(k, j)) {
                souths.add(getElement(k, j));
                directions.get(element)[getElement(k, j)] = SOUTH;
            } else {
                break;
            }
        }
        return souths;
    }

    public List<Integer> getWestElements(Integer element) {
        int i = getRow(element);
        int j = getCol(element);
        List<Integer> wests = new ArrayList<>();
        for (int k = j - 1; k > 0; k--) {
            if (isEmpty(i, k)) {
                wests.add(getElement(i, k));
                directions.get(element)[getElement(i, k)] = WEST;
            } else {
                break;
            }
        }
        return wests;
    }

    public List<Integer> getEastElements(Integer element) {
        int i = getRow(element);
        int j = getCol(element);
        List<Integer> easts = new ArrayList<>();
        for (int k = j + 1; k < numberOfCols; k++) {
            if (isEmpty(i, k)) {
                easts.add(getElement(i, k));
                directions.get(element)[getElement(i, k)] = EAST;
            } else {
                break;
            }
        }
        return easts;
    }


    private void initSizeAttribute() {
        this.numberOfRows = this.map.length;
        this.numberOfCols = this.map[0].length;
    }

    private void showMap() {
        System.out.println(String.format(" MuseumMap size: %dx%d", numberOfRows, numberOfCols));
        // showMapDirections();
        Map<Integer, String> laserDirections = buildLaserDirections();
        for (int i = 0; i < numberOfRows; i++) {
            for (int j = 0; j < numberOfCols; j++) {
                Integer element = getElement(i, j);
                if(laserVars[element].getValue() == 1){
                    String value = laserDirections.get(element);
                    System.out.print(String.format("%3s", value != null ? value : "L"));
                }else{
                    System.out.print(String.format("%3s", map[i][j]));
                }
            }
            System.out.println();
        }
    }

    private Map<Integer, String> buildLaserDirections() {
        Map<Integer, String> laserDirections = new HashMap<>();
        for(Integer element : getEmptyElements()){
            int watcher = watcherVars.get(element).getValue();
            int direction = dirOfVars.get(element).getValue();
            if(direction != SELF){
                laserDirections.put(watcher, getOppositeDirection(direction));
            }
        }
        return laserDirections;
    }

    private String getOppositeDirection(int value) {
        String result = "Unknown";
        switch(value){
            case NORTH:
                result = "S";
                break;
            case SOUTH:
                result = "N";
                break;
            case WEST:
                result = "E";
                break;
            case EAST:
                result = "W";
                break;
        }
        return result;
    }

    // TODO check that the getCol and getRow methods work correctly (units tests ?)
    // TODO build try catch if element is not between 1 and rows*cols

    /**
     * element must be between 1 and rows*cols
     *
     * @param element
     * @return the column value corresponding to the element
     */
    public int getCol(int element) {
        return element % numberOfCols;
    }

    // TODO check that the getCol and getRow methods work correctly (units tests ?)
    // TODO build try catch if element is not between 1 and rows*cols

    /**
     * element must be between 1 and rows*cols
     *
     * @param element
     * @return the row value corresponding to the element
     */
    public int getRow(int element) {
        return (element - getCol(element)) / numberOfCols;
    }

    /**
     * @param i row of the element
     * @param j column of the element
     * @return element is the value of the case corresponding to the coordinates (i, j)
     */
    public int getElement(int i, int j) {
        return i * numberOfCols + j;
    }

    public int getNumberOfElements() {
        return numberOfRows * numberOfCols;
    }

    public int getNumberOfEmptyElements() {
        return this.emptyElements.size();
    }

    public Integer[] getCoordinates(int element) {
        return new Integer[]{getRow(element), getCol(element)};
    }

    /**
     *
     * @param element key of the directions map, it must be a empty element
     * @return direction map of all empty elements surrounding 'element'
     */
    public Integer[] getDirectionTable(Integer element){
        return this.directions.get(element);
    }
    /**
     * @return all empty elements (with no obstacle) of the map
     */
    public List<Integer> getEmptyElements() {
        return this.emptyElements;
    }

    public static void main(String[] args) {
        String filename = "input/museum.txt";
        MuseumSurveillance solver = new MuseumSurveillance(filename);
        solver.solve();
    }

}
