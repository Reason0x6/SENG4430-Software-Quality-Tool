package seng4430_softwarequalitytool.CyclomaticComplexity;

import java.io.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import com.github.javaparser.ast.visitor.VoidVisitor;
import com.github.javaparser.utils.Pair;
import com.google.gson.Gson;
import seng4430_softwarequalitytool.Util.Module;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import seng4430_softwarequalitytool.Util.Util;

/**
 * This class implements the Module interface and provides methods to compute the cyclomatic complexity of Java source code.
 * It also provides a method to evaluate risk levels based on defined ranges given in risk_ranges.properties.
 * The cyclomatic complexity is a software metric used to indicate the complexity of a program.
 * It directly measures the number of linearly independent paths through a program's source code.
 * The class uses the JavaParser library to parse the Java source code and compute the cyclomatic complexity.
 *
 * @author G Austin
 * @studentID 3279166
 * @lastModified: 22/03/2024
 */
public class CyclomaticComplexity implements Module {

    /** Properties object to hold risk ranges for cyclomatic complexity. */
    private Properties properties;
    private List<CompilationUnit> CompilationUnits;
    private int complexity = 0;
    private final Map<String, List<Pair<String, Integer>>> complexityModel;
    private String finalResult;

    /**
     * Default constructor that loads the risk ranges from the default location.
     */
    public CyclomaticComplexity() {
        complexityModel = new HashMap<>();
        properties = new Properties();
        try {
            properties.load(new FileInputStream("src/main/resources/DefaultDefinitions/risk_ranges.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Constructor that loads the risk ranges from a specified location.
     *
     * @param location The file location of the properties file containing risk ranges.
     */
    public CyclomaticComplexity(String location) {
        complexityModel = new HashMap<>();
        properties = new Properties();
        try {
            properties.load(new FileInputStream(location));
        } catch (Exception e) {
            try {
                properties.load(new FileInputStream("src/main/resources/DefaultDefinitions/risk_ranges.properties"));
            } catch (Exception e1) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Computes the cyclomatic complexity for the provided compilation units.
     * It also prints the module header, information, saves the result and prints it to a file.
     *
     * @param compilationUnits List of CompilationUnit objects representing Java source files.
     * @param filePath Path to the file where the result will be printed.
     * @return a String message indicating the success or failure of the computation.
     */
    @Override
    public String compute(List<CompilationUnit> compilationUnits, String filePath) {
        this.CompilationUnits = compilationUnits;
        try{
            printModuleHeader();
            this.complexity = calculateCyclomaticComplexity(compilationUnits);

            printInformation();
            saveResult();
            printToFile(filePath,finalResult);
            return "Cyclomatic Complexity Successfully Calculated.";
        }catch(Exception e){
            return "Error Calculating Cyclomatic Complexity.";
        }

    }

    /**
     * Replaces a placeholder in the file with the JSON results.
     *
     * @param filePath Path to the file.
     * @param jsonResults JSON results.
     * @throws FileNotFoundException If the file is not found.
     */
    public void printToFile(String filePath, String jsonResults) throws FileNotFoundException {
        String find = "@@Cyclic Complex Response Here@@";
        Util util = new Util();
        util.fileFindAndReplace(filePath, find, jsonResults);
    }

    /**
     * Prints the module header to the console.
     */
    @Override
    public void printModuleHeader(){
        System.out.println("---- Cyclomatic Complexity Module ----");
    }

    /**
     * Prints the total cyclomatic complexity and the risk level to the console.
     */
    @Override
    public void printInformation(){
        List<Pair<String, Integer>> totalComplexity = new ArrayList<>();
        int complexityComputed = complexity/CompilationUnits.size();
        totalComplexity.add(new Pair<>(evaluateRisk(complexityComputed), complexityComputed));
        complexityModel.put("_Admin_Total", totalComplexity);

        System.out.println("Total Cyclomatic Complexity: " + complexityComputed);
        System.out.println("Risk Level: " + evaluateRisk(complexityComputed));

    }

    /**
     * Saves the result as a JSON string.
     */
    @Override
    public void saveResult(){
        Gson gson = new Gson();
        finalResult =  gson.toJson(complexityModel);
    }

    /**
 * Calculates the cyclomatic complexity for a list of CompilationUnit objects.
 * Each CompilationUnit object represents a Java source file.
 * The method iterates over each CompilationUnit, and for each one, it collects the class name and all method declarations.
 * It then calculates the cyclomatic complexity for each method and adds it to the total complexity.
 * The method also maintains a map (complexityModel) where the key is the class name and the value is a list of pairs.
 * Each pair contains a method name and its corresponding cyclomatic complexity.
 *
 * @param compilationUnits List of CompilationUnit objects representing Java source files.
 * @return The total cyclomatic complexity of all methods in all compilation units.
 */
public int calculateCyclomaticComplexity(List<CompilationUnit> compilationUnits) {

    AtomicInteger totalComplexity = new AtomicInteger();
    for (CompilationUnit cu : compilationUnits) {

        AtomicInteger partialComplexity = new AtomicInteger();

        // Collect class names
        List<String> classNameL = new ArrayList<>();
        VoidVisitor<List<String>> classNameVisitor = new ClassNameCollector();
        classNameVisitor.visit(cu, classNameL);
        String className = classNameL.get(0);

        // Find all method declarations in the class
        List<MethodDeclaration> methods = cu.findAll(MethodDeclaration.class);

        List<Pair<String, Integer>> methodComplexity = new ArrayList<>();
        methods.forEach(method -> {
            // Calculate cyclomatic complexity for each method
            int complex = calculateMethodComplexity(method);
            partialComplexity.addAndGet(complex);
            totalComplexity.addAndGet(complex);
            // Add method name and its cyclomatic complexity to the list
            methodComplexity.add(new Pair<>(method.getNameAsString(), complex));
        });

        // Add class name and its methods' complexities to the map
        complexityModel.put(className, methodComplexity);
    }
    return totalComplexity.get();
}

    /**
     * Calculates the cyclomatic complexity for a single method.
     *
     * @param method MethodDeclaration object representing the method.
     * @return The cyclomatic complexity score of the method.
     */
    private  int calculateMethodComplexity(MethodDeclaration method) {
        CyclomaticComplexityVisitor visitor = new CyclomaticComplexityVisitor();
        visitor.visit(method, null);
        int complexity = visitor.getComplexity();

        return complexity;
    }

    /**
     * Evaluates the risk level based on the cyclomatic complexity.
     * The risk levels are defined in a properties file with ranges as keys and risk levels as values.
     * The method iterates over the ranges, checks if the complexity falls within a range, and assigns the corresponding risk level.
     * If no range matches, the risk level is set to "Unknown".
     *
     * @param complexity The cyclomatic complexity of the code.
     * @return The risk level as a string.
     */
    public  String evaluateRisk(int complexity) {
        String riskLevel = "Unknown";
        for (String range : properties.stringPropertyNames()) {
            String[] limits = range.split("_");
            int lowerLimit = Integer.parseInt(limits[0]);
            int upperLimit = limits.length == 1 ? Integer.MAX_VALUE : Integer.parseInt(limits[1]);
            if (complexity >= lowerLimit && complexity <= upperLimit) {
                riskLevel = properties.getProperty(range);
                break;
            }
        }

        return riskLevel;
    }

}