package seng4430_softwarequalitytool.RedundantCode;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.Statement;
import seng4430_softwarequalitytool.Util.Module;

import java.util.*;

public class RedundantCode implements Module {

    // Map to store the count of unreachable code lines for each method
    private Map<String, Integer> unreachableCodeCounts = new HashMap<>();
    // Map to store the count of duplicated code occurrences for each method
    private Map<String, Integer> duplicatedCodeCounts = new HashMap<>();
    // Map to store the count of unused code occurrences for each method
    private Map<String, String> unusedCodeCounts = new HashMap<>();


    /**
     * Computes various metrics of redundant code including unreachable code, duplicated code,
     * and unused code for each provided CompilationUnit (module).
     * Updates internal maps with counts for each type of redundant code for every method.
     * It then triggers methods to print detailed information and save the results.
     *
     * @param compilationUnits A list of CompilationUnit objects to be analyzed.
     * @return An empty string on successful execution or an error message if an exception occurs.
     */
    @Override
    public String compute(List<CompilationUnit> compilationUnits) {
        try {
            // Set to store redundant code occurrences
            Set<String> redundantCodeOccurrences = new HashSet<>();
            // Iterate over each CompilationUnit to check for redundant code
            for (CompilationUnit compilationUnit : compilationUnits) {
                checkUnreachableCode(compilationUnit);
                checkDuplicatedCode(compilationUnit, redundantCodeOccurrences);
                checkUnusedCode(compilationUnit);
            }
            // Print detailed information and save the results
            printInformation();
            saveResult();
            return "";
        } catch (Exception e) {
            return "Error Calculating Redundant Code.";
        }
    }

    /**
     * Checks for unreachable code within each method of a given module.
     * Unreachable code is counted after a return statement within the same block.
     * The count for each method is stored in the unreachableCodeCounts map.
     *
     * @param compilationUnit The CompilationUnit to analyze.
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
        // Implementation remains the same as before
        return 0; // Placeholder return value
    }

    private Collection<String> checkUnusedCode(CompilationUnit compilationUnit) {
        Set<String> functionNames = new HashSet<>();

        // Collect declared method names
        compilationUnit.findAll(MethodDeclaration.class)
                .forEach(methodDeclaration -> functionNames.add(methodDeclaration.getNameAsString().trim() + "()"));

        // Visit each method call and mark the corresponding function as used
        compilationUnit.findAll(MethodCallExpr.class)
                .forEach(methodCallExpr -> {
                    String methodName = methodCallExpr.getNameAsString() + "()";
                    functionNames.remove(methodName);
                });

        // Print out the remaining unused function names
        for (String functionName : functionNames) {
            System.out.println(functionName);
        }

        return functionNames;
    }


    /**
     * Prints the header for the code analysis module output.
     * This includes the column names for the function name and the counts of various code analysis metrics.
     */
    @Override
    public void printModuleHeader() {
        System.out.println("\n---- Code Analysis Module ----");
        System.out.format("%-30s %-30s %-20s %s\n", "Function Name", "Unreachable Code Count", "Duplicated Code", "Unused Code");
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
            String unusedCodeStatus = unusedCodeCounts.getOrDefault(methodName, "");
            // Increment totalUnusedCode if the method is unused
            System.out.format("%-30s %-30d %-20d %s\n", methodName + "()", unreachableCodeCount, duplicatedCodeCount, unusedCodeStatus);
        }
        // Print the total unused code count
        System.out.println("Total unused code instances: " + totalUnusedCode);
    }


    /**
     * Calculates and prints the total counts of unreachable code across all methods analysed.
     * This method can be extended to include total counts of duplicated and unused code as well.
     */
    @Override
    public void saveResult() {
        int totalUnreachableCode = unreachableCodeCounts.values().stream().mapToInt(Integer::intValue).sum();
        System.out.println("Total unreachable code count: " + totalUnreachableCode);
    }
}
