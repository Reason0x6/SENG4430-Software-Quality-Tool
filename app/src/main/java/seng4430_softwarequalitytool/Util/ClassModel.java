package seng4430_softwarequalitytool.Util;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.util.*;

import java.util.ArrayList;
import java.util.List;



public class ClassModel {
    public List<MethodModel> methods = new ArrayList<>();
    public int numberOfMethods;
    public String name;
    public HashMap<String, Integer> returnTypeDictionary = new HashMap<String, Integer>();
    public HashMap<String, Integer> parameterDictionary = new HashMap<String, Integer>();
    public HashMap<String, Integer> memberDictionary = new HashMap<String, Integer>();

    public ClassModel(ClassOrInterfaceDeclaration classDeclaration) {
        name = classDeclaration.getNameAsString();
        numberOfMethods = classDeclaration.getMethods().size();
        for (MethodDeclaration methodDeclaration :
                classDeclaration.getMethods()) {
            methods.add(new MethodModel(methodDeclaration));
        }
    }

    /**
     * uses compilation unit to build classmodel data
     * @param compilationUnits
     * @param classes
     * @return
     */
    public static void getClassData(List<CompilationUnit> compilationUnits, List<ClassModel> classes) {
        for (CompilationUnit compilationUnit :
                compilationUnits) {
            try {
                new VoidVisitorAdapter<List<ClassModel>>() {//how to pass data from the parser out
                    @Override
                    public void visit(ClassOrInterfaceDeclaration n, List<ClassModel> arg) {//overide method to build model
                        super.visit(n, arg);
                        //build list of class models from this compilation unit
                        arg.add(new ClassModel(n));
                    }
                }.visit(compilationUnit, classes);
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println(e.toString());
                new RuntimeException(e);
            }
        }
        
    }
    public static void put(Map<String, Set<String>> map, String key, String value) {
        Set<String> set = map.get(key);
        if (set == null) {
            set = new LinkedHashSet<>();
            map.put(key, set);
        }
        set.add(value);
    }
    public void findDependencies(List<CompilationUnit> compilationUnits, List<String> classNames) {
        for (CompilationUnit compilationUnit :
                compilationUnits) {
            try {
                new VoidVisitorAdapter<ClassModel>() {//how to pass data from the parser out
                    @Override
                    public void visit(ClassOrInterfaceDeclaration cd, ClassModel arg) {//overide method to build model
                        super.visit(cd, arg);
                        String cdName = cd.getNameAsString();
                        if (cdName.equals(arg.name)) return;
                        //check constructor for parameters of another type
                        parametersBuildConnectionsConstructor(cd, arg, cdName);
                        //System.out.println("MEMBERS__________________");
                        membersBuildConnections(cd, arg, cdName);
                        //System.out.println("METHODS__________________");
                        parametersAndReturnTypesBuildConnections(cd, arg, cdName);
                    }
                }.visit(compilationUnit, this);
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println(e.toString());
                new RuntimeException(e);
            }



        }
    }

    private void parametersAndReturnTypesBuildConnections(ClassOrInterfaceDeclaration cd, ClassModel arg, String cdName) {
        cd.getMethods().forEach(md -> {
            String returnType = md.getTypeAsString();
            //System.out.println(returnType);
            if (returnType.contains(arg.name)) {
                arg.returnTypeDictionary.put(cdName, arg.returnTypeDictionary.getOrDefault(cdName, 0) + 1);
            }
            NodeList<Parameter> parameters = md.getParameters();
            int i = 0;
            for (Parameter p :
                    parameters) {
                String parameterType = p.getTypeAsString();
                //System.out.println("p" + (++i) +" - " + parameterType);
                if (parameterType.contains(arg.name)) {
                    arg.parameterDictionary.put(cdName, arg.parameterDictionary.getOrDefault(cdName, 0) + 1);
                }
            }
        });
    }

    private void membersBuildConnections(ClassOrInterfaceDeclaration cd, ClassModel arg, String cdName) {
        cd.getFields().forEach(member -> {
            String memberType = member.getVariable(0).getTypeAsString();
            //System.out.println(memberType);
            if (memberType.contains(arg.name)) {
                arg.memberDictionary.put(cdName, arg.memberDictionary.getOrDefault(cdName, 0) + 1);
            }
        });
    }

    private void parametersBuildConnectionsConstructor(ClassOrInterfaceDeclaration cd, ClassModel arg, String cdName) {
        cd.getMembers().forEach(m -> {
            if (m instanceof ConstructorDeclaration) {
                NodeList<Parameter> parameters = ((ConstructorDeclaration) m).getParameters();
                int i = 0;
                for (Parameter p :
                        parameters) {
                    String parameterType = p.getTypeAsString();
                    //System.out.println("p" + (++i) +" - " + parameterType);
                    if (parameterType.contains(arg.name)) {
                        arg.parameterDictionary.put(cdName, arg.parameterDictionary.getOrDefault(cdName, 0) + 1);
                    }
                }
            }
        });
    }
}
