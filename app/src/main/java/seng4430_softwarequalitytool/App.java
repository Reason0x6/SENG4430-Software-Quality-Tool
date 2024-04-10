
package seng4430_softwarequalitytool;


import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.utils.Log;
import com.github.javaparser.utils.SourceRoot;
import seng4430_softwarequalitytool.Util.Util;

import java.awt.*;
import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class App {
    public String getGreeting() {
        return "SENG4430 Software Quality Tool.";
    }

    public static void main(String[] args) throws IOException {
        System.out.println(new App().getGreeting());

        // JavaParser has a minimal logging class that normally logs nothing.
        // Let's ask it to write to standard out:
        Log.setAdapter(new Log.StandardOutStandardErrorAdapter());

        // SourceRoot is a tool that read and writes Java files from packages on a certain root directory.
        // In this case the root directory is found by taking the root from the current Maven module,
        // with src/main/resources appended.
//        Path pathToSource = Paths.get("src/main/resources/Examples/SENG2200-A1-GAustin");
        Path pathToSource = Paths.get("src/main/java/seng4430_softwarequalitytool");

        File report = createFile();
        String reportFilePath = report.getAbsolutePath();
        SourceRoot sourceRoot = new SourceRoot(pathToSource);
        sourceRoot.tryToParse();
        List<CompilationUnit> compilations = sourceRoot.getCompilationUnits();

       // Send the compilation units to the modules
       // TODO: This is where we do our things
       Util util = new Util();
       util.sendCUToModules(compilations, reportFilePath);
       util.computeDSModules(pathToSource, reportFilePath);
        Desktop desktop = Desktop.getDesktop();

        desktop.open(report);

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
