package ulb.infofond;

import jdk.nashorn.internal.runtime.regexp.joni.exception.ValueException;
import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.constraints.Constraint;
import org.chocosolver.solver.variables.BoolVar;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hboulahy on 20/05/17.
 */
public class Chess {

    private boolean domination;
    private int n, k1, k2, k3;
    private Model model;
    private BoolVar[] towerVars, foolVars, knightVars;
    private BoolVar[] presenceVars;
    private boolean[][] towerAttacks, foolAttacks, knightAttacks;

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
        this.knightVars = model.boolVarArray(getNumberOfElements());
        this.presenceVars = model.boolVarArray(getNumberOfElements());
        this.towerAttacks = AttacksFactory.towerAttacks(n, n);
        this.foolAttacks = AttacksFactory.foolAttacks(n, n);
        this.knightAttacks = AttacksFactory.knightAttacks(n, n);
        postConstraints();
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
        System.out.print(String.format("%10s", "TOWERS:"));
        for(int i = 0; i < getNumberOfElements(); i++){
            System.out.print(String.format("%7s",towerVars[i].getBooleanValue()));
        }
        System.out.println();
        System.out.print(String.format("%10s", "FOOLS:"));
        for(int i = 0; i < getNumberOfElements(); i++){
            System.out.print(String.format("%7s",foolVars[i].getBooleanValue()));
        }
        System.out.println();
        System.out.print(String.format("%10s", "KNIGHTS:"));
        for(int i = 0; i < getNumberOfElements(); i++){
            System.out.print(String.format("%7s", knightVars[i].getBooleanValue()));
        }
        System.out.println();
        System.out.print(String.format("%10s", "PRESENCE:"));
        for(int i = 0; i < getNumberOfElements(); i++){
            System.out.print(String.format("%7s", presenceVars[i].getBooleanValue()));
        }
        System.out.println();

    }

    private void showMap() {
        for (int i = 0; i < getNumberOfRows(); i++) {
            for (int j = 0; j < getNumberOfCols(); j++) {
                int element = getElement(i, j);
                int tower = towerVars[element].getValue();
                int fool = foolVars[element].getValue();
                int knight = knightVars[element].getValue();
                String value = "*";
                if(tower == 1 && fool == 1){
                    throw new ValueException("Cannot have a fool and a tower on the same spot!");
                }else if(tower == 1 && knight == 1){
                    throw new ValueException("Cannot have a tower and a knight on the same spot!");
                }else if(knight == 1 && fool == 1){
                    throw new ValueException("Cannot have a fool and a knight on the same spot!");
                }else if(tower == 1){
                    value = "T";
                }else if(fool == 1){
                    value = "F";
                }else if(knight == 1){
                    value = "C";
                }
                System.out.print(String.format("%2s", value));
            }
            System.out.println();
        }
    }

    private void postConstraints(){
        postPresenceConstraints();
        postDifferentSpot();
        postSumConstraints();
        if(this.domination){
            postDominationConstraints();
        }else{
            postIndependenceConstraints();
        }
    }

    private void postDifferentSpot() {
        for(int i = 0; i < getNumberOfElements(); i++){
            postDifferentVars(towerVars[i], foolVars[i]);
            postDifferentVars(towerVars[i], knightVars[i]);
            postDifferentVars(knightVars[i], foolVars[i]);
        }
    }

    private void postDifferentVars(BoolVar var1, BoolVar var2) {
        // var1 -> not var2
        model.or(model.arithm(var1, "=", 0), model.arithm(var2, "=", 0)).post();
    }

    private void postPresenceConstraints(){
        for(int i = 0; i < getNumberOfElements(); i++){
            // pi = (ti or fi)
            model.arithm(presenceVars[i], "=", model.or(towerVars[i], foolVars[i], knightVars[i]).reify()).post();
        }
    }

    private void postSumConstraints(){
        postSumConstraint(towerVars, k1);
        postSumConstraint(foolVars, k2);
        postSumConstraint(knightVars, k3);
    }

    private void postSumConstraint(BoolVar[] vars, int sum) {
        model.sum(vars, "=", sum).post();
    }

    private void postDominationConstraints(){
        for (int i = 0; i < getNumberOfElements(); i++) {
            List<Constraint> constraintList = new ArrayList<>();
            for (int j = 0; j < getNumberOfElements(); j++) {
                if (i != j) {
                    Constraint dominationPerJ = model.or(model.arithm(presenceVars[i], "=", 0),
                            buildAttackedByConstraint(towerVars, towerAttacks, i, j),
                            buildAttackedByConstraint(towerVars, towerAttacks, i, j),
                            buildAttackedByConstraint(knightVars, knightAttacks, i, j));
                    constraintList.add(dominationPerJ);
                }
            }
            Constraint[] constraints = new Constraint[constraintList.size()];
            constraintList.toArray(constraints);
            model.or(constraints).post();
        }
    }

    private Constraint buildAttackedByConstraint(BoolVar[] pieceVars, boolean[][] pieceAttacks, int i, int j){
        return model.and(model.arithm(pieceVars[j], "=", 1),
                model.arithm(model.boolVar(pieceAttacks[j][i]),  "=", 1));
    }

    private void postIndependenceConstraints(){
        postIndependenceConstraint(towerVars, towerAttacks);
        postIndependenceConstraint(foolVars, foolAttacks);
        postIndependenceConstraint(knightVars, knightAttacks);
    }
    private void postIndependenceConstraint(BoolVar[] pieceVars, boolean[][] pieceAttacks){
        String attackOperation = "=";
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
        Chess solver = new Chess(6, 4, 3, 1, false);
    }


}
