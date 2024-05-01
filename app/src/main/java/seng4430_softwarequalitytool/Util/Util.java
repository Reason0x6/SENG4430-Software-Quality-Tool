package seng4430_softwarequalitytool.Util;

import com.github.javaparser.ast.CompilationUnit;
import seng4430_softwarequalitytool.CodeCommentsAndFormatting.CodeCommentsAndFormatting;
import seng4430_softwarequalitytool.CouplingBetweenClasses.CouplingBetweenClasses;
import seng4430_softwarequalitytool.CredentialsInCode.CredentialsInCode;
import seng4430_softwarequalitytool.CyclomaticComplexity.CyclomaticComplexity;
import seng4430_softwarequalitytool.DataValidation.BillOfMaterials;
import seng4430_softwarequalitytool.FanInFanOut.FanInFanOut;
import seng4430_softwarequalitytool.FogIndex.FogIndex;
import seng4430_softwarequalitytool.NestedIfs.NestedIfs;
import seng4430_softwarequalitytool.RedundantCode.RedundantCode;
import seng4430_softwarequalitytool.WeightedMethodsPerClass.WeightedMethodsPerClass;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class Util {
    List<Module> modules = new ArrayList<>();
    List<DSModule> dsModules = new ArrayList<>();

     public Util(){
         registerModules();
    }

    public  void registerModules() {
        // Register the modules
        // TODO: Register your modules here
        modules.add(new CyclomaticComplexity());
        modules.add(new BillOfMaterials());
        modules.add(new NestedIfs());
        modules.add(new RedundantCode());
        modules.add(new FogIndex());
        //modules.add(new LCOM());
        modules.add(new FanInFanOut());
        modules.add(new CouplingBetweenClasses());
        modules.add(new WeightedMethodsPerClass());
        modules.add(new CodeCommentsAndFormatting());

        dsModules.add(new CredentialsInCode());
    }

    public void sendCUToModules(List<CompilationUnit> compilationUnits, String filePath) {

        for(Module module : modules){
          module.compute(compilationUnits, filePath);
        }

    }

    public void computeDSModules(Path pathToSource, String reportFilePath) {
        try {
            DirectoryScanner ds = new DirectoryScanner(pathToSource);

            for (DSModule module : dsModules) {
                module.compute(ds, reportFilePath);
                ds.reset();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}