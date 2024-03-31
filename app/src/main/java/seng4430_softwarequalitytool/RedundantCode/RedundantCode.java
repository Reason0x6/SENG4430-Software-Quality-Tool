package seng4430_softwarequalitytool.RedundantCode;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.visitor.VoidVisitor;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import seng4430_softwarequalitytool.Util.Module;
import java.util.*;

public class RedundantCode implements Module {

    // Map to store the count of unreachable code lines for each method
    private final Map<String, Integer> unreachableCodeCounts = new HashMap<>();
    // Map to store the count of duplicated code occurrences for each method
    private final Map<String, Integer> duplicatedCodeCounts = new HashMap<>();
    // Map to store the count of unused code occurrences for each method
    // Set to store the names of unique methods identified during analysis
    private final Set<String> uniqueMethodNames = new HashSet<>();
    // Map to store the method names found during analysis along with the context of their usage
    private final Map<String, List<String>> foundMethodNames = new HashMap<>();
    // Map to store the current set of unused variables identified in methods
    private final Map<String, String> currentUnusedVariables = new HashMap<>();

    /**
     * Computes various metrics of redundant code including unreachable code, duplicated code,
     * and unused code for each provided CompilationUnit (module).
     * Updates internal maps with counts for each type of redundant code for every method.
     * It then triggers methods to print detailed information and save the results.
     *
     * @param compilationUnits A list of CompilationUnit objects to be analysed.
     * @return An empty string on successful execution or an error message if an exception occurs.
     */
    @Override
    public String compute(List<CompilationUnit> compilationUnits) {
        try {
            // Set to store redundant code occurrences
            Set<String> redundantCodeOccurrences = new HashSet<>();
            // Iterate over each CompilationUnit to check for redundant code
            for (CompilationUnit compilationUnit : compilationUnits) {
                // Check and mark unreachable code in the current compilation unit
                checkUnreachableCode(compilationUnit);
                // Check and mark duplicated code in the current compilation unit
                checkDuplicatedCode(compilationUnit, redundantCodeOccurrences);
                // Check and mark unused code in the current compilation unit
                checkUnusedCode(compilationUnit);
            }
            // Print the detailed information of code analysis
            printInformation();
            // Save the results of the code analysis
            saveResult();
            // Return an empty string to indicate successful completion
            return "";
        } catch (Exception e) {
            // Return an error message if an exception occurs during the analysis
            return "Error Calculating Redundant Code.";
        }
    }

    /**
     * Checks for unreachable code within each method of a given module.
     * Unreachable code is counted after a return statement within the same block.
     * The count for each method is stored in the unreachableCodeCounts map.
     * @param compilationUnit The CompilationUnit to analyse.
     * @return The total count of unreachable code lines in the CompilationUnit.
     */
    private int checkUnreachableCode(CompilationUnit compilationUnit) {
        int methodUnreachableCodeCount;
        // Iterate over method declarations in the modules
        for (MethodDeclaration methodDeclaration : compilationUnit.findAll(MethodDeclaration.class)) {
            methodUnreachableCodeCount = 0;
            Optional<BlockStmt> body = methodDeclaration.getBody();
            // Check if the method has a body to analyse
            if (body.isPresent()) {
                BlockStmt methodBody = body.get();
                NodeList<Statement> statements = methodBody.getStatements();
                boolean returnFound = false;
                // Iterate over statements in the method body
                for (Statement statement : statements) {
                    // Mark returnFound as true if a return statement is encountered
                    if (statement.isReturnStmt()) {
                        returnFound = true;
                    } else if (returnFound) {
                        // Increment the count if subsequent statements are found after a return statement
                        methodUnreachableCodeCount++;
                    }
                }
            }
            // Update the count for the current method in the map
            unreachableCodeCounts.put(methodDeclaration.getNameAsString(), methodUnreachableCodeCount);
        }
        // Return the sum of unreachable code lines found in all methods of the modules
        return unreachableCodeCounts.values().stream().mapToInt(Integer::intValue).sum();
    }

