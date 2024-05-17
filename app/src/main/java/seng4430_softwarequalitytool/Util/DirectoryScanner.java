package seng4430_softwarequalitytool.Util;

import java.util.List;
import java.util.ArrayList;
import java.nio.file.Path;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.LineNumberReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Optional;
import java.util.Arrays;

import com.google.common.io.Files;

import java.io.IOException;

public class DirectoryScanner {
    private Path root;
    private List<File> files;
    private int currentFileIdx;
    private static List<String> compatibleExtensions = Arrays.asList("java", "properties", "json");

    private LineNumberReader lr;

    public DirectoryScanner(Path root) throws FileNotFoundException {
        this.root = root;

        // Get list of files from the root of the project
        this.files = new ArrayList<>();
        scanForFiles(root.toFile(), files);

        if (files.size() == 0) {
            throw new IllegalArgumentException("The directory " + root.toString() + " does not contain a file.");
        }

        // Initialise the scanner
        this.currentFileIdx = 0;
        this.lr = new LineNumberReader(new FileReader(getCurrentFile()));
    }

    /**
     * Recursively scans for files from given directory.
     * 
     * @param file  A file or directory.
     * @param files List of files found.
     */
    private void scanForFiles(File file, List<File> files) {
        if (file.isFile()) {
            String fileExt = Files.getFileExtension(file.getName());
            if (compatibleExtensions.contains(fileExt)) {
                files.add(file);
            }
            return;
        }

        for (File f : file.listFiles()) {
            scanForFiles(f, files);
        }
    }

    public String nextLine() throws IOException {
        String next;
        try {
            if ((next = lr.readLine()) != null) {
                return next;
            }
        } catch (IOException e) {
            // skip current file
        }

        // EOF for current file, look for next file
        lr = getNextReader().orElse(null);

        if (lr == null) {
            // all files scanned
            return null;
        }
        
        return nextLine();
    }

    private Optional<LineNumberReader> getNextReader() throws IOException {
        lr.close();
        currentFileIdx++;
        
        if (currentFileIdx >= files.size()) {
            return Optional.empty();
        }
        return Optional.of(new LineNumberReader(new FileReader(getCurrentFile())));
    }

    public void reset() throws IOException, FileNotFoundException {
        if (lr != null && lr.ready()) {
            lr.close();
        }

        this.currentFileIdx = 0;
        this.lr = new LineNumberReader(new FileReader(getCurrentFile()));
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

    public int getLineNum() {
        return lr.getLineNumber();
    }
}
