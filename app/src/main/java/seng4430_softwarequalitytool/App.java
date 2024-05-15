
package seng4430_softwarequalitytool;


import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.utils.SourceRoot;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JavaParserTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;
import seng4430_softwarequalitytool.Util.DisplayHandler;
import seng4430_softwarequalitytool.Util.Util;

import java.awt.*;
import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ArrayList;


public class App {


    public static void main(String[] args) throws IOException, InterruptedException {

        //launch JFrame for user input
        new DisplayHandler().createDisplay();

    }

    public static void introspectiveTest(String reportFilePath) throws IOException {
        System.out.println("Introspective Test Initiated");
        Path pathToSource = Paths.get("src/main/java/seng4430_softwarequalitytool");

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

    public static void exampleTest(String reportFilePath) throws IOException {
        System.out.println("Example Tests Initiated");

        Path pathToSource = Paths.get("src/main/resources/Examples/SENG2200-A1-GAustin");

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

    private static void scanForFolders(File file, List<File> folders) {
        if (file.isFile()) {
            return;
        }
        folders.add(file);
        for (File f : file.listFiles()) {
            scanForFolders(f, folders);
        }
    }


    public static File createFile() {

        // Get the current date and time
        LocalDateTime currentDateTime = LocalDateTime.now();

        // Define the format for the date and time `string`
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH_mm_ss");

        // Format the current date and time as a string
        String formattedDateTime = currentDateTime.format(formatter);

        String sourceFilePath = "src/main/resources/Report/Default_Report.html"; // Path to the source file
        String destinationFilePath = "src/main/resources/Report/Default_Report_" + formattedDateTime + ".html"; // Path for the duplicated file

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
