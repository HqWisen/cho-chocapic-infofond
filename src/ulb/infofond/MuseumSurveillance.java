package ulb.infofond;

import org.chocosolver.solver.Model;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by hboulahy on 18/05/17.
 */


public class MuseumSurveillance {
    private final static Logger log = Logger.getLogger(MuseumSurveillance.class.getName());

    static {
        log.setLevel(Level.INFO);
    }

    public static List<Character[]> getMapLines(String filename) {
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

    public static Character[][] parseMap(String filename) {
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
        return map;
    }

    private static Character[] parseLine(String line) {
        List<Character> elements = new ArrayList<>();
        for(int i = 0; i < line.length(); i+=2){
            elements.add(line.charAt(i));
        }
        Character[] array = elements.toArray(new Character[elements.size()]);
        log.info(Arrays.toString(array));
        return array;
    }

    private static void showMap(Character[][] map) {
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[i].length; j++) {
                System.out.print(map[i][j]);
            }
            System.out.println();
        }
    }

    public static void main(String[] args) {
        System.out.println("Starting Museum Surveillance..");
        Character[][] map = parseMap("input/museum.txt");
        showMap(map);
        int n = 4;
        Model model = new Model("Museum Surveillance");


    }

}
