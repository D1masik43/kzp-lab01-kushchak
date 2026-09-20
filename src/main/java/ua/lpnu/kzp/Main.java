package ua.lpnu.kzp;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

/** Консольна програма обробки записів «Спортивна ліга» (варіант 17). */
public final class Main {

    private static final Path DEFAULT_INPUT = Path.of("data", "input.csv");
    private static final Path DEFAULT_OUTPUT = Path.of("out", "report.txt");

    /* Забороняє створення екземплярів службового класу. */
    private Main() {
    }

    /**
     * Точка входу до програми.
     *
     * @param args аргументи командного рядка: {@code --help}, {@code --version},
     *             {@code --input <файл>}, {@code --output <файл>}
     */
    public static void main(String[] args) {
        if (containsFlag(args, "--help")) {
            printUsage();
            return;
        }
        if (containsFlag(args, "--version")) {
            System.out.printf("lab01 %s%n", versionString());
            return;
        }

        Path input = resolveOption(args, "--input", DEFAULT_INPUT);
        Path output = resolveOption(args, "--output", DEFAULT_OUTPUT);

        List<String> lines;
        try {
            lines = FileReport.readLines(input);
        } catch (IOException exception) {
            System.out.printf("Не вдалося прочитати файл %s: %s%n", input, exception.getMessage());
            return;
        }

        LeagueProcessor.Result result = LeagueProcessor.process(lines);
        String report = LeagueProcessor.formatReport(result);

        System.out.print(report);

        try {
            FileReport.writeReport(output, report);
        } catch (IOException exception) {
            System.out.printf("Не вдалося записати звіт у %s: %s%n", output, exception.getMessage());
        }
    }

    private static void printUsage() {
        System.out.printf(
                "Використання: java -jar lab01.jar [--help] [--version] [--input <файл>] [--output <файл>]%n");
    }

    private static String versionString() {
        String version = Main.class.getPackage().getImplementationVersion();
        return version != null ? version : "1.0.0-dev";
    }

    private static boolean containsFlag(String[] args, String flag) {
        for (String arg : args) {
            if (flag.equals(arg)) {
                return true;
            }
        }
        return false;
    }

    private static Path resolveOption(String[] args, String flag, Path defaultValue) {
        for (int index = 0; index < args.length - 1; index++) {
            if (flag.equals(args[index])) {
                return Path.of(args[index + 1]);
            }
        }
        return defaultValue;
    }
}
