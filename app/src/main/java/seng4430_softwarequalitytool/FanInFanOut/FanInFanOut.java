package seng4430_softwarequalitytool.FanInFanOut;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

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
    }

    @Override
    public String compute(List<CompilationUnit> compilationUnits) {
        try {
            // Calculate
            calculateFanInFanOut(compilationUnits);

            // Print report
            printModuleHeader();
            printContent();
            printInformation();
            saveResult();

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
        System.out.format("%25s %8s %8s\n", "Function Name", "Fan-in", "Fan-out");
    }

    public void printContent() {
        for (String key : fanIns.keySet()) {
            System.out.format("%25s %8s %8s\n", key + "()", fanIns.get(key), fanOuts.get(key));
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

    private void evaluate() {

    }

    private void calculateFanInFanOut(List<CompilationUnit> compilationUnits) {
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
                    // Get method name
                    // TODO: Use fully qualified method name
                    String methodName = md.getNameAsString();

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
    private static Map<String, Integer> getFanIns(Map<String, List<String>> methodMap) {
        // Initialise FanIn map with same keys as methodMap
        Map<String, Integer> fanIns = new HashMap<>();
        for (String key : methodMap.keySet()) {
            fanIns.put(key, 0);
        }

        // Calculate fan-ins for every methods
        for (Map.Entry<String, List<String>> entry : methodMap.entrySet()) {
            for (String method : entry.getValue()) {
                if (!fanIns.containsKey(method)) continue;
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
    private static Map<String, Integer> getFanOuts(Map<String, List<String>> methodMap) {
        Map<String, Integer> fanOuts = new HashMap<>();
        for (Map.Entry<String, List<String>> entry : methodMap.entrySet()) {
            fanOuts.put(entry.getKey(), entry.getValue().size());
        }
        return fanOuts;
    }

    private static class MethodCallVisitor extends VoidVisitorAdapter<List<String>> {
        @Override
        public void visit(MethodCallExpr methodCallExpr, List<String> collector) {
            // TODO: Get fully qualified method name from method call expression
            collector.add(methodCallExpr.getNameAsString());
            super.visit(methodCallExpr, collector);
        }
    }
}
