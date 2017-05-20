package ulb.infofond;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.variables.BoolVar;
import org.chocosolver.solver.variables.IntVar;

/**
 * Created by hboulahy on 20/05/17.
 */
public class Chess {

    private int n, k1, k2, k3;
    private Model model;
    private BoolVar[] towerVars;
    private BoolVar[] presenceVars;
    private boolean[][] towerAttacks;

    public Chess(int n, int k1, int k2, int k3, boolean domination) {
        this.n = n;
        this.k1 = k1;
        this.k2 = k2;
        this.k3 = k3;
        this.model = new Model("Chess Problem");
        this.towerVars = model.boolVarArray(getNumberOfElements());
        this.presenceVars = model.boolVarArray(getNumberOfElements());
        this.towerAttacks = AttacksFactory.towerAttacks(n, n);
        boolean[][] towerAttacks = AttacksFactory.towerAttacks(n, n);
        // model.arithm(totalVar, "=", k1).post();
        // model.count(1, towerVars, totalVar).post();
        Solver solver = model.getSolver();
        model.sum(towerVars, "=", k1).post();
        postTowerConstraints();
        // model.setObjective(false, totalVar);
        //while (solver.solve()) {
        solver.solve();
        System.out.println("Solution #" + solver.getSolutionCount());
        showMap();
        //}
    }

    private void showMap() {
        for (int i = 0; i < getNumberOfRows(); i++) {
            for (int j = 0; j < getNumberOfCols(); j++) {
                int element = getElement(i, j);
                System.out.print(towerVars[element].getValue());
            }
            System.out.println();
        }
    }

    private void postTowerConstraints() {
        for (int i = 0; i < getNumberOfElements(); i++) {
            for (int j = 0; j < getNumberOfElements(); j++) {
                if (i != j) {
                    model.or(model.arithm(towerVars[i], "=", 0),
                            model.arithm(presenceVars[j], "=", 0),
                            model.arithm(model.boolVar(towerAttacks[i][j]), "=", 0)).post();
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
        Chess solver = new Chess(4, 3, 3, 3, false);
    }


}
