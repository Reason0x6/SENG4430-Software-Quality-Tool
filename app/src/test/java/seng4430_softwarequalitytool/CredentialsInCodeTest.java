package seng4430_softwarequalitytool;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import seng4430_softwarequalitytool.CredentialsInCode.Credential;
import seng4430_softwarequalitytool.CredentialsInCode.CredentialsInCode;
import seng4430_softwarequalitytool.Util.DirectoryScanner;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Arrays;
import static org.junit.jupiter.api.Assertions.*;

public class CredentialsInCodeTest {
    
    @Test
    public void testCredentialsInCodeConstructor() {
        CredentialsInCode cic = new CredentialsInCode();
        Properties properties = new Properties();
        try {
            properties.load(new FileInputStream("src/main/resources/DefaultDefinitions/credentials_in_code.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        double minEntropyRatio = Double.parseDouble(properties.getProperty("min_entropy"));
        assertNotNull(cic);
        assertTrue(cic.getCredentials().isEmpty());
        assertEquals(cic.getMinEntropyRatio(), minEntropyRatio);
    }

    @Test
    public void testScanCredentialsInCode() throws IOException {
        List<String> apiKeys = Arrays.asList(
            "jQzWtBpTgNUIRs6O8BVgkNb8I3tfdIIh",
            "5IwNfPHqf0MwSAowKFMdSIquYjV43eTg",
            "u0oSLSpnFdfk1DRazyAdJyeS5vMwiJ1K"
        );

        // Create a new CredentialsInCode object
        CredentialsInCode cic = new CredentialsInCode();
        
        DirectoryScanner ds = Mockito.mock(DirectoryScanner.class);
        when(ds.nextLine()).thenReturn(
            "api.key.service1=jQzWtBpTgNUIRs6O8BVgkNb8I3tfdIIh",
            "api.key.service2=5IwNfPHqf0MwSAowKFMdSIquYjV43eTg",
            "api.key.service3=u0oSLSpnFdfk1DRazyAdJyeS5vMwiJ1K",
            null
        );
        when(ds.getCurrentFile()).thenReturn(new File("test.txt"));
        when(ds.getLineNum()).thenReturn(1, 2, 3, -1);

        cic.scanCredentialsInCode(ds);
        assertFalse(cic.getCredentials().isEmpty());
        cic.getCredentials().forEach(cred -> {
            // Check if the Credential object is not null
            assertNotNull(cred);
            // Check if the Credential object's password is not null
            assertNotNull(cred.token());
            // Check if the Credential object's password is not empty
            assertFalse(cred.token().isEmpty());

            assertTrue(apiKeys.contains(cred.token()));
        });
    }

    @Test
    public void testScanCredentialsInCode_noCredentials() throws IOException {
        // Create a new CredentialsInCode object
        CredentialsInCode cic = new CredentialsInCode();
        
        DirectoryScanner ds = Mockito.mock(DirectoryScanner.class);
        when(ds.nextLine()).thenReturn(
            "api.key.service1=${SERVICE1_API_KEY}",
            "api.key.service2=${SERVICE2_API_KEY}",
            "api.key.service3=${SERVICE3_API_KEY}",
            null
        );
        when(ds.getCurrentFile()).thenReturn(new File("test.txt"));
        when(ds.getLineNum()).thenReturn(1, 2, 3, -1);

        cic.scanCredentialsInCode(ds);
        assertTrue(cic.getCredentials().isEmpty());
    }

    @Test
    public void testCalculateEntropy() {
        CredentialsInCode cic = new CredentialsInCode();
        String token = "jQzWtBpTgNUIRs6O8BVgkNb8I3tfdIIh";
        double expectedEntropy = 4.44;
        double calculatedEntropy = cic.calculateEntropy(token);
        assertTrue(Math.abs(calculatedEntropy - expectedEntropy) < 0.01);
    }

    @Test
    public void testGetIfAPIKey() {


        CredentialsInCode cic = new CredentialsInCode();
        
        DirectoryScanner ds = Mockito.mock(DirectoryScanner.class);
        when(ds.getCurrentFile()).thenReturn(new File("test.txt"));
        when(ds.getLineNum()).thenReturn(1, 2, -1);

        String highEntropyToken = "jQzWtBpTgNUIRs6O8BVgkNb8I3tfdIIh";
        String lowEntropyToken = "${SERVICE1_API_KEY}";
        
        assertTrue(cic.getIfAPIKey(ds, highEntropyToken, cic.getMinEntropyRatio()).isPresent());
        assertFalse(cic.getIfAPIKey(ds, lowEntropyToken, cic.getMinEntropyRatio()).isPresent());
    }
}