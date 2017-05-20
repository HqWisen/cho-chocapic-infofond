package ulb.infofond;

/**
 * Created by hboulahy on 20/05/17.
 */
public class MuseumMap {

    private final static Character OBSTACLE = '*';


    private Character[][] map;
    private int numberOfRows, numberOfCols;

    public MuseumMap(Character[][] map){
        this.map = map;
        initSizeAttribute();

    }

    private void initSizeAttribute() {
        this.numberOfRows = this.map.length;
        this.numberOfCols = this.map[0].length;
    }

    public void show() {
        System.out.println(String.format("MuseumMap size: %dx%d", numberOfRows, numberOfCols));
        for (int i = 0; i < numberOfRows; i++) {
            for (int j = 0; j < numberOfCols; j++) {
                System.out.print(map[i][j] + " ");
            }
            System.out.println();
        }
    }

    public boolean isEmpty(int i, int j) {
        return map[i][j] != OBSTACLE;
    }

    public int getNumberOfRows() {
        return numberOfRows;
    }

    public int getNumberOfCols() {
        return numberOfCols;
    }

    public Character[][] getAsMatrix(){
        return this.map;
    }
}
