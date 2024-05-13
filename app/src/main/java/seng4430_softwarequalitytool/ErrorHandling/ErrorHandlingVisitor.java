package seng4430_softwarequalitytool.ErrorHandling;

import com.github.javaparser.ast.stmt.CatchClause;
import com.github.javaparser.ast.stmt.ThrowStmt;
import com.github.javaparser.ast.stmt.TryStmt;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.util.ArrayList;
import java.util.List;


public class ErrorHandlingVisitor extends VoidVisitorAdapter<Void> {

    private int tryCatchCount;

    private int throwCount;

    List<String> exceptionTypes = new ArrayList<>();

    public ErrorHandlingMetrics getMetrics() {
        return new ErrorHandlingMetrics(tryCatchCount, throwCount, exceptionTypes);
    }

    @Override
    public void visit(TryStmt n, Void arg) {
        tryCatchCount++;

        for (CatchClause catchClause : n.getCatchClauses()) {
            // get the exception type from the catch
            String exceptionType = catchClause.getParameter().getType().asString();

            // add it
            exceptionTypes.add(exceptionType);
        }

        super.visit(n, arg);
    }

    @Override
    public void visit(ThrowStmt n, Void arg) {
        throwCount++;
        super.visit(n, arg);
    }

}


