package seng4430_softwarequalitytool.BillOfMaterials;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import seng4430_softwarequalitytool.Util.ImportPrinter;
import seng4430_softwarequalitytool.Util.Module;
import seng4430_softwarequalitytool.Util.Util;

import java.io.*;
import java.text.Collator;
import java.util.Collection;
import java.util.List;
import java.util.TreeSet;

/**
 * Class representing a Bill of Materials.
 * Implements the Module interface.
 */
public class BillOfMaterials implements Module {

    public Collection<String> standardLibraries = new TreeSet<String>(Collator.getInstance());
    public Collection<String> nonStandardLibraries = new TreeSet<String>(Collator.getInstance());

    private StringBuilder htmlOutput = new StringBuilder();

    /**
     * Checks for vulnerabilities in import statements.
     * @param compilationUnits List of CompilationUnit objects.
     */
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

    /**
     * Checks if an import declaration is standard.
     * @param importDeclaration ImportDeclaration object.
     * @return boolean indicating if the import declaration is standard.
     */
    private  boolean isStandard(ImportDeclaration importDeclaration) {
        // Check if the import starts with "java." or "javax."
        String importPath = importDeclaration.getNameAsString();
        return importPath.startsWith("java.") || importPath.startsWith("javax.");
    }

    /**
     * Computes the Bill of Materials.
     * @param compilationUnits List of CompilationUnit objects.
     * @param filePath Path to the file.
     * @return String result.
     */
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

    /**
     * Prints the results to a file.
     * @param filePath Path to the file.
     * @param jsonResults JSON results.
     * @param search Search string.
     * @throws FileNotFoundException If the file is not found.
     */
    public void printToFile(String filePath, String jsonResults, String search) throws FileNotFoundException {
        String find = search;
        Util util = new Util();
        util.fileFindAndReplace(filePath, find, jsonResults);
    }

    /**
     * Module Stub Method
     */
    @Override
    public void printModuleHeader() {

    }

    /**
     * Module Stub Method
     */
    @Override
    public void printInformation() {

    }

    /**
     * Module Stub Method
     */
    @Override
    public void saveResult() {

    }

}