package ulb.infofond;


/**
 * Created by hboulahy on 20/05/17.
 */
public final class AttacksFactory {

    public static boolean[][] towerAttacks(int numberOfRows, int numberOfCols){
        int numberOfElements = numberOfRows * numberOfCols;
        boolean[][] attacks = new boolean[numberOfElements][numberOfElements];
        for(int i = 0; i < numberOfElements; i++){
            for(int j = 0; j < numberOfElements; j++){
                attacks[i][j] = towerAttack(i, j, numberOfCols);
            }
        }
        return attacks;
    }

    public static boolean[][] foolAttacks(int numberOfRows, int numberOfCols) {
        int numberOfElements = numberOfRows * numberOfCols;
        boolean[][] attacks = new boolean[numberOfElements][numberOfElements];
        for(int i = 0; i < numberOfElements; i++){
            for(int j = 0; j < numberOfElements; j++){
                attacks[i][j] = foolAttack(i, j, numberOfCols);
            }
        }return attacks;
    }

    public static boolean[][] knightAttacks(int numberOfRows, int numberOfCols) {
        int numberOfElements = numberOfRows * numberOfCols;
        boolean[][] attacks = new boolean[numberOfElements][numberOfElements];
        for(int i = 0; i < numberOfElements; i++){
            for(int j = 0; j < numberOfElements; j++){
                attacks[i][j] = knightAttack(i, j, numberOfCols);
            }
        }return attacks;
    }

    private static boolean knightAttack(int i, int j, int numberOfCols) {
        int ir = getRow(i, numberOfCols);
        int ic = getCol(i, numberOfCols);
        int jr = getRow(j, numberOfCols);
        int jc = getCol(j, numberOfCols);
        return (Math.abs(ir - jr) == 2 && Math.abs(ic - jc) == 1) || (Math.abs(ir - jr) == 1 && Math.abs(ic -jc) == 2);
    }

    private static boolean foolAttack(int i, int j, int numberOfCols) {
        int ir = getRow(i, numberOfCols);
        int ic = getCol(i, numberOfCols);
        int jr = getRow(j, numberOfCols);
        int jc = getCol(j, numberOfCols);
        return Math.abs(ir - jr) == Math.abs(ic - jc);
    }

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
