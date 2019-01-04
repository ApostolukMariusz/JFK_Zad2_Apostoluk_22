package pl.edu.wat;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.DoStmt;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import javax.tools.*;
import java.io.*;
import java.util.*;

public class Main {

    public static void main(String[] args) throws IOException {
        final String fileName = "src\\Class.java";
        final String alteredFileName = "src\\ClassAltered.java";
        CompilationUnit cu;
        try(FileInputStream in = new FileInputStream(fileName)){
            cu = JavaParser.parse(in);
        }

        cu.getChildNodesByType(MethodCallExpr.class)
			.stream()
			.filter(m -> m.getNameAsString().equalsIgnoreCase("WHILE"))
			.forEach(Main::weaveLog);

        //new Rewriter().visit(cu, null);
        cu.getClassByName("Class").get().setName("ClassAltered");

        try(FileWriter output = new FileWriter(new File(alteredFileName), false)) {
            output.write(cu.toString());
        }

        File[] files = {new File(alteredFileName)};
        String[] options = { "-d", "out//production//Synthesis" };

        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();
        try (StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null)) {
            Iterable<? extends JavaFileObject> compilationUnits =
                fileManager.getJavaFileObjectsFromFiles(Arrays.asList(files));
            compiler.getTask(
                null,
                fileManager,
                diagnostics,
                Arrays.asList(options),
                null,
                compilationUnits).call();

            diagnostics.getDiagnostics().forEach(d -> System.out.println(d.getMessage(null)));
        }
    }

    private static DoStmt GetMethodStmt(MethodCallExpr method){

        List<Node> childList = method.getChildNodes();

        NameExpr nameExpr = new NameExpr(childList.get(1).toString());

        DoStmt dostatment = new DoStmt();

        dostatment.setCondition(nameExpr);
        dostatment.setBody(new BlockStmt());


        return dostatment;
    }

    private static void weaveLog(MethodCallExpr method) {
        DoStmt block = GetMethodStmt(method);
        method.getParentNode().get().replace(block);
    }

    private static class Rewriter extends VoidVisitorAdapter<Void> {
        @Override
        public void visit(MethodCallExpr n, Void arg) {
            String methodName = n.getNameAsString();
            if ("log".equalsIgnoreCase(methodName))
                return;

            DoStmt block = GetMethodStmt(n);

            Expression call = JavaParser.parseExpression("log(\"" + n.getNameAsString() + "\")");
            //block.addStatement(0, call);
        }
    }
}
