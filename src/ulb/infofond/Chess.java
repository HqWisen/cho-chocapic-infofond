package ulb.infofond;

import org.chocosolver.solver.Model;

import java.util.Arrays;

/**
 * Created by hboulahy on 20/05/17.
 */
public class Chess {

    Model model;

    public Chess(){
        this.model = new Model("Chess Problem");
        int n = 4;
        int m = 4;
        boolean[][] towerAttacks = AttacksFactory.towerAttacks(n, m);
        System.out.println(Arrays.toString(towerAttacks[15]));
    }

    public static void main(String[] args){
        Chess solver = new Chess();

    }
}
