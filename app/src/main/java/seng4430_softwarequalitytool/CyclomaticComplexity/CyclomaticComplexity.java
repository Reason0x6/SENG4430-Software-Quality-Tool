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
 * Module to compute cyclomatic complexity of Java source code.
 * Provides a method to calculate cyclomatic complexity and evaluate risk levels
 * based on defined ranges given in risk_ranges.properties.
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
     * Constructs a CyclomaticComplexity object using default risk ranges.
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
     * Constructs a CyclomaticComplexity object using risk ranges from a specified location.
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

    public void printToFile(String filePath, String jsonResults) throws FileNotFoundException {
        String find = "@@Cyclic Complex Response Here@@";
        Util util = new Util();
        util.fileFindAndReplace(filePath, find, jsonResults);
    }


    @Override
    public void printModuleHeader(){
        System.out.println("---- Cyclomatic Complexity Module ----");
    }

    @Override
    public void printInformation(){
        List<Pair<String, Integer>> totalComplexity = new ArrayList<>();
        int complexityComputed = complexity/CompilationUnits.size();
        totalComplexity.add(new Pair<>(evaluateRisk(complexityComputed), complexityComputed));
        complexityModel.put("_Admin_Total", totalComplexity);

        System.out.println("Total Cyclomatic Complexity: " + complexityComputed);
        System.out.println("Risk Level: " + evaluateRisk(complexityComputed));

    }

    @Override
    public void saveResult(){
        Gson gson = new Gson();
        finalResult =  gson.toJson(complexityModel);
    }


    /**
     * Computes the complexity for compilation units provided in App.java.
     * receives the complexity score for each compilation unit and returns adds to the total complexity.
     *
     * @param compilationUnits List of CompilationUnit objects representing Java source files.
     * @return an int representing the total cyclomatic complexity of all compilation units.
     */
    public  int calculateCyclomaticComplexity(List<CompilationUnit> compilationUnits) {

        AtomicInteger totalComplexity = new AtomicInteger();
        for (CompilationUnit cu : compilationUnits) {

            AtomicInteger partialComplexity = new AtomicInteger();

            List<String> classNameL = new ArrayList<>();
            VoidVisitor<List<String>> classNameVisitor = new ClassNameCollector();

            classNameVisitor.visit(cu, classNameL);
            String className = classNameL.get(0);

            List<MethodDeclaration> methods = cu.findAll(MethodDeclaration.class);

            List<Pair<String, Integer>> methodComplexity = new ArrayList<>();
                    methods.forEach(method -> {
                        int complex = calculateMethodComplexity(method);
                        partialComplexity.addAndGet(complex);
                        totalComplexity.addAndGet(complex);
                        methodComplexity.add(new Pair<>(method.getNameAsString(), complex));
                    });

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


