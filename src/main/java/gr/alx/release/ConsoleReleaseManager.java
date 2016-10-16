package gr.alx.release;

import jline.TerminalFactory;
import jline.console.ConsoleReader;
import jline.console.history.FileHistory;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Central point of the application which manages the whole release process.
 * Created by alx on 10/2/2016.
 */
@Slf4j
public class ConsoleReleaseManager extends CommonReleaseManager {

    private ConsoleReader console;

    /**
     * Initialisation constructor which initialise all dependent classes.
     */
    public ConsoleReleaseManager() {
        try {
            setUpConsole();

            printInConsole(getAsciiArt());
            initialiseManager();
        } catch (IOException e) {
            log.error("An error occurred while creating console", e);
        }
    }

    /**
     * This is the entry point method.
     *
     * @param args parameters (if any) passed by the user.
     */
    public void run(String... args) {
        try {
            printInConsole("\nPlease enter a release command:\n");
            console.setPrompt("$ ");
            String line;
            while ((line = console.readLine()) != null) {
                if ("quit".equalsIgnoreCase(line) || "exit".equalsIgnoreCase(line)) {
                    break;
                } else if (Arrays.asList(line.split(" ")).size() == MAX_COMMAND_LENGTH) {
                    doRelease(line);
                } else {
                    printInConsole(ALLOWED_ACTIONS_MESSAGE);
                }
            }
        } catch (IOException e) {
            log.error("An error occurred while running the release process", e);
        } finally {
            try {
                TerminalFactory.get().restore();
            } catch (Exception e) {
                log.error("An error occurred while finalising the release process.", e);
            }
        }
    }

    @Override
    protected void printInConsole(String writeMessage) {
        try {
            console.println(writeMessage);
        } catch (IOException e) {
            log.error("An error occurred while printing in the console the message: " + writeMessage, e);
        }

    }

    private void setUpConsole() throws IOException {
        console = new ConsoleReader();
        File historyFile = new File(".rmhistory");
        console.setHistory(new FileHistory(historyFile));
        console.setHistoryEnabled(true);
    }

    private String getAsciiArt() {
        InputStream is = getClass().getClassLoader().getResourceAsStream("asciiArt.txt");
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader br = new BufferedReader(isr);
        return br.lines().collect(Collectors.joining("\n"));
    }

}

