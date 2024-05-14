package seng4430_softwarequalitytool;

import org.junit.jupiter.api.Test;
import com.github.javaparser.ast.CompilationUnit;
import seng4430_softwarequalitytool.BillOfMaterials.BillOfMaterials;

import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class BillOfMaterialsTest {

    @Test
    public void testCheckForVulnerabilitiesInImports() {
        BillOfMaterials bom = new BillOfMaterials();

        // Prepare test compilation units with various import declarations
        CompilationUnit cu1 = new CompilationUnit();
        cu1.addImport("java.util.ArrayList");
        cu1.addImport("javax.swing.JFrame");
        cu1.addImport("com.example.CustomClass");

        List<CompilationUnit> compilationUnits = new ArrayList<>();
        compilationUnits.add(cu1);

        // Perform vulnerability check
        bom.checkForVulnerabilitiesInImports(compilationUnits);

        // Verify standard and non-standard imports
        assertTrue(bom.standardLibraries.contains("java.util.ArrayList"));
        assertTrue(bom.standardLibraries.contains("javax.swing.JFrame"));
        assertTrue(bom.nonStandardLibraries.contains("com.example.CustomClass"));
    }



    @Test
    public void testCheckForVulnerabilitiesInImports_NoImports() {
        BillOfMaterials bom = new BillOfMaterials();
        List<CompilationUnit> compilationUnits = new ArrayList<>();

        // Perform vulnerability check
        bom.checkForVulnerabilitiesInImports(compilationUnits);

        // Ensure no standard or non-standard imports are detected
        assertTrue(bom.standardLibraries.isEmpty());
        assertTrue(bom.nonStandardLibraries.isEmpty());
    }

    @Test
    public void testCheckForVulnerabilitiesInImports_OnlyStandardImports() {
        BillOfMaterials bom = new BillOfMaterials();
        CompilationUnit cu = new CompilationUnit();
        cu.addImport("java.util.ArrayList");
        cu.addImport("java.util.HashMap");
        List<CompilationUnit> compilationUnits = new ArrayList<>();
        compilationUnits.add(cu);

        // Perform vulnerability check
        bom.checkForVulnerabilitiesInImports(compilationUnits);

        // Ensure only standard imports are detected
        assertTrue(bom.standardLibraries.contains("java.util.ArrayList"));
        assertTrue(bom.standardLibraries.contains("java.util.HashMap"));
        assertTrue(bom.nonStandardLibraries.isEmpty());
    }

    @Test
    public void testCheckForVulnerabilitiesInImports_OnlyNonStandardImports() {
        BillOfMaterials bom = new BillOfMaterials();
        CompilationUnit cu = new CompilationUnit();
        cu.addImport("com.example.CustomClass");
        cu.addImport("org.example.ExternalLibrary");
        List<CompilationUnit> compilationUnits = new ArrayList<>();
        compilationUnits.add(cu);

        // Perform vulnerability check
        bom.checkForVulnerabilitiesInImports(compilationUnits);
        // Ensure only non-standard imports are detected
        assertTrue(bom.nonStandardLibraries.contains("com.example.CustomClass"));
        assertTrue(bom.nonStandardLibraries.contains("org.example.ExternalLibrary"));
        assertTrue(bom.standardLibraries.isEmpty());
    }

}
