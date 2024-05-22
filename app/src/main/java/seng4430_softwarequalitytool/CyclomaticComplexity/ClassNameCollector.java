package seng4430_softwarequalitytool.CyclomaticComplexity;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.util.List;

/**
 * ClassNameCollector is a visitor class that collects the names of all classes and interfaces in a Java source file.
 * It extends the VoidVisitorAdapter class from the JavaParser library, which allows it to visit specific nodes in the AST (Abstract Syntax Tree) generated by JavaParser.
 */
public class ClassNameCollector extends VoidVisitorAdapter<List<String>> {

    /**
     * This method is called when the visitor encounters a ClassOrInterfaceDeclaration node in the AST.
     * It adds the name of the class or interface to the collector list.
     *
     * @param n The ClassOrInterfaceDeclaration node that is currently being visited.
     * @param collector A list of strings where the names of classes and interfaces are collected.
     */
    @Override
    public void visit(ClassOrInterfaceDeclaration n, List<String> collector) {
        super.visit(n, collector);
        collector.add(n.getNameAsString());
    }
}