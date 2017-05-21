package ulb.infofond;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solution;
import org.chocosolver.solver.variables.IntVar;

public class ChessSolver {

    public static Options buildCommandLineOptions() {
        Options options = new Options();
        options.addOption("d", "domination", false, "Domination mode.");
        options.addOption("i", "independence", false, "Independence mode");
        options.addOption("n", "taille", true, "Chessboard size");
        options.addOption("t", "tour", true, "Tours");
        options.addOption("f", "fou", true, "Fous");
        options.addOption("c", "cavalier", true, "Cavaliers");
        return options;
    }

    public static String fixedLength(String string) {
        return fixedLength(string, 3);
    }

    public static String fixedLength(String string, int length) {
        return String.format("%1$" + length + "s", string);
    }

    private static int checkOption(String name, String value) throws ParseException {
        if (value == null) {
            throw new ParseException(String.format("Option '%s' is not set.", name));
        }
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new ParseException(String.format("Option '%s' has a non-correct value.", name));
        }
    }

    public static void main(String[] args) {
        CommandLineParser commandLineParser = new DefaultParser();
        Options options = buildCommandLineOptions();
        int n, k1, k2, k3;
        boolean domination, independence;
        try {
            CommandLine line = commandLineParser.parse(options, args);
            domination = line.hasOption("d");
            independence = line.hasOption("i");
            if (domination && independence) {
                throw new ParseException("Cannot use both mode at the same time.");
            } else if (!domination && !independence) {
                throw new ParseException("Choose a mode to run the Chess< solver.");
            }
            n = checkOption("n", line.getOptionValue("n"));
            k1 = checkOption("t", line.getOptionValue("t"));
            k2 = checkOption("f", line.getOptionValue("f"));
            k3 = checkOption("c", line.getOptionValue("c"));
            Chess chessSolver = new Chess(n, k1, k2, k3, domination);
            chessSolver.solve();
            chessSolver.showSolution();
        } catch (ParseException exp) {
            System.out.println("Option Error: " + exp.getMessage());
        }
    }

}
