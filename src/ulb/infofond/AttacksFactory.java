package ulb.infofond;


/**
 * Created by hboulahy on 20/05/17.
 */
public final class AttacksFactory {

    public static boolean[][] towerAttacks(int numberofRows, int numberofCols){
        int numberOfElements = numberofRows * numberofCols;
        boolean[][] attacks = new boolean[numberOfElements][numberOfElements];
        for(int i = 0; i < numberOfElements; i++){
            for(int j = 0; j < numberOfElements; j++){
                attacks[i][j] = towerAttack(i, j, numberofCols);
            }
        }
        return attacks;
    }

    /*public static boolean[][] foolAttacks(int numberofRows, int numberofCols){
        int numberOfElements = numberofRows * numberofCols;
        boolean[][] attacks = new boolean[numberOfElements][numberOfElements];
        for(int i = 0; i < numberOfElements; i++){
            for(int j = 0; j < numberOfElements; j++){
                attacks[i][j] = towerAttack(i, j, numberofCols);
            }
        }
        return attacks;
    }
    */
    private static boolean towerAttack(int i, int j, int numberOfCols) {
        int ir = getRow(i, numberOfCols);
        int ic = getCol(i, numberOfCols);
        int jr = getRow(j, numberOfCols);
        int jc = getCol(j, numberOfCols);
        return ir == jr || ic == jc;
    }

    public static int getCol(int element, int numberOfCols) {
        return element % numberOfCols;
    }

    public static int getRow(int element, int numberOfCols) {
        return (element - getCol(element, numberOfCols)) / numberOfCols;
    }

}
