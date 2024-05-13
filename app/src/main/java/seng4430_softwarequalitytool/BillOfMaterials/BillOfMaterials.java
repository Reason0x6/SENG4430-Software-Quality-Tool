package seng4430_softwarequalitytool.BillOfMaterials;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.google.gson.Gson;
import seng4430_softwarequalitytool.Util.ImportPrinter;
import seng4430_softwarequalitytool.Util.Module;
import seng4430_softwarequalitytool.Util.Util;

import java.io.*;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.TreeSet;

public class BillOfMaterials implements Module {

    Collection<String> standardLibraries = new TreeSet<String>(Collator.getInstance());
    Collection<String> nonStandardLibraries = new TreeSet<String>(Collator.getInstance());

    private StringBuilder htmlOutput = new StringBuilder();


    // Function to check for vulnerabilities in import statements
    public void checkForVulnerabilitiesInImports(List<CompilationUnit> compilationUnits) {

        // Loop through each compilation unit
        for (CompilationUnit cu : compilationUnits) {
            // Get all import declarations in the compilation unit
            List<ImportDeclaration> importDeclarations = cu.findAll(ImportDeclaration.class);

            // Check each import declaration for potential vulnerabilities
            for (ImportDeclaration importDeclaration : importDeclarations) {
                if (isStandard(importDeclaration)) {
                    // Add vulnerability information to the list
                    standardLibraries.add(importDeclaration.getNameAsString());
                }else{
                    nonStandardLibraries.add(importDeclaration.getNameAsString());
                }
            }
        }

    }

    private  boolean isStandard(ImportDeclaration importDeclaration) {
        // Check if the import starts with "java." or "javax."
        String importPath = importDeclaration.getNameAsString();
        return importPath.startsWith("java.") || importPath.startsWith("javax.");
    }


    @Override
    public String compute(List<CompilationUnit> compilationUnits, String filePath) {
        checkForVulnerabilitiesInImports(compilationUnits);

        ImportPrinter iPrinter = new ImportPrinter();

        String standardLibrariesJson = iPrinter.importTreeToJson(standardLibraries);
        String nonStandardLibrariesJson = iPrinter.importTreeToJson(nonStandardLibraries);
        try{
            printToFile(filePath, standardLibrariesJson, "@@StdImport@@");
            printToFile(filePath, nonStandardLibrariesJson, "@@nonStdImport@@");
        }catch(FileNotFoundException e){}
        return "";
    }

    private void printToFile(String filePath, String jsonResults, String search) throws FileNotFoundException {
        String find = search;
        Util util = new Util();
        util.fileFindAndReplace(filePath, find, jsonResults);
    }


    @Override
    public void printModuleHeader() {

    }

    @Override
    public void printInformation() {

    }

    @Override
    public void saveResult() {

    }
}
