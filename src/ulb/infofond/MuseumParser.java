package ulb.infofond;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by hboulahy on 20/05/17.
 */
public class MuseumParser {

    private static final Logger log = Logger.getLogger(MuseumParser.class.getName());

    static{
        log.setLevel(Level.OFF);
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
        } catch (FileNotFoundException e) {
            System.out.println("File '" + filename + "' doesn't exist.\nAbort.");
            System.exit(1);
        } catch (IOException e) {
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

    private MuseumMap museumMap;

    public MuseumParser(String filename){
        this.museumMap = new MuseumMap(parseMap(filename));
    }

    public MuseumMap getMuseumMap(){
        return this.museumMap;
    }
}
