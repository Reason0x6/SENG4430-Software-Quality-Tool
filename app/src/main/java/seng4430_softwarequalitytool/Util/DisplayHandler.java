package seng4430_softwarequalitytool.Util;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.utils.Log;
import com.github.javaparser.utils.SourceRoot;
import seng4430_softwarequalitytool.App;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class DisplayHandler {
    static public String selectedDirectory = "- no directory selected -";
    public static void createDisplay() {
        JFrame frame = new JFrame();

        JPanel panel = new JPanel();
        JPanel titlePanel = new JPanel();


        //page elements
        JLabel title = new JLabel("CodeProbe - Software Testing Tool");
        JButton selectDirectorybutton = new JButton("Select Directory");
        JLabel directorySelectionPrompt = new JLabel("Please select the root directory of the test application");
        JLabel directorySelectionDisplay = new JLabel(selectedDirectory);
        JButton generalTestButton = new JButton("Run Test on Directory");
        JLabel blankSpace = new JLabel("");
        JLabel blankSpace2 = new JLabel("");
        JLabel blankSpace3 = new JLabel("");
        JButton introspectiveTestButton = new JButton("Test Self");
        JButton testExampleButton = new JButton("Test Example");



        //event listeners
        selectDirectorybutton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser chooser= new JFileChooser();
                chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                int choice = chooser.showOpenDialog(frame);

                if (choice != JFileChooser.APPROVE_OPTION) return;

                selectedDirectory = chooser.getSelectedFile().getAbsolutePath();
                directorySelectionDisplay.setText(selectedDirectory);
            }
        });
        introspectiveTestButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //run the main application function here passing in the string
                Log.setAdapter(new Log.StandardOutStandardErrorAdapter());
                File report = App.createFile();
                String reportFilePath = report.getAbsolutePath();
                try{
                    App.introspectiveTest(reportFilePath);

                    Desktop desktop = Desktop.getDesktop();

                    desktop.open(report);
                } catch (Exception err) {
                    err.printStackTrace();
                }
            }
        });
        testExampleButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //run the main application function here passing in the string
                Log.setAdapter(new Log.StandardOutStandardErrorAdapter());
                File report = App.createFile();
                String reportFilePath = report.getAbsolutePath();
                try{
                    App.exampleTest(reportFilePath);

                    Desktop desktop = Desktop.getDesktop();

                    desktop.open(report);
                } catch (Exception err) {
                    err.printStackTrace();
                }
            }
        });
        generalTestButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //run the main application function here passing in the string
                Log.setAdapter(new Log.StandardOutStandardErrorAdapter());
                File report = App.createFile();
                String reportFilePath = report.getAbsolutePath();
                try{
                    App.generalTest(reportFilePath, selectedDirectory);

                    Desktop desktop = Desktop.getDesktop();

                    desktop.open(report);
                } catch (Exception err) {
                    err.printStackTrace();
                }
            }
        });

        //element styling
        panel.setBorder(BorderFactory.createEmptyBorder(30,30,10,30));
        panel.setLayout(new GridLayout(9,1));
        panel.setPreferredSize(new Dimension(600,200));
        ImageIcon img = new ImageIcon("src/main/resources/Icons/icon.png");
        frame.setIconImage(img.getImage());
        title.setAlignmentX(JLabel.CENTER);
        title.setFont(new Font("Sans Serif", Font.PLAIN, 20));

        //addition of elements to the page
        titlePanel.add(title);
        panel.add(directorySelectionPrompt);
        panel.add(selectDirectorybutton);
        panel.add(directorySelectionDisplay);
        panel.add(generalTestButton);
        panel.add(blankSpace);
        panel.add(blankSpace2);
        panel.add(blankSpace3);
        panel.add(introspectiveTestButton);
        panel.add(testExampleButton);

        JPanel parent = new JPanel();
        parent.setLayout(new BoxLayout(parent, BoxLayout.Y_AXIS));
        parent.add(titlePanel);
        parent.add(panel);
        frame.getContentPane().add(parent);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setTitle("CodeProbe");
        frame.pack();
        frame.setVisible(true);
    }
}
