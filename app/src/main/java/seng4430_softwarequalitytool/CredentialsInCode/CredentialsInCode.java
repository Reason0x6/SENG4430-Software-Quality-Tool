package seng4430_softwarequalitytool.CredentialsInCode;

import java.io.FileInputStream;
import java.util.List;
import java.util.LinkedList;
import java.nio.file.Paths;
import java.util.Scanner;
import java.io.IOException;
import java.util.Optional;

import java.util.Properties;

import com.github.javaparser.ast.CompilationUnit;

import seng4430_softwarequalitytool.Util.Module;

/**
 * Checks for hardcoded passwords/API key
 */
public class CredentialsInCode implements Module {

    private DirectoryScanner ds;
    private Properties properties;
    private List<Credential> credentials;
    private double minEntropyRatio;

    public CredentialsInCode() {
        this.properties = new Properties();
        try {
            properties.load(new FileInputStream("src/main/resources/DefaultDefinitions/credentials_in_code.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.credentials = new LinkedList<Credential>();
        minEntropyRatio = Double.parseDouble(properties.getProperty("min_entropy_ratio"));
    }

    @Override
    public String compute(List<CompilationUnit> compilationUnits, String filePath) {
        try {
            scanCredentialsInCode(filePath);
            // Print report
            printModuleHeader();
            printContent();
            printInformation();
            saveResult();

            printToFile();

            return "Credentials in Code Successfully Scanned.";
        } catch (IOException e) {
            e.printStackTrace();
            return "Error Scanning for Credentials in Code.";
        }
    }

    private void scanCredentialsInCode(String filePath) throws IOException {
        ds = new DirectoryScanner(Paths.get(filePath));
        credentials.clear();

        String line;
        while ((line = ds.nextLine()) != null) {
            Scanner sc = new Scanner(line);
            while (sc.hasNext()) {
                Credential c = getIfAPIKey(sc.next(), minEntropyRatio).orElse(null);
                if (c != null) {
                    credentials.add(c);
                }
            }
            sc.close();
        }
    }

    @Override
    public void printModuleHeader() {
        System.out.println("\n");
        System.out.println("---- Credentials in Code Module ----");
        System.out.format("%-25s %8s %16s %8s\n", "File name", "Line", "Token", "Entropy Ratio");
    }

    private void printContent() {
        for (Credential c : credentials) {
            System.out.format("%-25s %8s %16s %8s\n", c.fileName(), c.lineNum(), c.token(), c.entropyRatio());
        }
    }

    @Override
    public void printInformation() {
        System.out.println("---- Definitions Used ----");
        System.out.println(properties.toString());
    }

    @Override
    public void saveResult() {
        System.out.println("---- Results ----");
        System.out.println("Number of potential credentials in code: " + credentials.size());
    }

    /**
     * TODO: implement print to file
     */
    private void printToFile() {

    }

    public Optional<Credential> getIfAPIKey(String token, double minEntropyRatio) {
        if (token.length() == 0)
            return Optional.empty();

        double entropyRatio = calculateEntropyRatio(token);
        if (entropyRatio > minEntropyRatio) {
            return Optional.of(new Credential(ds.getCurrentFile().toString(), ds.getLineNum(), token, entropyRatio));
        }

        return Optional.empty();
    }

    /**
     * String has high entropy ratio -> higher chance that string is an API key.
     * Algorithm from:
     * https://github.com/daylen/api-key-detect/blob/master/api_key_detect.py
     * 
     * @param token
     * @return
     */
    public double calculateEntropyRatio(String token) {
        if (token.length() == 0)
            return 0;
        //
        int entropy = 0;
        for (int i = 0; i < token.length() - 1; i++) {
            char c1 = token.charAt(i);
            char c2 = token.charAt(i + 1);
            if (Character.isUpperCase(c1) && Character.isUpperCase(c2) ||
                    Character.isLowerCase(c1) && Character.isLowerCase(c2) ||
                    Character.isDigit(c1) && Character.isDigit(c2)) {
                entropy++;
            }
        }
        return (float) entropy / token.length();
    }
}
