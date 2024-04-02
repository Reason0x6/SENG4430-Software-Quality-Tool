package seng4430_softwarequalitytool.CredentialsInCode;

import java.io.FileInputStream;
import java.util.List;
import java.util.LinkedList;
import java.util.Scanner;
import java.io.IOException;
import java.util.Optional;

import java.util.Properties;

import seng4430_softwarequalitytool.Util.DSModule;
import seng4430_softwarequalitytool.Util.DirectoryScanner;

/**
 * Checks for hardcoded passwords/API key
 */
public class CredentialsInCode implements DSModule {

    private DirectoryScanner ds;
    private Properties properties;
    private List<Credential> credentials;
    private double minEntropyRatio;

    public CredentialsInCode() {
        this.properties = new Properties();
        try {
            properties
                    .load(new FileInputStream("src/main/resources/DefaultDefinitions/credentials_in_code.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.credentials = new LinkedList<Credential>();
        minEntropyRatio = Double.parseDouble(properties.getProperty("min_entropy"));
    }

    @Override
    public String compute(DirectoryScanner ds, String filePath) {
        this.ds = ds;
        try {
            scanCredentialsInCode();
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

    private void scanCredentialsInCode() throws IOException {
        credentials.clear();

        String line;
        while ((line = ds.nextLine()) != null) {
            Scanner sc = new Scanner(line).useDelimiter("\\s+|=");
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
        System.out.format("%-75s %8s %50s %s\n", "File name", "Line", "Token", "Entropy");
    }

    private void printContent() {
        for (Credential c : credentials) {
            System.out.format("%-75s %8s %50s %.2f\n", c.fileName(), c.lineNum(), c.token(), c.entropyRatio());
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

    public Optional<Credential> getIfAPIKey(String token, double minEntropy) {
        if (token.length() == 0)
            return Optional.empty();

        double entropy = calculateEntropy(token);
        if (entropy > minEntropy) {
            return Optional.of(new Credential(ds.getCurrentFile().getPath(), ds.getLineNum(), token, entropy));
        }

        return Optional.empty();
    }

    /**
     * String has high entropy ratio -> higher chance that string is an API key.
     * Calculates the entropy using Shannon's entropy formula.
     * 
     * @param token
     * @return
     */
    public double calculateEntropy(String token) {
        // Convert token to byte array
        byte[] bytes = token.getBytes();

        // Count frequency of each character
        int[] frequencies = new int[256];
        for (byte b : bytes) {
            frequencies[b & 0xFF]++;
        }

        double entropy = 0;
        int totalCount = bytes.length;

        // Calculate the entropy using Shannon's entropy formula
        for (int freq : frequencies) {
            if (freq > 0) {
                double probability = (double) freq / totalCount;
                entropy -= probability * Math.log(probability) / Math.log(2);
            }
        }

        return entropy;
    }
}
