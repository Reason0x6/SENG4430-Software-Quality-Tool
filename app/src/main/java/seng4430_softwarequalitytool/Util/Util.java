package seng4430_softwarequalitytool.Util;

import com.github.javaparser.ast.CompilationUnit;
import seng4430_softwarequalitytool.CyclomaticComplexity.CyclomaticComplexity;
import seng4430_softwarequalitytool.FanInFanOut.FanInFanOut;
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
        modules.add(new FanInFanOut());
    }

    public void sendCUToModules(List<CompilationUnit> compilationUnits) {

        for(Module module : modules){
          module.compute(compilationUnits);
        }

    }



}