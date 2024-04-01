package seng4430_softwarequalitytool.CyclomaticComplexity;


import com.github.javaparser.ast.CompilationUnit;
import seng4430_softwarequalitytool.Util.Module;

import com.github.javaparser.ast.body.MethodDeclaration;

import java.io.*;
import java.util.List;

import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;


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

    private StringBuilder htmlOutput = new StringBuilder();

    /**
     * Constructs a CyclomaticComplexity object using default risk ranges.
     */
    public CyclomaticComplexity() {
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
        properties = new Properties();
        try {
            properties.load(new FileInputStream(location));
        } catch (IOException e) {
            e.printStackTrace();
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
            printToFile(filePath);
            return "Cyclomatic Complexity Successfully Calculated.";
        }catch(Exception e){
            return "Error Calculating Cyclomatic Complexity.";
        }

    }

    private void printToFile(String filePath) {
        String find = " <!------ @@Cyclomatic Output@@  ---->";

        try {
            // Read the content of the file
            BufferedReader reader = new BufferedReader(new FileReader(filePath));
            StringBuilder contentBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                contentBuilder.append(line).append("\n");
            }
            reader.close();
            String content = contentBuilder.toString();

            // Perform find and replace operation
            content = content.replaceAll(find, this.htmlOutput.toString());

            // Write modified content back to the file
            BufferedWriter writer = new BufferedWriter(new FileWriter(filePath));
            writer.write(content);
            writer.close();

        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }


    @Override
    public  void printModuleHeader(){
        System.out.println("---- Cyclomatic Complexity Module ----");
        this.htmlOutput.append("<div class=\"row\">");
    }


    @Override
    public void printInformation(){

        this.htmlOutput.append("<br/><br/><hr style=\"margin-top: 10px;\"/><b>Definitions Used</b><br/><code>" + properties.toString() + "</code>");
    }

    @Override
    public void saveResult(){

        this.htmlOutput.append("</div>");
        System.out.println("Avg Complexity Score: " + (this.complexity/this.CompilationUnits.size()) + "\nRisk: " + evaluateRisk( (this.complexity/this.CompilationUnits.size())));
        this.htmlOutput.append("<br/><p><b>Avg Complexity Score: </b>" + (this.complexity/this.CompilationUnits.size()) + "</p>")
                .append("<p><b>Risk: </b>" + evaluateRisk( (this.complexity/this.CompilationUnits.size())) + "</p>");
    }


    public void printRowInformation(String methodName, int complexity){

            this.htmlOutput.append("<tr>")
                    .append("<td>"+methodName+"()</td>")
                    .append("<td>"+complexity+"</td>")
                    .append("</tr>");
    }

    public void printClassInformation(String className, int methods){

        this.htmlOutput.append("<div class=\"col-sm-12\">");
        this.htmlOutput.append("<table class=\"table\">")
                .append("<thead class=\"thead-light\">")
                .append("<tr>")
                .append("<th scope=\"col\">"+className+" | Methods: " + methods + "</th>")
                .append("<th scope=\"col\">Complexity</th>")
                .append("</tr>")
                .append("</thead>")
                .append("<tbody>");

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
            String className = cu.getPrimaryTypeName().get();

            List<MethodDeclaration> methods = cu.findAll(MethodDeclaration.class);
            this.printClassInformation(className, methods.size());

            methods.forEach(method -> {
                int complex = calculateMethodComplexity(method);
                partialComplexity.addAndGet(complex);
                totalComplexity.addAndGet(complex);
            });

            System.out.println("Complexity for: " + className + "(): " + partialComplexity.get() + "\n");
            this.htmlOutput.append("</tbody>")
                    .append("</table>");
            this.htmlOutput.append("<b>Complexity for: </b>" + className + "() | " + partialComplexity.get());
            this.htmlOutput.append("</div>").append("<hr/>");
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
        printRowInformation(method.getNameAsString(), complexity);

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


