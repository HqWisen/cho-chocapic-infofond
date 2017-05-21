package ulb.infofond;

import jdk.nashorn.internal.runtime.regexp.joni.exception.ValueException;
import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.variables.BoolVar;

import java.util.Arrays;

/**
 * Created by hboulahy on 20/05/17.
 */
public class Chess {

    private boolean domination;
    private int n, k1, k2, k3;
    private Model model;
    private BoolVar[] towerVars, foolVars;
    private BoolVar[] presenceVars;
    private boolean[][] towerAttacks, foolAttacks;

    public Chess(int n, int k1, int k2, int k3, boolean domination) {
        this.n = n;
        this.k1 = k1;
        this.k2 = k2;
        this.k3 = k3;
        this.domination = domination;
        showExecutionDetails();
        this.model = new Model("Chess Problem");
        this.towerVars = model.boolVarArray(getNumberOfElements());
        this.foolVars = model.boolVarArray(getNumberOfElements());
        this.presenceVars = model.boolVarArray(getNumberOfElements());
        this.towerAttacks = AttacksFactory.towerAttacks(n, n);
        this.foolAttacks = AttacksFactory.foolAttacks(n, n);
        postPresenceConstraints();
        postDifferentSpot();
        postPieceConstraints(towerVars, towerAttacks, k1);
        postPieceConstraints(foolVars, foolAttacks, k2);
        solve();
    }

    private void showExecutionDetails() {
        String mode = domination ? "DOMINITION" : "INDEPENDENCE";
        System.out.println("Running chess solver with:");
        System.out.println(String.format("n=%d k1=%d k2=%d k3=%d mode is %s", n, k1, k2, k3, mode));
    }

    private void solve() {
        // model.setObjective(false, totalVar);
        //while (solver.solve()) {
        //}
        Solver solver = model.getSolver();
        System.out.println(solver.solve());
        System.out.println("Solution #" + solver.getSolutionCount());
        showMap();
        System.out.print("TOWERS:   ");
        for(int i = 0; i < getNumberOfElements(); i++){
            System.out.print(String.format("%10s",towerVars[i].getBooleanValue()));
        }
        System.out.println();
        System.out.print("FOOLS:    ");
        for(int i = 0; i < getNumberOfElements(); i++){
            System.out.print(String.format("%10s",foolVars[i].getBooleanValue()));
        }
        System.out.println();
        System.out.print("PRESENCE: ");
        for(int i = 0; i < getNumberOfElements(); i++){
            System.out.print(String.format("%10s", presenceVars[i].getBooleanValue()));
        }
        System.out.println();

    }

    private void showMap() {
        for (int i = 0; i < getNumberOfRows(); i++) {
            for (int j = 0; j < getNumberOfCols(); j++) {
                int element = getElement(i, j);
                int tower = towerVars[element].getValue();
                int fool = foolVars[element].getValue();
                String value = "*";
                if(tower == 1 && fool == 1){
                    throw new ValueException("Cannot have a fool and a tower on the same spot!");
                }else if(tower == 1){
                    value = "T";
                }else if(fool == 1){
                    value = "F";
                }
                System.out.print(String.format("%2s", value));
            }
            System.out.println();
        }
    }

    private void postDifferentSpot() {
        for(int i = 0; i < getNumberOfElements(); i++){
            postDifferentVars(towerVars[i], foolVars[i]);
        }
    }

    private void postDifferentVars(BoolVar var1, BoolVar var2) {
        // var1 -> not var2
        model.or(model.arithm(var1, "=", 0), model.arithm(var2, "=", 0)).post();
    }

    private void postPresenceConstraints(){
        for(int i = 0; i < getNumberOfElements(); i++){
            // pi = (ti or fi)
            model.arithm(presenceVars[i], "=", model.or(towerVars[i], foolVars[i]).reify()).post();
        }
    }

    private void postPieceConstraints(BoolVar[] pieceVars, boolean[][] pieceAttacks, int sum){
        postSumConstraints(pieceVars, sum);
        // postIndependenceConstraints(pieceVars, pieceAttacks);
        postDominationConstraints(pieceVars, pieceAttacks);

    }

    public void postSumConstraints(BoolVar[] vars, int sum) {
        model.sum(vars, "=", sum).post();
    }

    private void postIndependenceConstraints(BoolVar[] pieceVars, boolean[][] pieceAttacks){
        postMainPieceConstraints(pieceVars, pieceAttacks, false);
    }

    private void postDominationConstraints(BoolVar[] pieceVars, boolean[][] pieceAttacks){
        postMainPieceConstraints(pieceVars, pieceAttacks, true);
    }

    private void postMainPieceConstraints(BoolVar[] pieceVars, boolean[][] pieceAttacks, boolean domination) {
        String attackOperation = domination ? "!=" : "=";
        for (int i = 0; i < getNumberOfElements(); i++) {
            for (int j = 0; j < getNumberOfElements(); j++) {
                if (i != j) {
                    model.or(model.arithm(pieceVars[i], "=", 0),
                            model.arithm(presenceVars[j], "=", 0),
                            model.arithm(model.boolVar(pieceAttacks[i][j]),  attackOperation, 0)).post();
                }
            }
        }
    }

    public int getNumberOfElements() {
        return n * n;
    }

    public int getElement(int i, int j) {
        return i * getNumberOfCols() + j;
    }

    public int getNumberOfRows() {
        return n;
    }

    public int getNumberOfCols() {
        return n;
    }

    public static void main(String[] args) {
        Chess solver = new Chess(2, 2, 1, 1, false);
    }


}
