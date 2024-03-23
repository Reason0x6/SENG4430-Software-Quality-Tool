package seng4430_softwarequalitytool.CyclomaticComplexity;

import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.expr.ConditionalExpr;
import com.github.javaparser.ast.stmt.*;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

/**
 * A visitor used to calculate the cyclomatic complexity.
 * This class extends the javaparser Void Visitor in order to visit each node and increment the complexity ONLY when the node is of a control flow type
 * {@link com.github.javaparser.ast.visitor.VoidVisitorAdapter},
 *
 * Starting complexity is set to 1, as that represents a linear, not cyclomatic program.
 * as per {@link https://www.ibm.com/docs/en/raa/6.1?topic=metrics-cyclomatic-complexity}
 *  and {@link https://www.geeksforgeeks.org/cyclomatic-complexity/}
 *
 * @author G Austin
 * @studentID 3279166
 * @lastModified: 22/03/2024
 */
public class CyclomaticComplexityVisitor extends VoidVisitorAdapter<Void> {

    /** Current cyclomatic complexity value. Initialized value is always 1*/
    private int complexity = 1;

    public int getComplexity() {
        return complexity;
    }

    /**
     * Increments cyclomatic complexity count for each visited control flow statement.
     * 'for', 'foreach', 'while', 'do-while', 'switch', 'catch', conditional expressions & binary expressions (AND, OR, BINARY_AND, BINARY_OR, XOR),
     * complexity is incremented by a value of 1
     */
    @Override
    public void visit(ForStmt n, Void arg) {
        complexity++;
        super.visit(n, arg);
    }

    @Override
    public void visit(ForEachStmt n, Void arg) {
        complexity++;
        super.visit(n, arg);
    }

    @Override
    public void visit(WhileStmt n, Void arg) {
        complexity++;
        super.visit(n, arg);
    }

    @Override
    public void visit(DoStmt n, Void arg) {
        complexity++;
        super.visit(n, arg);
    }

    @Override
    public void visit(SwitchStmt n, Void arg) {
        complexity++;
        super.visit(n, arg);
    }

    @Override
    public void visit(CatchClause n, Void arg) {
        complexity++;
        super.visit(n, arg);
    }

    @Override
    public void visit(ConditionalExpr n, Void arg) {
        complexity++;
        super.visit(n, arg);
    }

    @Override
    public void visit(BinaryExpr n, Void arg) {
        switch (n.getOperator()) {
            case AND:
            case OR:
            case BINARY_AND:
            case BINARY_OR:
            case XOR:
                complexity++;
                break;
            default:
                break;
        }
        super.visit(n, arg);
    }

}