package seng4430_softwarequalitytool.CredentialsInCode;

import java.util.Scanner;
import java.util.List;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.nio.file.Path;
import java.io.File;
import java.io.FileNotFoundException;

public class DirectoryScanner {
    private Path root;
    private List<File> files;
    private int currentFileIdx;

    private Scanner sc;

    public DirectoryScanner(Path root) {
        this.root = root;

        // Get list of files from the root of the project
        this.files = new ArrayList<>();
        scanForFiles(root.toFile(), files);

        if (files.size() == 0) {
            throw new IllegalArgumentException("The directory " + root.toString() + " does not contain a file.");
        }

        // Initialise the scanner
        this.currentFileIdx = 0;
        try {
            this.sc = new Scanner(files.get(currentFileIdx));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Recursively scans for files from given directory.
     * 
     * @param file  A file or directory.
     * @param files List of files found.
     */
    private void scanForFiles(File file, List<File> files) {
        if (file.isFile()) {
            files.add(file);
            return;
        }

        for (File f : file.listFiles()) {
            scanForFiles(f, files);
        }
    }

    public boolean hasNext() {
        if (sc.hasNext()) {
            return true;
        }
        // EOF, check if there is next file to read
        boolean nextFound = false;
        while (!nextFound || currentFileIdx < files.size()) {
            sc = getNextScanner();
            nextFound = sc.hasNext(); // Ignore empty files
        }
        return nextFound;
    }

    /**
     * Finds and returns the next complete token from this directory scanner.
     * 
     * @return the next token
     */
    public String next() {
        if (hasNext()) {
            return sc.next();
        }
        throw new NoSuchElementException();
    }

    private Scanner getNextScanner() {
        sc.close();
        currentFileIdx++;

        if (currentFileIdx >= files.size()) {
            // All files scanned
            throw new NoSuchElementException("All files scanned.");
        }

        try {
            return new Scanner(files.get(currentFileIdx));
        } catch (Exception e) {
            // Should not be reachable
            e.printStackTrace();
            return null;
        }
    }

    /* Getters */

    public Path getRoot() {
        return root;
    }

    public List<File> getFiles() {
        return files;
    }

    public int getCurrentFileIdx() {
        return currentFileIdx;
    }

    public File getCurrentFile() {
        return files.get(currentFileIdx);
    }
}
