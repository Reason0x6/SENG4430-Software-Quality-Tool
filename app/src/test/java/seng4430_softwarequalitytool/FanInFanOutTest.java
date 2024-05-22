package seng4430_softwarequalitytool;

import org.junit.jupiter.api.Test;

import com.github.javaparser.JavaParser;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JavaParserTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.MemoryTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;
import com.github.javaparser.utils.SourceRoot;

import seng4430_softwarequalitytool.BillOfMaterials.BillOfMaterials;
import seng4430_softwarequalitytool.CredentialsInCode.CredentialsInCode;
import seng4430_softwarequalitytool.FanInFanOut.FanInFanOut;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

public class FanInFanOutTest {
    @Test
    public void testCredentialsInCodeConstructor() {
        FanInFanOut fifo = new FanInFanOut();
        Properties properties = new Properties();
        try {
            properties.load(new FileInputStream("src/main/resources/DefaultDefinitions/fanin_fanout.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        int maxFanInValue = Integer.parseInt(properties.getProperty("max_fanin_value"));
        int maxFanOutValue = Integer.parseInt(properties.getProperty("max_fanout_value"));
        assertNotNull(fifo);
        assertTrue(fifo.getProperties().equals(properties));
    }

    @Test
    void testCalculateFanInFanOut() throws IOException {
        FanInFanOut fifo = new FanInFanOut();

        Path srcDir = Paths.get("src/test/resources/FanInFanOutTest");
        
        CombinedTypeSolver combinedTypeSolver = new CombinedTypeSolver();
        combinedTypeSolver.add(new ReflectionTypeSolver());
        combinedTypeSolver.add(new JavaParserTypeSolver(srcDir));
        JavaSymbolSolver symbolSolver = new JavaSymbolSolver(combinedTypeSolver);
        SourceRoot sourceRoot = new SourceRoot(srcDir);
        sourceRoot.getParserConfiguration().setSymbolResolver(symbolSolver); // Configure parser to use type resolution
        sourceRoot.tryToParse();
        List<CompilationUnit> compilationUnits = sourceRoot.getCompilationUnits();

        fifo.calculateFanInFanOut(compilationUnits);

        assertNotNull(fifo.getFanIns());
        assertTrue(fifo.getFanIns().keySet().contains("FanInFanOutTest.MyClass1.method1"));
        assertTrue(fifo.getFanIns().keySet().contains("FanInFanOutTest.MyClass1.method2"));
        assertTrue(fifo.getFanIns().keySet().contains("FanInFanOutTest.MyClass2.method1"));
        assertTrue(fifo.getFanIns().keySet().contains("FanInFanOutTest.MyClass2.method2"));

        assertNotNull(fifo.getFanOuts());
        assertTrue(fifo.getFanOuts().keySet().contains("FanInFanOutTest.MyClass1.method1"));
        assertTrue(fifo.getFanOuts().keySet().contains("FanInFanOutTest.MyClass1.method2"));
        assertTrue(fifo.getFanOuts().keySet().contains("FanInFanOutTest.MyClass2.method1"));
        assertTrue(fifo.getFanOuts().keySet().contains("FanInFanOutTest.MyClass2.method2"));
    }

    @Test
    void testGetFanIns() {
        Map<String, List<String>> methodMap = new HashMap<>();
        methodMap.put("FanInFanOutTest.MyClass1.method1", List.of("java.io.PrintStream.println", "java.io.PrintStream.println"));
        methodMap.put("FanInFanOutTest.MyClass1.method2", List.of("java.io.PrintStream.println"));
        methodMap.put("FanInFanOutTest.MyClass2.method1", List.of("FanInFanOutTest.MyClass1.method1"));
        methodMap.put("FanInFanOutTest.MyClass2.method2", List.of("FanInFanOutTest.MyClass1.method2"));

        Map<String, Integer> fanIns = FanInFanOut.getFanIns(methodMap);
        assertNotNull(fanIns);
        assertTrue(fanIns.get("FanInFanOutTest.MyClass1.method1") == 1);
        assertTrue(fanIns.get("FanInFanOutTest.MyClass1.method2") == 1);
        assertTrue(fanIns.get("FanInFanOutTest.MyClass2.method1") == 0);
        assertTrue(fanIns.get("FanInFanOutTest.MyClass2.method2") == 0);
    }

    @Test
    void testGetFanOuts() {
        Map<String, List<String>> methodMap = new HashMap<>();
        methodMap.put("FanInFanOutTest.MyClass1.method1", List.of("java.io.PrintStream.println", "java.io.PrintStream.println"));
        methodMap.put("FanInFanOutTest.MyClass1.method2", List.of("java.io.PrintStream.println"));
        methodMap.put("FanInFanOutTest.MyClass2.method1", List.of("FanInFanOutTest.MyClass1.method1"));
        methodMap.put("FanInFanOutTest.MyClass2.method2", List.of("FanInFanOutTest.MyClass1.method2"));

        Map<String, Integer> fanOuts = FanInFanOut.getFanOuts(methodMap);
        assertNotNull(fanOuts);
        assertTrue(fanOuts.get("FanInFanOutTest.MyClass1.method1") == 2);
        assertTrue(fanOuts.get("FanInFanOutTest.MyClass1.method2") == 1);
        assertTrue(fanOuts.get("FanInFanOutTest.MyClass2.method1") == 1);
        assertTrue(fanOuts.get("FanInFanOutTest.MyClass2.method2") == 1);
    }
}
