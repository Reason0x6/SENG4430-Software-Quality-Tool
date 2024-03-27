
package seng4430_softwarequalitytool;


import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.utils.Log;
import com.github.javaparser.utils.SourceRoot;
import seng4430_softwarequalitytool.Util.Util;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class App {
    public String getGreeting() {
        return "Hello SENG4430!";
    }

    public static void main(String[] args) throws IOException {
        System.out.println(new App().getGreeting());

        // JavaParser has a minimal logging class that normally logs nothing.
        // Let's ask it to write to standard out:
        Log.setAdapter(new Log.StandardOutStandardErrorAdapter());

        // SourceRoot is a tool that read and writes Java files from packages on a certain root directory.
        // In this case the root directory is found by taking the root from the current Maven module,
        // with src/main/resources appended.
        Path pathToSource = Paths.get("src/main/resources/Examples/SENG2200-A1-GAustin");

        SourceRoot sourceRoot = new SourceRoot(pathToSource);
        sourceRoot.tryToParse();
        List<CompilationUnit> compilations = sourceRoot.getCompilationUnits();


       // Send the compilation units to the modules
        // TODO: This is where we do our things
       Util util = new Util();
       util.sendCUToModules(compilations);

    }
}
