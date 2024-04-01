package seng4430_softwarequalitytool.RedundantCode;

import com.github.javaparser.ast.CompilationUnit;
import seng4430_softwarequalitytool.Util.Module;

import java.util.List;

public class RedundantCode implements Module {
    @Override
    public String compute(List<CompilationUnit> compilationUnits, String filePath) {
        return "Oh that code is redundant af";
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
}