    private int checkDuplicatedCode(CompilationUnit compilationUnit, Set<String> redundantCodeOccurrences) {
        return 0;
    }

    /**
     * Analyses the provided CompilationUnit for unused code, including unused functions and variables.
     * It utilises two separate methods to identify unused functions and variables, updates the global
     * state with the findings, and removes identified used methods from the set of unique methods.
     * @param compilationUnit The CompilationUnit to analyse for unused code.
     */
    private void checkUnusedCode (CompilationUnit compilationUnit) {
        // Identify and record unused functions in the compilation unit
        unusedFunctions(compilationUnit);
        // Identify and record unused variables in the compilation unit
        Map<String, String> unusedVariables = unUsedVariables(compilationUnit);
        // Update the map of current unused variables with the newly identified unused variables
        currentUnusedVariables.putAll(unusedVariables);
        // Remove all found function calls from the allMethods list to get the unused functions
        uniqueMethodNames.removeAll(foundMethodNames.keySet());
    }

    /**
     * A custom visitor that collects method names and associates them with their respective classes.
     * It extends VoidVisitorAdapter to traverse AST nodes and operates on a map where each key is
     * a class name and each value is a list of method names within that class.
     */
    private static class MethodCollector extends VoidVisitorAdapter<Map<String, List<String>>> {
        /**
         * Visits each method declaration node in the AST, extracting the method's name and
         * the class it belongs to, then recording this information in the provided map.
         *
         * @param method The method declaration being visited.
         * @param map The map where the class and method names are recorded.
         */
        @Override
        public void visit(MethodDeclaration method, Map<String, List<String>> map) {
            // Invoke the parent class's visit method to continue the AST traversal
            super.visit(method, map);
            // Extract the name of the class or interface that contains this method
            String className = method.getParentNode().get()
                    .findFirst(com.github.javaparser.ast.body.ClassOrInterfaceDeclaration.class)
                    .get().getNameAsString();
            // Extract the method's name
            String methodName = method.getNameAsString();
            // Add the method name to the list of methods for its class in the map
            // If no entry exists for the class, create a new list and add the method name to it
            map.computeIfAbsent(className, k -> new ArrayList<>()).add(methodName);
        }
    }

    /**
     * Analyses the provided CompilationUnit for unused functions. It collects all method
     * declarations and method call expressions within each module.
     * Methods that are declared but not called are considered unused.
     * @param compilationUnit The CompilationUnit to analyse for unused functions.
     */
    private void unusedFunctions(CompilationUnit compilationUnit) {
        // Initialise a map to hold class methods mappings
        Map<String, List<String>> classMethodsMap = new HashMap<>();
        // Create and apply a method visitor to populate the classMethodsMap
        VoidVisitor<Map<String, List<String>>> methodVisitor = new MethodCollector();
        methodVisitor.visit(compilationUnit, classMethodsMap);
        // Add all methods found in the classMethodsMap to the global set of unique methods
        for (List<String> methods : classMethodsMap.values()) {
            uniqueMethodNames.addAll(methods);
        }
        // Visit all class or interface declarations in the module to collect called methods
        compilationUnit.accept(new VoidVisitorAdapter<Void>() {
            @Override
            public void visit(ClassOrInterfaceDeclaration classDecl, Void arg) {
                // Call the parent visit method to ensure proper traversal
                super.visit(classDecl, arg);
                // For each method call within the class, add its name to the foundMethodNames map
                classDecl.findAll(MethodCallExpr.class).forEach(methodCallExpr -> {
                    String methodName = methodCallExpr.getNameAsString();
                    foundMethodNames.computeIfAbsent(methodName, k -> new ArrayList<>()).add("occurrence");
                });
            }
        }, null);
    }

