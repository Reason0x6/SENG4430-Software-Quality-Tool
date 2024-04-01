package seng4430_softwarequalitytool.Util;

import com.github.javaparser.ast.CompilationUnit;

import java.util.List;

public interface Module {
     String compute(List<CompilationUnit> compilationUnits, String filePath);
     void printModuleHeader();
     void printInformation();
     void saveResult();
}