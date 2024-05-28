package seng4430_softwarequalitytool.FanInFanOut;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Arrays;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.github.javaparser.resolution.UnsolvedSymbolException;
import com.github.javaparser.resolution.declarations.ResolvedMethodDeclaration;
import com.github.javaparser.resolution.types.ResolvedType;

import seng4430_softwarequalitytool.Util.HTMLTableBuilder;
import seng4430_softwarequalitytool.Util.Module;

/**
 * Prototype of fan-in and fan-out calculation module.
 * The module does not consider:
 * <ol>
 * <li>method overloading with different parameter types, and</li>
 * <li>same method names in different java classes.</li>
 * </ol>
 * 
 * Fan-in is the number of methods that call a particular method.
 * Fan-out is the number of methods that are called by a particular method.
 * 
 * @author Jiseok Yune
 * @studentID 3376644
 * @lastModified: 24/03/2024
 */
public class FanInFanOut implements Module {

    private Properties properties;
    private Map<String, List<String>> methodMap;
    private Map<String, Integer> fanIns;
    private Map<String, Integer> fanOuts;
    private Map<String, Integer> fanInsWarning;
    private Map<String, Integer> fanOutsWarning;
    private Map<String, List<Integer>> fanInFanOutWarning;

    public FanInFanOut() {
        properties = new Properties();
        try {
            properties.load(new FileInputStream("src/main/resources/DefaultDefinitions/fanin_fanout.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.methodMap = new HashMap<>();
        this.fanIns = new HashMap<>();
        this.fanOuts = new HashMap<>();
        this.fanInsWarning = new HashMap<>();
        this.fanOutsWarning = new HashMap<>();
        this.fanInFanOutWarning = new HashMap<>();
    }

    @Override
    public String compute(List<CompilationUnit> compilationUnits, String filePath) {
        try {
            // Calculate
            calculateFanInFanOut(compilationUnits);
            evaluate();

            // Print report
            printModuleHeader();
            printContent();
            printInformation();
            saveResult();
            printToFile(filePath);

            return "Fan-in & Fan-out Successfully Calculated.";
        } catch (Exception e) {
            e.printStackTrace();
            return "Error Calculating Fan-in and Fan-out.";
        }
    }

    @Override
    public void printModuleHeader() {
        System.out.println("\n");
        System.out.println("---- Fan-in & Fan-out Module ----");
        System.out.format("%-60s %8s %8s\n", "Function Name", "Fan-in", "Fan-out");
    }

    public void printContent() {
        for (String key : fanIns.keySet()) {
            System.out.format("%-60s %8s %8s\n", key + "()", fanIns.get(key), fanOuts.get(key));
        }
    }

    @Override
    public void printInformation() {
        System.out.println("---- Definitions Used ----");
        System.out.println(properties.toString());
        System.out.println("---- Results ----");
    }

    @Override
    public void saveResult() {
        System.out.println("Fan-in average: " + calculateAverage(fanIns.values()) +
                "\nFan-out average: " + calculateAverage(fanOuts.values()));
        System.out.println("Number of methods with high fan-in: " + fanInsWarning.size());
        System.out.println("Number of methods with high fan-out: " + fanOutsWarning.size());
    }

    private void printToFile(String reportFilePath) {
        String find = "<!------ @@Fan-in/Fan-out Output@@  ---->";

        try {
            // Read the content of the file
            BufferedReader reader = new BufferedReader(new FileReader(reportFilePath));
            StringBuilder contentBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                contentBuilder.append(line).append("\n");
            }
            reader.close();
            String content = contentBuilder.toString();

            // Build output to display
            StringBuilder sb = new StringBuilder();
            HTMLTableBuilder tableBuilder;

            // Evaluations - Fan-in
            sb.append("<h3>Fan-in</h3>");
            tableBuilder = new HTMLTableBuilder("", "Method Name", "Fan-in");
            for (String key : fanInsWarning.keySet()) {
                tableBuilder.addRow(key, fanInsWarning.get(key).toString());
            }
            if (!tableBuilder.isEmpty()) {
                sb.append(String.format("<p><b>%s methods has fan-in value higher than %s.</b></p>", fanInsWarning.size(), properties.getProperty("max_fanin_value")));
                sb.append("<p>A high fan-in value means that many other modules depend on the module in question, making it harder to change or refactor without potentially causing ripple effects throughout the codebase.</p>");    
                sb.append(tableBuilder.toString());
            } else {
                sb.append(String.format("<p><b>No methods with fan-in value higher than %s were found.</b></p>", properties.getProperty("max_fanin_value")));
                sb.append("<p>A high fan-in value means that many other modules depend on the module in question, making it harder to change or refactor without potentially causing ripple effects throughout the codebase.</p>");
            }
            
            // Evaluations - Fan-out
            sb.append("<h3>Fan-out</h3>");
            tableBuilder = new HTMLTableBuilder("", "Method Name", "Fan-out");
            for (String key : fanOutsWarning.keySet()) {
                tableBuilder.addRow(key, fanOutsWarning.get(key).toString());
            }
            if (!tableBuilder.isEmpty()) {
                sb.append(String.format("<p><b>%s methods has fan-out value higher than %s.</b></p>", fanOutsWarning.size(), properties.getProperty("max_fanout_value")));
                sb.append("<p>A high fan-out value means that the module in question depends on many other modules, potentially making it more difficult to understand, maintain, and test.</p>");
                sb.append(tableBuilder.toString());
            } else {
                sb.append(String.format("<p><b>No methods with fan-out value higher than %s were found.</b></p>", properties.getProperty("max_fanout_value")));
                sb.append("<p>A high fan-out value means that the module in question depends on many other modules, potentially making it more difficult to understand, maintain, and test.</p>");
            }

            // Evaluations - High Fan-in & Fan-out
            sb.append("<h3>High Fan-in & Fan-out</h3>");
            tableBuilder = new HTMLTableBuilder("", "Method Name", "Fan-in", "Fan-out");
            for (String key : fanInFanOutWarning.keySet()) {
                List<Integer> values = fanInFanOutWarning.get(key);
                tableBuilder.addRow(key, values.get(0).toString(), values.get(1).toString());
            }
            if (!tableBuilder.isEmpty()) {
                sb.append(String.format("<p><b>%s methods has both fan-in and fan-out value higher than their threshold.</b></p>", fanOutsWarning.size()));
                sb.append("<p>A high fan-in and fan-out value lead to significant maintainability issues in the codebase, as it is prone to errors whenever one of its dependencies changes, and the errors will propagate to many other parts of the codebase that rely on it.</p>");
                sb.append(tableBuilder.toString());
            } else {
                sb.append("<p><b>No method has a high value for both fan-in and fan-out.</b></p>");
                sb.append("<p>A high fan-in and fan-out value lead to significant maintainability issues in the codebase, as it is prone to errors whenever one of its dependencies changes, and the errors will propagate to many other parts of the codebase that rely on it.</p>");
            }
            
            // Table
            sb.append("<h3>All Methods</h3>");
            tableBuilder = new HTMLTableBuilder("", "Method Name", "Fan-in",
                    "Fan-out");
            for (String key : fanIns.keySet()) {
                tableBuilder.addRow(key, fanIns.get(key).toString(), fanOuts.get(key).toString());
            }
            sb.append(tableBuilder.toString());

            // Perform find and replace operation
            content = content.replaceAll(find, sb.toString());

            // Write modified content back to the file
            BufferedWriter writer = new BufferedWriter(new FileWriter(reportFilePath));
            writer.write(content);
            writer.close();

        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    /**
     * Calculates an average value from a collection of Integer.
     * From
     * https://stackoverflow.com/questions/10791568/calculating-average-of-an-array-list.
     * 
     * @param values
     * @return
     */
    private double calculateAverage(Collection<Integer> values) {
        return values.stream().mapToDouble(d -> d).average().orElse(0.0);
    }

    public void evaluate() {
        // check if there are methods that exceed max fan-in threshold
        for (String key : fanIns.keySet()) {
            boolean fanInWarning = false;
            boolean fanOutWarning = false;

            if (fanIns.get(key) > Integer.parseInt(properties.getProperty("max_fanin_value"))) {
                fanInsWarning.put(key, fanIns.get(key));
                fanInWarning = true;
            }

            if (fanOuts.get(key) > Integer.parseInt(properties.getProperty("max_fanout_value"))) {
                fanOutsWarning.put(key, fanOuts.get(key));
                fanOutWarning = true;
            }

            if (fanInWarning && fanOutWarning) {
                fanInFanOutWarning.put(key, Arrays.asList(fanIns.get(key), fanOuts.get(key)));
            }
        }
    }

    public void calculateFanInFanOut(List<CompilationUnit> compilationUnits) {
        methodMap.clear();
        fanIns.clear();
        fanOuts.clear();

        for (CompilationUnit cu : compilationUnits) {
            // Get top level classes/interfaces/enums/etc.
            NodeList<TypeDeclaration<?>> types = cu.getTypes();
            for (TypeDeclaration<?> td : types) {
                // Get methods from top level type.
                List<MethodDeclaration> mds = td.getMethods();
                for (MethodDeclaration md : mds) {
                    // skip empty methods
                    if (md.getBody().isEmpty())
                        continue;

                    // Get method name
                    // TODO: Use fully qualified method name
                    String methodName = getFullyQualifiedMethodName(cu, td, md);

                    // Get method calls inside the method
                    List<String> methodCalls = new ArrayList<>();
                    MethodCallVisitor methodCallVisitor = new MethodCallVisitor();
                    methodCallVisitor.visit(md.getBody().get(), methodCalls);

                    // Record method name and method invocations
                    if (methodMap.containsKey(methodName)) {
                        // multiple methods with same names
                    }
                    // Overwrite for now
                    methodMap.put(methodName, methodCalls);
                }
            }
        }

        fanIns = getFanIns(methodMap);
        fanOuts = getFanOuts(methodMap);
    }

    /**
     * Calculates the fan-in metric for each method in a given map.
     * Fan-in is the number of methods that call a particular method.
     * 
     * @param methodMap Map where the keys are the method names and the values are
     *                  list of methods called from the corresponding method.
     * @return Map where the keys are method names, and the values are the fan-in
     *         metric.
     */
    public static Map<String, Integer> getFanIns(Map<String, List<String>> methodMap) {
        // Initialise FanIn map with same keys as methodMap
        Map<String, Integer> fanIns = new HashMap<>();
        for (String key : methodMap.keySet()) {
            fanIns.put(key, 0);
        }

        // Calculate fan-ins for every methods
        for (Map.Entry<String, List<String>> entry : methodMap.entrySet()) {
            for (String method : entry.getValue()) {
                if (!fanIns.containsKey(method))
                    continue;
                fanIns.put(method, fanIns.get(method) + 1);
            }
        }

        return fanIns;
    }

    /**
     * Calculates the fan-out metric for each method in a given map.
     * Fan-out is the number of methods that are called by a particular method.
     * 
     * @param methodMap Map of method names and list of methods that are called from
     *                  the method.
     * @return A map where the keys are method names, and the values are the fan-in
     *         metric.
     */
    public static Map<String, Integer> getFanOuts(Map<String, List<String>> methodMap) {
        Map<String, Integer> fanOuts = new HashMap<>();
        for (Map.Entry<String, List<String>> entry : methodMap.entrySet()) {
            fanOuts.put(entry.getKey(), entry.getValue().size());
        }
        return fanOuts;
    }

    public String getFullyQualifiedMethodName(CompilationUnit cu, TypeDeclaration<?> td, MethodDeclaration md) {
        StringBuilder fullyQualifiedName = new StringBuilder();

        // Add the package name
        String packageName = cu.getPackageDeclaration().map(PackageDeclaration::getNameAsString).orElse("");
        if (!packageName.isEmpty()) {
            fullyQualifiedName.append(packageName).append(".");
        }

        // Add the class/interface/enum name
        String typeName = td.getNameAsString();
        fullyQualifiedName.append(typeName).append(".");

        // Add method name
        fullyQualifiedName.append(md.getNameAsString());

        return fullyQualifiedName.toString();
    }

    private static class MethodCallVisitor extends VoidVisitorAdapter<List<String>> {
        @Override
        public void visit(MethodCallExpr methodCallExpr, List<String> collector) {
            // TODO: Get fully qualified method name from method call expression
            Expression scope = methodCallExpr.getScope().orElse(null);
            System.out.println("Scope: " + scope);
            String qualifiedName = "";
            if (scope != null) {
                // Try to resolve the scope
                try {
                    System.out.println("bb");
                    ResolvedType resolvedType = scope.calculateResolvedType();
                    System.out.println("ss");
                    String resolvedScope = resolvedType.describe();
                    String methodName = methodCallExpr.getNameAsString();
                    qualifiedName = resolvedScope != "" ? resolvedScope + "." + methodName : methodName;
                } catch (UnsolvedSymbolException e) {
                    // Do nothing...
                } catch (IllegalArgumentException e) {
                    // If symbol solver cannot detect type declaration, e.g. Record
                    // Do nothing...
                }
            } else {
                // If scope is not found, try to resolve the method directly
                try {
                    ResolvedMethodDeclaration rmd = methodCallExpr.resolve();
                    qualifiedName = rmd.getQualifiedName();
                } catch (UnsolvedSymbolException e) {
                    // Do nothing...
                }
            }
            if (!qualifiedName.isEmpty()) {
                collector.add(qualifiedName);
            }
            super.visit(methodCallExpr, collector);
        }
    }

    public Map<String, List<String>> getMethodMap() {
        return methodMap;
    }

    public Map<String, Integer> getFanIns() {
        return fanIns;
    }

    public Map<String, Integer> getFanOuts() {
        return fanOuts;
    }

    public Properties getProperties() {
        return properties;
    }
}
