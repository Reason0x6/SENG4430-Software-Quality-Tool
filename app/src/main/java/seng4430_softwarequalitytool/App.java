
package seng4430_softwarequalitytool;


import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.utils.Log;
import com.github.javaparser.utils.SourceRoot;
import seng4430_softwarequalitytool.Util.DisplayHandler;
import seng4430_softwarequalitytool.Util.Util;

import java.awt.*;
import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;

public class App {
    public String getGreeting() {
        return "SENG4430 Software Quality Tool.";
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        System.out.println(new App().getGreeting());


        // JavaParser has a minimal logging class that normally logs nothing.
        // Let's ask it to write to standard out:
        Log.setAdapter(new Log.StandardOutStandardErrorAdapter());

        //launch JFrame for user input
        DisplayHandler.createDisplay();

        File report = createFile();
        String reportFilePath = report.getAbsolutePath();

        Scanner scanner = new Scanner(System.in);
        try {
            System.out.println("Software Report Tool: ");
            System.out.println("Select Option (1) For Introspective Test");
            System.out.println("Select Option (2) For Example Test");
            System.out.println("Enter your choice (1 or 2): ");
            int choice = scanner.nextInt();

            switch (choice) {
                case 1:
                    introspectiveTest(reportFilePath);
                    break;
                case 2:
                    exampleTest(reportFilePath);
                    break;
                default:
                    System.out.println("Invalid choice!");
            }
        } catch (java.util.NoSuchElementException e) {
            System.out.println("Error: Input not found. Please provide valid input.");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        } finally {
            scanner.close(); // Close the scanner to release resources
        }

       Desktop desktop = Desktop.getDesktop();

        desktop.open(report);
        System.out.println("---------------------------------");
        System.out.println("-------- Report Completed -------");
        System.out.println("Output Report: " + reportFilePath);
        System.out.println("---------------------------------");
    }

    public static void introspectiveTest(String reportFilePath) throws IOException {
        System.out.println("Introspective Test Initiated");
        Path pathToSource = Paths.get("src/main/java/seng4430_softwarequalitytool");
        SourceRoot sourceRoot = new SourceRoot(pathToSource);
        sourceRoot.tryToParse();
        List<CompilationUnit> compilations = sourceRoot.getCompilationUnits();

        Util util = new Util();
        util.sendCUToModules(compilations, reportFilePath);
        util.computeDSModules(pathToSource, reportFilePath);
    }

    public static void exampleTest(String reportFilePath) throws IOException {
        System.out.println("Example Tests Initiated");

        Path pathToSource = Paths.get("src/main/resources/Examples/SENG2200-A1-GAustin");
        SourceRoot sourceRoot = new SourceRoot(pathToSource);
        sourceRoot.tryToParse();
        List<CompilationUnit> compilations = sourceRoot.getCompilationUnits();

        Util util = new Util();
        util.sendCUToModules(compilations, reportFilePath);
        util.computeDSModules(pathToSource, reportFilePath);
    }
    public static void generalTest(String reportFilePath, String sourceDirectory) throws IOException {

        Path pathToSource = Paths.get(sourceDirectory);
        SourceRoot sourceRoot = new SourceRoot(pathToSource);
        sourceRoot.tryToParse();
        List<CompilationUnit> compilations = sourceRoot.getCompilationUnits();

        Util util = new Util();
        util.sendCUToModules(compilations, reportFilePath);
        util.computeDSModules(pathToSource, reportFilePath);
    }




    public static File createFile() {

        // Get the current date and time
        LocalDateTime currentDateTime = LocalDateTime.now();

        // Define the format for the date and time string
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
