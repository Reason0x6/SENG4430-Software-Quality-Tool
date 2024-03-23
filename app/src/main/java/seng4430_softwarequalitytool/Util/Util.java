package seng4430_softwarequalitytool.Util;

import com.github.javaparser.ast.CompilationUnit;
import seng4430_softwarequalitytool.CyclomaticComplexity.CyclomaticComplexity;
import seng4430_softwarequalitytool.NestedIfs.NestedIfs;
import seng4430_softwarequalitytool.RedundantCode.RedundantCode;

import java.util.ArrayList;
import java.util.List;

public class Util {
    List<Module> modules = new ArrayList<>();

     public Util(){
        regesiterModules();
    }

    public  void regesiterModules() {
        // Register the modules
        // TODO: Register your modules here
        modules.add(new CyclomaticComplexity());
        modules.add(new NestedIfs());
        modules.add(new RedundantCode());
    }

    public  String sendCUToModules(List<CompilationUnit> compilationUnits) {
        // Send the compilation units to the modules
        StringBuilder result = new StringBuilder();
        for(Module module : modules){
           result.append(module.compute(compilationUnits)).append("\n");
        }
        return result.toString();
    }



}