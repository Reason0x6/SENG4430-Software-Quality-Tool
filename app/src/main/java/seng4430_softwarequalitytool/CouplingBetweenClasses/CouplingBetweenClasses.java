package seng4430_softwarequalitytool.CouplingBetweenClasses;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import seng4430_softwarequalitytool.Util.ClassModel;
import seng4430_softwarequalitytool.Util.Module;

import java.util.ArrayList;
import java.util.List;

import static seng4430_softwarequalitytool.Util.ClassModel.getClassData;

/**
 * Fenton and Melton metric C(a,b) = i + n/(n+1)
 * where:
 * C(a,b) is the coupling index between module / classes
 * a and b, n is the number of dependencies and i is a score
 * of the tightest dependency from 0 to 5 (0 = lowest tightness).
 *
 * currently only n is considered, that is C(a,b) = n/(n+1)
 * to be added to later
 */
public class CouplingBetweenClasses implements Module {
    private List<ClassModel> classes = new ArrayList<>();

    @Override
    public String compute(List<CompilationUnit> compilationUnits) {
        return null;
    }

    @Override
    public String compute(List<CompilationUnit> compilationUnits, String filePath) {
        //build out models of classes and methods
        getClassData(compilationUnits, classes);
        List<String> classNames = new ArrayList<>();
        for (ClassModel c :
                classes) {
            classNames.add(c.name);
        }
        for (ClassModel c :
                classes) {
            c.findDependencies(compilationUnits, classNames);
        }


        return "\n***********************\n" + toString() + "\n***********************\n";
    }

    @Override
    public void printModuleHeader() {

    }

    @Override
    public void printInformation() {

    }

    @Override
    public void saveResult() {

    }

    @Override
    public String toString() {
        String result = "";

        for (int i = 0; i < classes.size(); i++) {
            for (int j = i + 1; j < classes.size(); j++) {
                ClassModel a = classes.get(i);
                ClassModel b = classes.get(j);
                int bUsingA = a.returnTypeDictionary.getOrDefault(b.name,0)
                        + a.parameterDictionary.getOrDefault(b.name,0)
                        + a.memberDictionary.getOrDefault(b.name,0);
                int aUsingB = b.returnTypeDictionary.getOrDefault(a.name,0)
                        + b.parameterDictionary.getOrDefault(a.name,0)
                        + b.memberDictionary.getOrDefault(a.name,0);
                if (bUsingA > 4) {
                    result += b.name + " uses " + a.name + ":\n";
                    result += a.returnTypeDictionary.getOrDefault(b.name,0) + " times as a return type\n";
                    result += a.parameterDictionary.getOrDefault(b.name,0) + " times as a parameter\n";
                    result += a.memberDictionary.getOrDefault(b.name,0) + " times as a instance member\n";
                }
                if (aUsingB > 4) {
                    result += a.name + " uses " + b.name + ":\n";
                    result += b.returnTypeDictionary.getOrDefault(a.name,0) + " times as a return type\n";
                    result += b.parameterDictionary.getOrDefault(a.name,0) + " times as a parameter\n";
                    result += b.memberDictionary.getOrDefault(a.name,0) + " times as a instance member\n";
                }
            }
        }
        return result;
    }
}
