package ulb.infofond;

import com.sun.xml.internal.bind.v2.runtime.reflect.opt.Const;
import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.constraints.Constraint;
import org.chocosolver.solver.variables.IntVar;

import java.util.*;

/**
 * Created by hboulahy on 20/05/17.
 */
@Deprecated
public class MuseumSolver {
    private static final int OBSTACLE_VALUE = 0, EMPTY_VALUE = 1, NORTH = 2, SOUTH = 3, EAST = 4, WEST = 5;
    private static final int[] VARS_DOMAIN = {OBSTACLE_VALUE, EMPTY_VALUE, NORTH, SOUTH, EAST, WEST};

    MuseumMap map;
    Model model;
    IntVar[][] vars;

    public MuseumSolver(String filename) {
        this.map = new MuseumParser(filename).getMuseumMap();
        this.model = new Model("MuseumSolver solver for " + filename);
        map.show();
        buildVars();
        buildConstraints();
        Solver solver = model.getSolver();
        // model.setObjective(false, numberOfLasersVar);
        while (solver.solve()) {
            solver.showShortStatistics();
        }
    }

    private void buildVars() {
        vars = new IntVar[map.getNumberOfRows()][map.getNumberOfCols()];
        for (int i = 0; i < map.getNumberOfRows(); i++) {
            for (int j = 0; j < map.getNumberOfCols(); j++) {
                vars[i][j] = model.intVar(VARS_DOMAIN);
            }
        }
    }

    private void buildConstraints() {
        for(int i = 0; i < map.getNumberOfRows(); i++){
            for (int j = 0; j < map.getNumberOfCols(); j++){
                buildConstraintsFor(i, j);
            }
        }
    }

    private void buildConstraintsFor(int i, int j) {
        Constraint emptyCst = model.arithm(vars[i][j], "=", EMPTY_VALUE);
        model.or(emptyCst, buildSouthInTopOfConstraints(i, j), buildNorthInTopOfConstraints(i, j),
                buildEastInTopOfConstraints(i, j), buildWestInTopOfConstraints(i, j)).post();
    }

    private Constraint buildSouthInTopOfConstraints(int i, int j) {
        List<Constraint> constraintList = new ArrayList<>();
        for(int k = i - 1; k > 0; k--){
            if(map.isEmpty(k, j)){
                constraintList.add(model.arithm(vars[k][j], "=", SOUTH));
            }else{
                break;
            }
        }
        Constraint[] constraints = new Constraint[constraintList.size()];
        constraintList.toArray(constraints);
        return constraintList.size() > 0 ? model.or(constraints) : model.or(model.boolVar(true));
    }

    private Constraint buildNorthInTopOfConstraints(int i, int j) {
        List<Constraint> constraintList = new ArrayList<>();
        for(int k = i + 1; k < map.getNumberOfRows(); k++){
            if(map.isEmpty(k, j)){
                constraintList.add(model.arithm(vars[k][j], "=", NORTH));
            }else{
                break;
            }
        }
        Constraint[] constraints = new Constraint[constraintList.size()];
        constraintList.toArray(constraints);
        return constraintList.size() > 0 ? model.or(constraints) : model.or(model.boolVar(true));
    }

    private Constraint buildEastInTopOfConstraints(int i, int j) {
        List<Constraint> constraintList = new ArrayList<>();
        for(int k = j - 1; k > 0; k--){
            if(map.isEmpty(i, k)){
                constraintList.add(model.arithm(vars[i][k], "=", EAST));
            }else{
                break;
            }
        }
        Constraint[] constraints = new Constraint[constraintList.size()];
        constraintList.toArray(constraints);
        return constraintList.size() > 0 ? model.or(constraints) : model.or(model.boolVar(true));
    }

    private Constraint buildWestInTopOfConstraints(int i, int j) {
        List<Constraint> constraintList = new ArrayList<>();
        for(int k = j + 1; k < map.getNumberOfCols(); k++){
            if(map.isEmpty(i, k)){
                constraintList.add(model.arithm(vars[i][k], "=", WEST));
            }else{
                break;
            }
        }
        Constraint[] constraints = new Constraint[constraintList.size()];
        constraintList.toArray(constraints);
        return constraintList.size() > 0 ? model.or(constraints) : model.or(model.boolVar(true));
    }


    public MuseumMap getMap() {
        return this.map;
    }

    public static void main(String[] args) {
        System.out.println("Starting MuseumSolver 2.0");
        String filename = "input/museum.txt";
        MuseumSolver solver = new MuseumSolver(filename);
    }

}