    /**
     * Analyses the provided module for unused variables within each method. It iterates
     * through all methods, checking each variable to see if it is used. Results are stored in a map
     * where each method's name is associated with a string detailing unused variables or indicating
     * that all variables are used.
     * @param compilationUnit The CompilationUnit to analyse for unused variables.
     * @return A map with method names as keys and details about unused variables as values.
     */
    private Map<String, String> unUsedVariables(CompilationUnit compilationUnit) {
        // Initialise a map to hold the unused variables count for each method
        Map<String, String> unusedCodeCounts = new HashMap<>();
        // Iterate over each method declaration within the module
        for (MethodDeclaration method : compilationUnit.findAll(MethodDeclaration.class)) {
            // List to store names of unused variables
            List<String> unusedVariableNames = new ArrayList<>();
            // Check each variable declarator within the method for usage
            for (VariableDeclarator variable : method.findAll(VariableDeclarator.class)) {
                // Determine if the variable is used in any expressions within the method
                boolean isUsed = method.findAll(NameExpr.class).stream()
                        .anyMatch(nameExpr -> nameExpr.getNameAsString().equals(variable.getNameAsString()));
                // If the variable is not used, add it to the list of unused variables
                if (!isUsed) {
                    unusedVariableNames.add(variable.getNameAsString());
                }
            }
            // Create a result string that either lists unused variables or states that all are used
            String result = unusedVariableNames.isEmpty()
                    ? "All used"
                    : "Unused Variable: " + String.join(", ", unusedVariableNames);
            // Map the method's name to the result string
            unusedCodeCounts.put(method.getNameAsString(), result);
        }
        // Return the map containing information about unused variables for each method
        return unusedCodeCounts;
    }

    /**
     * Prints the header for the code analysis module output.
     * This includes the column names for the function name and the counts of various code analysis metrics.
     */
    @Override
    public void printModuleHeader() {
        System.out.println("\n---- Code Analysis Module ----");
        System.out.format("%-30s %-30s %-20s %-20s %s\n", "Function Name", "Unreachable Code Count", "Duplicated Code", "Function Usage", "Variable Usage");
    }

    /**
     * Iterates through each method and prints out the associated counts of unreachable, duplicated, and unused code.
     * This method assumes all maps have the same keyset, which are the method names.
     */
    public void printInformation() {
        printModuleHeader();
        // Initialise counters for the totals
        int totalUnusedCode = 0;
        // Iterate through methods to display their information
        for (String methodName : unreachableCodeCounts.keySet()) {
            int unreachableCodeCount = unreachableCodeCounts.getOrDefault(methodName, 0);
            int duplicatedCodeCount = duplicatedCodeCounts.getOrDefault(methodName, 0);
            // Retrieve the used/unused status from unusedCodeCounts
            String usedStatus = uniqueMethodNames.contains(methodName) ? "Not used" : "used";
            String unusedVariables = currentUnusedVariables.getOrDefault(methodName, "");
            // Increment totalUnusedCode if the method is unused
            System.out.format("%-30s %-30d %-20d %-20s %s\n", methodName + "()", unreachableCodeCount, duplicatedCodeCount, usedStatus, unusedVariables);
        }
    }

    /**
     * Calculates and prints the total counts of unreachable code across all areas analysed.
     */
    @Override
    public void saveResult() {
        int totalUnreachableCode = unreachableCodeCounts.values().stream().mapToInt(Integer::intValue).sum();
        int totalDuplicatedCode = duplicatedCodeCounts.values().stream().mapToInt(Integer::intValue).sum();
        int totalUnusedFunctions = (int) uniqueMethodNames.stream().filter(name -> !foundMethodNames.containsKey(name)).count();
        int totalMethodsWithUnusedVariables = (int) currentUnusedVariables.values().stream()
                .filter(info -> !info.equals("All used"))
                .count();
        int totalRedundantCodeScore = totalUnreachableCode + totalDuplicatedCode + totalUnusedFunctions + totalMethodsWithUnusedVariables;
        System.out.println("Total unreachable code count: " + totalUnreachableCode);
        System.out.println("Total duplicated code count: " + totalDuplicatedCode);
        System.out.println("Total unused functions count: " + totalUnusedFunctions);
        System.out.println("Total methods with unused variables count: " + totalMethodsWithUnusedVariables);
        System.out.println("Total redundant code score: " + totalRedundantCodeScore);
    }
}
