package seng4430_softwarequalitytool.RedundantCode;

import java.io.*;
import java.util.*;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.stmt.Statement;
import com.google.gson.Gson;
import seng4430_softwarequalitytool.Util.Module;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.visitor.VoidVisitor;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;

public class RedundantCode implements Module {

    // Map to store the count of unreachable code lines for each method
    private final Map<String, Integer> unreachableCodeCounts = new HashMap<>();
    // Map to store the count of duplicated code occurrences for each method
    private final Map<String, Integer> duplicatedCodeCounts = new HashMap<>();
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
    public String compute(List<CompilationUnit> compilationUnits, String filePath) {
        try {
            // Set to store redundant code occurrences
            Set<String> redundantCodeOccurrences = new HashSet<>();
            // Iterate over each CompilationUnit to check for redundant code
            for (CompilationUnit compilationUnit : compilationUnits) {
                // Check and mark unreachable code in the current compilation unit
                checkUnreachableCode(compilationUnit);
                // Check and mark duplicated code in the current compilation unit
                checkDuplicatedCode(compilationUnit);
                // Check and mark unused code in the current compilation unit
                checkUnusedCode(compilationUnit);
            }
            // Print the detailed information of code analysis
            printInformation();
            // Save the results of the code analysis
            saveResult();
            // Create a summary object to store all the results
            Map<String, Object> analysisSummary = new HashMap<>();
            analysisSummary.put("unreachableCodeCounts", unreachableCodeCounts);
            analysisSummary.put("duplicatedCodeCounts", duplicatedCodeCounts);
            analysisSummary.put("uniqueMethodNames", uniqueMethodNames);
            analysisSummary.put("foundMethodNames", foundMethodNames);
            analysisSummary.put("currentUnusedVariables", currentUnusedVariables);

            int totalUnreachableCode = unreachableCodeCounts.values().stream().mapToInt(Integer::intValue).sum();
            int totalDuplicatedCode = duplicatedCodeCounts.values().stream().mapToInt(Integer::intValue).sum();
            int totalUnusedFunctions = (int) uniqueMethodNames.stream().filter(name -> !foundMethodNames.containsKey(name)).count();
            int totalMethodsWithUnusedVariables = (int) currentUnusedVariables.values().stream().filter(info -> !info.equals("All used")).count();
            int totalRedundantCodeScore = totalUnreachableCode + totalDuplicatedCode + totalUnusedFunctions + totalMethodsWithUnusedVariables;

            Map<String, Integer> totals = new HashMap<>();
            totals.put("totalUnreachableCode", totalUnreachableCode);
            totals.put("totalDuplicatedCode", totalDuplicatedCode);
            totals.put("totalUnusedFunctions", totalUnusedFunctions);
            totals.put("totalMethodsWithUnusedVariables", totalMethodsWithUnusedVariables);
            totals.put("totalRedundantCodeScore", totalRedundantCodeScore);
            analysisSummary.put("totals", totals);

            // Convert the summary object to JSON
            Gson gson = new Gson();
            String jsonResults = gson.toJson(analysisSummary);
            printToFile(filePath, jsonResults);
            // Return an empty string to indicate successful completion
            return "";
        } catch (Exception e) {
            printToFile(filePath, "{}");
            System.out.println("Error: " + e.getMessage());
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
    private int checkUnreachableCode (CompilationUnit compilationUnit) {
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

    /**
     * Checks for duplicated code within a given module and update the count of each duplicated method or variable
     * and increments their count in the global duplicatedCodeCounts map.
     * @param compilationUnit The compilation unit to be analyzed for duplicated code.
     */
    private void checkDuplicatedCode (CompilationUnit compilationUnit) {
        Set<String> methodsWithDuplicates = new HashSet<>();
        checkDuplicatedStatements(compilationUnit, methodsWithDuplicates);
        // Increment the count for each method in duplicatedCodeCounts
        for (String methodName : methodsWithDuplicates) {
            duplicatedCodeCounts.put(methodName, duplicatedCodeCounts.getOrDefault(methodName, 0) + 1);
        }
    }

    /**
     * Analyses a given module to identify duplicated statements within its methods.
     * It updates the provided set with the names of methods containing duplicated code fragments.
     * The method iterates over all method declarations within the module, examining each
     * statement in the method bodies. If a statement is found to be a duplicate (already seen in the
     * current module), the method's name is added to the methodsWithDuplicates set.
     * @param compilationUnit The module being analszed for duplicated statements.
     * @param methodsWithDuplicates A set that gets updated with names of methods containing duplicated statements.
     */
    private void checkDuplicatedStatements (CompilationUnit compilationUnit, Set<String> methodsWithDuplicates) {
        // Set to store unique code fragments
        Set<String> uniqueCodeFragments = new HashSet<>();
        // Visit each method declaration in the compilation unit
        for (MethodDeclaration method : compilationUnit.findAll(MethodDeclaration.class)) {
            String methodName = method.getNameAsString() + "()";
            BlockStmt body = method.getBody().orElse(null);
            if (body != null) {
                List<Statement> statements = body.getStatements();
                // Traverse through statements and identify code fragments
                for (Statement statement : statements) {
                    String codeFragment = statement.toString();
                    // Check if the current code fragment is unique
                    if (uniqueCodeFragments.contains(codeFragment)) {
                        // If it's not unique, add the method name to the set
                        methodsWithDuplicates.add(methodName);
                    } else {
                        // If it's unique, add it to the unique set
                        uniqueCodeFragments.add(codeFragment);
                    }
                }
            }
        }
    }

    /**
     * Analyses the provided CompilationUnit for unused code, including unused functions and variables.
     * It utilises two separate methods to identify unused functions and variables, updates the global
     * state with the findings, and removes identified used methods from the set of unique methods.
     *
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
         * @param map    The map where the class and method names are recorded.
         */
        @Override
        public void visit (MethodDeclaration method, Map<String, List<String>> map) {
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
     *
     * @param compilationUnit The CompilationUnit to analyse for unused functions.
     */
    private void unusedFunctions (CompilationUnit compilationUnit) {
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
     *
     * @param compilationUnit The CompilationUnit to analyse for unused variables.
     * @return A map with method names as keys and details about unused variables as values.
     */
    private Map<String, String> unUsedVariables (CompilationUnit compilationUnit) {
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
    public void printModuleHeader () {
        System.out.println("\n---- Code Analysis Module ----");
        System.out.format("%-30s %-30s %-20s %-20s %s\n", "Function Name", "Unreachable Code Count", "Duplicated code", "Function Usage", "Variable Usage");
    }

    /**
     * Iterates through each method and prints out the associated counts of unreachable, duplicated, and unused code.
     * This method assumes all maps have the same keyset, which are the method names.
     */
    public void printInformation () {
        printModuleHeader();
        // Iterate through methods to display their information
        for (String methodName : unreachableCodeCounts.keySet()) {
            int unreachableCodeCount = unreachableCodeCounts.getOrDefault(methodName, 0);
            String unreachableStatus = unreachableCodeCount == 0 ? "Reachable" : "Unreachable";

            // Get the duplication status for the current method
            int duplicateCodeCount = duplicatedCodeCounts.getOrDefault(methodName + "()", 0);
            String duplicateStatus = duplicateCodeCount == 0 ? "No" : "Yes";

            String usedStatus = uniqueMethodNames.contains(methodName) ? "Not used" : "Used";
            String unusedVariables = currentUnusedVariables.getOrDefault(methodName, "");
            System.out.format("%-30s %-30s %-20s %-20s %s\n",
                    methodName + "()",
                    unreachableStatus,
                    duplicateStatus,
                    usedStatus,
                    unusedVariables);
        }
    }

    /**
     * Calculates and prints the total counts of unreachable code across all areas analysed.
     */
    @Override
    public void saveResult () {
        int totalUnreachableCode = unreachableCodeCounts.values().stream().mapToInt(Integer::intValue).sum();
        int totalDuplicatedCode = duplicatedCodeCounts.values().stream().mapToInt(Integer::intValue).sum();
        int totalUnusedFunctions = (int) uniqueMethodNames.stream().filter(name -> !foundMethodNames.containsKey(name)).count();
        int totalMethodsWithUnusedVariables = (int) currentUnusedVariables.values().stream()
                .filter(info -> !info.equals("All used"))
                .count();
        int totalRedundantCodeScore = totalUnreachableCode + totalDuplicatedCode + totalUnusedFunctions + totalMethodsWithUnusedVariables;
        System.out.println("Total unreachable code: " + totalUnreachableCode);
        System.out.println("Total duplicated code: " + totalDuplicatedCode);
        System.out.println("Total unused functions: " + totalUnusedFunctions);
        System.out.println("Total methods with unused variables count: " + totalMethodsWithUnusedVariables);
        System.out.println("Total redundant code score: " + totalRedundantCodeScore);
    }

    private void printToFile(String filePath, String jsonResults) {
        String find = "@@Redudant code response here@@";
        System.out.println("Writing results to file: " + jsonResults);
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
            content = content.replaceAll(find, jsonResults);
            // Write modified content back to the file
            BufferedWriter writer = new BufferedWriter(new FileWriter(filePath));
            writer.write(content);
            writer.close();

        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

}
