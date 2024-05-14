package seng4430_softwarequalitytool.LCOM;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.google.gson.Gson;
import seng4430_softwarequalitytool.Util.Module;

import com.github.javaparser.ast.body.MethodDeclaration;
import seng4430_softwarequalitytool.Util.Util;

import java.io.FileNotFoundException;
import java.util.*;

/**
 * Module to compute lack of cohesion in methods (LCOM) of Java source code.
 * Provides a method to calculate LCOM and evaluate ....
 * based on defined ranges given in ....
 *
 * @author I Andrews
 * @studentID c3204936
 * @lastModified: 27/03/2024
 */

public class LCOM implements Module {

    HashMap <String, Double> LCOMs = new HashMap<>();
    String filePath;
    @Override
    public void printModuleHeader() {
        System.out.println("\n");
        System.out.println("---- LCOM Module ----");
        System.out.format("%25s %s", "Function Name", "Lack of Cohesion in Methods (LCOM)\n");
        for(Map.Entry<String, Double> entry : LCOMs.entrySet()){
            System.out.println(entry.getKey() + ", " + entry.getValue());
        }
        System.out.println("-------------------------------------------------");
    }

    @Override
    public void printInformation() {
        System.out.println("---- Definitions Used ----");
        System.out.println("LCOM1 Calcuation: LCOM = P - Q");
        System.out.println("---- Results ----");
    }

    @Override
    public void saveResult() {
        Map<String, String> output = new HashMap<>();
        for(Map.Entry<String, Double> entry : LCOMs.entrySet()){

           output.put(entry.getKey(), entry.getValue().toString());
        }

        Gson json = new Gson();
        String jsonResults = json.toJson(output);
        try {
            printToFile( this.filePath, jsonResults, "@@LCOMSCORES@@" );
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

    }

    public void printToFile(String filePath, String jsonResults, String search) throws FileNotFoundException {
        String find = search;
        Util util = new Util();
        util.fileFindAndReplace(filePath, find, jsonResults);
    }


    @Override
    public String compute(List<CompilationUnit> compilationUnits, String filePath) {
        this.filePath = filePath;
        for(CompilationUnit cu : compilationUnits){
            // Visit and analyze each method declaration in the parsed file
            MethodVisitor methodVisitor = new MethodVisitor();
            methodVisitor.visit(cu, null);

        // Calculate LCOM for each class
            for (Map.Entry<String, Set<String>> entry : methodVisitor.classMethods.entrySet()) {
                String className = entry.getKey();
                Set<String> methods = entry.getValue();
                double lcom = calculateLCOM(methods, methodVisitor.methodFields);
                LCOMs.put(className, lcom);
            }
        }
        saveResult();
        return "LCOM Computed";
    }



    private static double calculateLCOM(Set<String> methods, Map<String, Set<String>> methodFields) {
        int totalReferences = 0;
        int fieldCount = 0;

        // Iterate through each field
        for (Set<String> fields : methodFields.values()) {
            fieldCount += fields.size();
            // Count the methods that reference each field
            for (String field : fields) {
                int references = 0;
                for (Set<String> methodSet : methodFields.values()) {
                    if (methodSet.contains(field)) {
                        references++;
                    }
                }
                totalReferences += references;
            }
        }

        // Calculate LCOM
        int methodCount = methods.size();
        int totalMethodFieldPairs = methodCount * fieldCount;
        double cohesion = 1.0 - ((double) totalReferences / totalMethodFieldPairs);
        return cohesion;
    }

    private static class MethodVisitor extends VoidVisitorAdapter<Void> {
        Map<String, Set<String>> methodFields = new HashMap<>();
        Map<String, Set<String>> classMethods = new HashMap<>();
        String currentClassName;

        public void visit(CompilationUnit n, Void arg) {
            super.visit(n, arg);
            List<MethodDeclaration> methodName = n.findAll(MethodDeclaration.class);
            for(MethodDeclaration method : methodName){
                Set<String> accessedFields = extractAccessedFields(method);
                methodFields.put(method.getNameAsString(), accessedFields);
                classMethods.computeIfAbsent(currentClassName, k -> new HashSet<>()).add(method.getNameAsString());
            }
        }

        private Set<String> extractAccessedFields(MethodDeclaration method) {

            Set<String> accessedFields = new HashSet<>();
            method.accept(new VoidVisitorAdapter<Void>() {
                @Override
                public void visit(FieldAccessExpr n, Void arg) {
                    super.visit(n, arg);
                    accessedFields.add(n.getNameAsString());
                }
                @Override
                public void visit(NameExpr n, Void arg) {
                    super.visit(n, arg);
                    accessedFields.add(n.getNameAsString());
                }
                @Override
                public void visit(VariableDeclarator n, Void arg) {
                    super.visit(n, arg);
                    accessedFields.add(n.getNameAsString());
                }
            }, null);
            return accessedFields;
        }

        @Override
        public void visit(com.github.javaparser.ast.body.ClassOrInterfaceDeclaration n, Void arg) {
            super.visit(n, arg);
            currentClassName = n.getNameAsString();
        }


    }
}
