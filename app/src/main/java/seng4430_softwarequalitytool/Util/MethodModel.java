package seng4430_softwarequalitytool.Util;

import com.github.javaparser.ast.body.MethodDeclaration;

public class MethodModel {
    public String name;
    public String id;
    public int linesOfCode;
    public MethodModel(MethodDeclaration methodDeclarartion) {
        name = methodDeclarartion.getNameAsString();
        id = methodDeclarartion.getDeclarationAsString(false,false,true);
        linesOfCode = methodDeclarartion.getEnd().get().line - methodDeclarartion.getBegin().get().line - 1;
    }
}
