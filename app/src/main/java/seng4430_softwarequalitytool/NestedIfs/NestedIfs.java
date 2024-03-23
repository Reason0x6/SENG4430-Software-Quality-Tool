    package seng4430_softwarequalitytool.NestedIfs;

    import seng4430_softwarequalitytool.Util.Module;
    import com.github.javaparser.ast.CompilationUnit;
    import com.github.javaparser.ast.Node;
    import com.github.javaparser.ast.body.MethodDeclaration;
    import com.github.javaparser.ast.stmt.IfStmt;

    import java.util.HashMap;
    import java.util.List;
    import java.util.Map;

    public class NestedIfs implements Module {

        private final Map<String, Integer> nestedIfScores;
        public NestedIfs () {
            nestedIfScores = new HashMap<>();
        }

        /**
         * Computes the maximum depth of nested 'if' statements across all provided project files.
         * @param compilationUnits A list of CompilationUnit objects to be analyzed.
         * @return A string summarising the maximum if nesting depth for each method in the compilation units.
         */
        @Override
        public String compute (List<CompilationUnit> compilationUnits) {
            try {
                for (CompilationUnit compilationUnit : compilationUnits) {
                    // Iterate through all method declarations in the compilation unit
                    compilationUnit.findAll(MethodDeclaration.class).forEach(methodDeclaration -> {
                        if (methodDeclaration.getBody().isPresent()) {
                            int maximumDepth = calculateMaxDepth(methodDeclaration.getBody().get(), 0);
                            // Adjust maximum depth for the initial level of 'if' statements
                            if (maximumDepth > 0) {
                                maximumDepth--;
                            }
                            // Store the nested if score for the method
                            nestedIfScores.put(methodDeclaration.getNameAsString(), maximumDepth);
                        }
                    });
                }
                // Print information
                printInformation();
                saveResult();
                return "Nested If's Successfully Calculated.";
            } catch(Exception e){
                return "Error Calculating Nested If statements.";
            }
        }

        /**
         * Prints the module header for the Nested If Module.
         * Prints a string representing the module header.
         */
        @Override
        public void printModuleHeader () {
            System.out.println("\n");
            System.out.println("---- Nested If Module ----");
            System.out.format("%25s %s", "Function Name", "Nested If's\n");
        }

        /**
         * Prints information about the nested if scores for each method.
         * Prints a string representing the printed information.
         */
        @Override
        public void printInformation () {
            printModuleHeader();
            for (Map.Entry<String, Integer> entry : nestedIfScores.entrySet()) {
                System.out.format("%25s %d\n", entry.getKey() + "()", entry.getValue());
            }
        }

        /**
         * Saves the total count of nested if scores and prints it.
         * Prints a string representing the saved result.
         */
        @Override
        public void saveResult () {
            int totalCount = 0;
            for (int value : nestedIfScores.values()) {
                totalCount += value;
            }
            System.out.println("Nested if's score: " + totalCount);
        }


        /**
         * Recursively calculates the maximum depth of nested 'if' statements within a node.
         * @param currentNode  The current node being analyzed.
         * @param currentDepth The current depth of 'if' statement nesting.
         * @return The maximum depth of nested 'if' statements found within the node.
         */
        private int calculateMaxDepth (Node currentNode, int currentDepth) {
            // Base case: If the node is not an IfStmt, traverse its children
            if (!(currentNode instanceof IfStmt ifStatement)) {
                int maxChildDepth = currentDepth;
                for (Node childNode : currentNode.getChildNodes()) {
                    int childDepth = calculateMaxDepth(childNode, currentDepth);
                    maxChildDepth = Math.max(maxChildDepth, childDepth);
                }
                return maxChildDepth;
            }
            // Processing IfStmt nodes
            // Calculate depth in the 'then' branch
            int thenBranchDepth = calculateMaxDepth(ifStatement.getThenStmt(), currentDepth + 1);
            int elseBranchDepth = currentDepth;
            // Process the 'else' branch, if present
            if (ifStatement.getElseStmt().isPresent()) {
                Node elseBranch = ifStatement.getElseStmt().get();
                // Differentiate between 'else if' and 'else' for depth calculation
                if (elseBranch instanceof IfStmt) {
                    // 'else if' should not increase the depth
                    elseBranchDepth = calculateMaxDepth(elseBranch, currentDepth);
                } else {
                    // 'else' block increases the depth
                    elseBranchDepth = calculateMaxDepth(elseBranch, currentDepth + 1);
                }
            }
            // Return the maximum depth found between the 'then' and 'else' branches
            return Math.max(thenBranchDepth, elseBranchDepth);
        }
    }