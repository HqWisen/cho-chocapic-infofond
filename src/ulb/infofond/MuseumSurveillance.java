package ulb.infofond;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.variables.IntVar;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by hboulahy on 18/05/17.
 */


public class MuseumSurveillance {
    private final static Logger log = Logger.getLogger(MuseumSurveillance.class.getName());
    private final static Character OBSTACLE = '*';

    static {
        log.setLevel(Level.INFO);
    }

    private static Character[] parseLine(String line) {
        List<Character> elements = new ArrayList<>();
        for (int i = 0; i < line.length(); i += 2) {
            elements.add(line.charAt(i));
        }
        Character[] array = elements.toArray(new Character[elements.size()]);
        // log.info(Arrays.toString(array));
        return array;
    }

    private static List<Character[]> getMapLines(String filename) {
        List<Character[]> mapLines = new ArrayList<>();
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(filename));
            String line;
            while ((line = reader.readLine()) != null) {
                mapLines.add(parseLine(line));
            }
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mapLines;
    }

    private static Character[][] parseMap(String filename) {
        log.info(String.format("Parsing %s", filename));
        List<Character[]> lines = getMapLines(filename);
        int numberOfRows = lines.size();
        int numberOfCols = lines.get(0).length;
        log.info(String.format("size %d x %d", numberOfRows, numberOfCols));
        Character[][] map = new Character[numberOfRows][numberOfCols];
        for (int r = 0; r < numberOfRows; r++) {
            for (int c = 0; c < numberOfCols; c++) {
                map[r][c] = lines.get(r)[c];
            }
        }
        log.info(String.format("Parsing results: %s", Arrays.deepToString(map)));
        return map;
    }


    private Character[][] map;
    private int numberOfRows, numberOfCols;
    private Model model;
    private IntVar laserVars, obstacleVars;
    private Map<Integer, IntVar> watcherVars;
    private List<Integer> emptyElements;

    public MuseumSurveillance(String filename) {
        System.out.println("Creating MuseumSurveillance");
        this.map = parseMap(filename);
        initSizeAttribute();
        buildEmptyElements();
        this.model = new Model("MuseumSurveillance");
        initWatcherVars();
        initLaserVars();
        initObstacleVars();

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
     *
     * @param i row to check
     * @param j col to check
     * @return true if there is NO obstacle, false otherwise
     */
    private boolean isEmpty(int i, int j) {
        return map[i][j] != OBSTACLE;
    }

    /**
     * Initiliaze the Watchers variables.
     * Those variables correspond to all variables per empty elements.
     * The domain correspond of all elements that can watch this empty element
     * i.e. the elements where a laser can be put to monitor this empty element.
     */
    private void initWatcherVars() {
        this.watcherVars = new HashMap<>();
        for (Integer element : getEmptyElements()) {
            this.watcherVars.put(element, buildElementDomain(element));
        }
    }

    /**
     * This function build to domain for an element.
     * The domain correspond to the set of elements that have the same row
     * and the same column that the element itself. Each element of the
     * set must also be accessible by the element (from the parameter) without
     * any obstacle.
     * REMINDER: element is just a value that as row and col values
     * (using method {@link MuseumSurveillance#getRow(int)} or {@link MuseumSurveillance#getCol(int)})
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
        // all.add(element);
        // TODO to test that this returns an array of the list values
        int[] values  = all.stream().mapToInt(i->i).toArray();
        return model.intVar(String.format("(%d, %d)", getRow(element), getCol(element)), values);
    }

    private List<Integer> getNorthElements(Integer element){
        int i = getRow(element);
        int j = getCol(element);
        List<Integer> norths = new ArrayList<>();
        for(int k = 0; k < i; k++){
            if(isEmpty(k, j)) {
                norths.add(getElement(k, j));
            }else{
                break;
            }
        }
        return norths;
    }

    private List<Integer> getSouthElements(Integer element){
        int i = getRow(element);
        int j = getCol(element);
        List<Integer> souths = new ArrayList<>();
        for(int k = i+1; k < numberOfRows; k++){
            if(isEmpty(k, j)) {
                souths.add(getElement(k, j));
            }else{
                break;
            }
        }
        return souths;
    }

    private List<Integer> getWestElements(Integer element){
        int i = getRow(element);
        int j = getCol(element);
        List<Integer> wests = new ArrayList<>();
        for(int k = 0; k < j; k++){
            if(isEmpty(i, k)) {
                wests.add(getElement(i, k));
            }else{
                break;
            }
        }
        return wests;
    }

    private List<Integer> getEastElements(Integer element){
        int i = getRow(element);
        int j = getCol(element);
        List<Integer> easts = new ArrayList<>();
        for(int k = j+1; k < numberOfCols; k++){
            if(isEmpty(i, k)) {
                easts.add(getElement(i, k));
            }else{
                break;
            }
        }
        return easts;
    }


    private void initLaserVars() {

    }

    private void initObstacleVars() {

    }

    private void initSizeAttribute() {
        this.numberOfRows = this.map.length;
        this.numberOfCols = this.map[0].length;
    }

    private void showMap() {
        System.out.println(String.format("Map size: %dx%d", numberOfRows, numberOfCols));
        for (int i = 0; i < numberOfRows; i++) {
            for (int j = 0; j < numberOfCols; j++) {
                System.out.print(map[i][j] + " ");
            }
            System.out.println();
        }
    }

    // TODO check that the getCol and getRow methods work correctly (units tests ?)
    // TODO build try catch if element is not between 1 and rows*cols
    /**
     * element must be between 1 and rows*cols
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

    public Integer[] getCoordinates(int element) {
        return new Integer[]{getRow(element), getCol(element)};
    }

    /**
     *
     * @return all empty elements (with no obstacle) of the map
     */
    public List<Integer> getEmptyElements(){
        return this.emptyElements;
    }
    public static void main(String[] args) {
        String filename = "input/museum.txt";
        MuseumSurveillance solver = new MuseumSurveillance(filename);
        solver.showMap();
    }

}
