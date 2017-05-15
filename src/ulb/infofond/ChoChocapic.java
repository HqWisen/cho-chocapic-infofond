package ulb.infofond;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.chocosolver.solver.Model;

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

	public static void main(String[] args) {
		ChoChocapic choco = new ChoChocapic(args);
		Model model = new Model("A first model");
		System.out.println(model.getName());
	}
}
