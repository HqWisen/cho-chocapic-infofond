package ulb.infofond;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solution;
import org.chocosolver.solver.variables.IntVar;

public class ChoChocapic {

	public ChoChocapic(String[] args) {
		parseArgs(args);
	}

	public void parseArgs(String[] args) {
		CommandLineParser commandLineParser = new DefaultParser();
		Options options = buildCommandLineOptions();
		try {
			CommandLine line = commandLineParser.parse(options, args);
		} catch (ParseException exp) {
			System.out.println("Unexpected exception:" + exp.getMessage());
		}

	}

	public static Options buildCommandLineOptions() {
		// TODO create real options
		Options options = new Options();
		options.addOption("a", "all", false, "do not hide entries starting with .");
		options.addOption("A", "almost-all", false, "do not list implied . and ..");
		options.addOption("b", "escape", false, "print octal escapes for nongraphic " + "characters");
		options.addOption("bs", "block-size", true, "Block size");
		options.addOption("B", "ignore-backups", false, "do not list implied entried " + "ending with ~");
		options.addOption("c", false,
				"with -lt: sort by, and show, ctime (time of last " + "modification of file status information) with "
						+ "-l:show ctime and sort by name otherwise: sort " + "by ctime");
		options.addOption("C", false, "list entries by columns");
		return options;

	}

	public static String fixedLength(String string) {
		return fixedLength(string, 3);
	}

	public static String fixedLength(String string, int length) {
		return String.format("%1$" + length + "s", string);
	}

	public static void main(String[] args) {
		// ChoChocapic choco = new ChoChocapic(args);
		int k1 = 4;
		int n = 5;
		Model model = new Model("n=" + n + ", k1=" + k1);
		IntVar[] trs = model.intVarArray("tr", k1, 0, n-1, false);
		IntVar[] tls = model.intVarArray("tl", k1, 0, n-1, false);
		for (int i = 0; i < k1; i++) {
			for (int j = 0; j < k1; j++) {
				if (i != j) {
					model.arithm(trs[i], "!=", trs[j]).post();
					model.arithm(tls[i], "!=", tls[j]).post();
					model.arithm(trs[i], "!=", tls[j]).post();
				}
			}
		}
		Solution solution = model.getSolver().findSolution();
		if (solution != null) {
			System.out.println("Solution" + solution.toString());
		} else {
			System.out.print("No solution");
		}
		String[][] board = new String[n][n];
		for (int i = 0; i < k1; i++) {
			board[trs[i].getValue()][tls[i].getValue()] = "T";
		}
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				if (board[i][j] != null) {
					System.out.print(fixedLength(board[i][j]));
				} else {
					System.out.print(fixedLength("*"));
				}
			}
			System.out.println();
		}

	}
}
