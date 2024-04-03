package seng4430_softwarequalitytool.Util;

/**
 * Interface for modules using directory scanner
 */
public interface DSModule {
    String compute(DirectoryScanner ds, String reportFilePath);
    void printModuleHeader();
    void printInformation();
    void saveResult();
}
