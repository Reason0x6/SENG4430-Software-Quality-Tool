package seng4430_softwarequalitytool;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.utils.SourceRoot;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JavaParserTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;
import seng4430_softwarequalitytool.Util.DisplayHandler;
import seng4430_softwarequalitytool.Util.Util;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ArrayList;

/**
 * Main application class.
 */
public class App {

    /**
     * Main method of the application.
     * @param args Command line arguments.
     * @throws IOException If an I/O error occurs.
     * @throws InterruptedException If the thread is interrupted.
     */
    public static void main(String[] args) throws IOException, InterruptedException {

        // Create a new folder in app data if it doesnt exist
        String appDataPath = System.getenv("APPDATA");
        if (appDataPath == null) {
            appDataPath = System.getProperty("user.home");
        } else {
            appDataPath = appDataPath.replace("Roaming", "Local");
        }
        String folderName = "CodeProbe";
        Path folderPath = Paths.get(appDataPath, folderName);

        try {
            if (!Files.exists(folderPath)) {
                Files.createDirectories(folderPath);
            }
        } catch (Exception e) {
            System.err.println("Error creating directory: " + e.getMessage());
        }

        //launch JFrame for user input
        new DisplayHandler().createDisplay(folderPath);
    }

    /**
     * Runs introspective test.
     * @param reportFilePath Path to the report file.
     * @throws IOException If an I/O error occurs.
     */
    public static void introspectiveTest(String reportFilePath) throws IOException {
        System.out.println("Introspective Test Initiated");
        Path pathToSource = Paths.get("src/main/java/seng4430_softwarequalitytool");

        runCodeProbe(reportFilePath, pathToSource);
    }

    /**
     * Runs example test.
     * @param reportFilePath Path to the report file.
     * @throws IOException If an I/O error occurs.
     */
    public static void exampleTest(String reportFilePath) throws IOException {
        System.out.println("Example Tests Initiated");

        Path pathToSource = Paths.get("src/main/resources/Examples/SENG2200-A1-GAustin");

        // Set up type solver
        runCodeProbe(reportFilePath, pathToSource);
    }

    /**
     * Runs CodeProbe.
     * @param reportFilePath Path to the report file.
     * @param pathToSource Path to the source code.
     * @throws IOException If an I/O error occurs.
     */
    private static void runCodeProbe(String reportFilePath, Path pathToSource) throws IOException {
        // Set up type solver
        CombinedTypeSolver combinedTypeSolver = new CombinedTypeSolver();
        // Type solver for java modules
        combinedTypeSolver.add(new ReflectionTypeSolver());
        // Type solver for source project
        List<File> folders = new ArrayList<>();
        scanForFolders(pathToSource.toFile(), folders);
        for (File folder : folders) {
            combinedTypeSolver.add(new JavaParserTypeSolver(folder));
        }
        JavaSymbolSolver symbolSolver = new JavaSymbolSolver(combinedTypeSolver);

        SourceRoot sourceRoot = new SourceRoot(pathToSource);
        sourceRoot.getParserConfiguration().setSymbolResolver(symbolSolver); // Configure parser to use type resolution
        sourceRoot.tryToParse();
        List<CompilationUnit> compilations = sourceRoot.getCompilationUnits();

        Util util = new Util();
        util.sendCUToModules(compilations, reportFilePath);
        util.computeDSModules(pathToSource, reportFilePath);
    }

    /**
     * Runs general test.
     * @param reportFilePath Path to the report file.
     * @param sourceDirectory Source directory.
     * @throws IOException If an I/O error occurs.
     */
    public static void generalTest(String reportFilePath, String sourceDirectory) throws IOException {

        Path pathToSource = Paths.get(sourceDirectory);

        // Set up type solver
        CombinedTypeSolver combinedTypeSolver = new CombinedTypeSolver();
        // Type solver for java modules
        combinedTypeSolver.add(new ReflectionTypeSolver());
        // Type solver for source project
        List<File> folders = new ArrayList<>();
        scanForFolders(pathToSource.toFile(), folders);
        for (File folder : folders) {
            combinedTypeSolver.add(new JavaParserTypeSolver(folder));
        }
        JavaSymbolSolver symbolSolver = new JavaSymbolSolver(combinedTypeSolver);

        SourceRoot sourceRoot = new SourceRoot(pathToSource);
        sourceRoot.getParserConfiguration().setSymbolResolver(symbolSolver); // Configure parser to use type resolution
        sourceRoot.tryToParse();
        List<CompilationUnit> compilations = sourceRoot.getCompilationUnits();

        Util util = new Util();
        util.sendCUToModules(compilations, reportFilePath);
        util.computeDSModules(pathToSource, reportFilePath);
    }

    /**
     * Scans for folders.
     * @param file File to scan.
     * @param folders List of folders.
     */
    private static void scanForFolders(File file, List<File> folders) {
        if (file.isFile()) {
            return;
        }
        folders.add(file);
        for (File f : file.listFiles()) {
            scanForFolders(f, folders);
        }
    }

    /**
     * Creates the output report file.
     * @param folderPath Path to the folder.
     * @return The created file.
     */
    public static File createFile(String folderPath) {

        // Get the current date and time
        LocalDateTime currentDateTime = LocalDateTime.now();

        // Define the format for the date and time `string`
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH_mm_ss");

        // Format the current date and time as a string
        String formattedDateTime = currentDateTime.format(formatter);

        String sourceFilePath = "src/main/resources/Report/Default_Report.html"; // Path to the source file
        String destinationFilePath = folderPath + "/CodeProbe_Generated_Report_" + formattedDateTime + ".html"; // Path for the duplicated file

        File sourceFile = new File(sourceFilePath);
        File destFile = new File(destinationFilePath);

        FileInputStream fis = null;
        FileOutputStream fos = null;

        try {
            fis = new FileInputStream(sourceFile);
            fos = new FileOutputStream(destFile);

            byte[] buffer = new byte[1024];
            int length;
            while ((length = fis.read(buffer)) > 0) {
                fos.write(buffer, 0, length);
            }
            System.out.println("File copied successfully!");
        } catch (IOException e) {
            System.err.println("Error copying file: " + e.getMessage());
        } finally {
            try {
                if (fis != null) {
                    fis.close();
                }
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {
                System.err.println("Error closing streams: " + e.getMessage());
            }
        }
        return destFile;
    }
}