package seng4430_softwarequalitytool.Util;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLightLaf;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.utils.Log;
import com.github.javaparser.utils.SourceRoot;
import seng4430_softwarequalitytool.App;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class DisplayHandler {
    static public String selectedDirectory = "C:\\";
    File lastFile;

    static {
        try {
            selectedDirectory = new File(".").getCanonicalPath();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public  void createDisplay() {
        FlatDarkLaf.setup();

        JFrame frame = new JFrame();
        JPanel parent = new JPanel();
        parent.setLayout(new BoxLayout(parent, BoxLayout.Y_AXIS));

        ImageIcon img = new ImageIcon("src/main/resources/Icons/CODEPROBE.png");
        frame.setIconImage(img.getImage());

        JPanel titlePanel = new JPanel();
        JLabel title = new JLabel("CodeProbe - Software Testing Tool");
        title.setAlignmentX(JLabel.CENTER);
        title.setFont(new Font("Sans Serif", Font.PLAIN, 20));

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new FlowLayout(FlowLayout.CENTER));

        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout(FlowLayout.CENTER));

        JPanel actionPanel = new JPanel();
        actionPanel.setLayout(new FlowLayout(FlowLayout.CENTER));

        //page elements
        JButton selectDirectorybutton = new JButton("Select Directory");
        JLabel directorySelectionPrompt = new JLabel("Please select the root directory of the Java application you wish to test");
        JTextField  directorySelectionDisplay = new JTextField(selectedDirectory);
        JButton generalTestButton = new JButton("Run Test on Directory");

        JButton introspectiveTestButton = new JButton("Test Self");

        JButton testExampleButton = new JButton("Test Example");
        JButton openLastFileButton = new JButton("Open Last Report");
        openLastFileButton.setEnabled(false);

        //addition of elements to the page
        titlePanel.add(title);
        panel.add(directorySelectionPrompt);

        inputPanel.add(selectDirectorybutton);
        inputPanel.add(directorySelectionDisplay);

        actionPanel.add(generalTestButton);
        actionPanel.add(introspectiveTestButton);
        actionPanel.add(testExampleButton);
        actionPanel.add(openLastFileButton);

        parent.setPreferredSize(new Dimension(600,200));
        parent.add(titlePanel);
        parent.add(panel);
        parent.add(inputPanel);
        parent.add(actionPanel);

        frame.getContentPane().add(parent);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setTitle("CodeProbe");
        frame.setMinimumSize(new Dimension(600,200));
        frame.pack();
        frame.setVisible(true);


        //event listeners
        frame.addComponentListener(new ComponentAdapter(){
            public void componentResized(ComponentEvent e){
                Dimension d=frame.getSize();
                Dimension minD=frame.getMinimumSize();
                if(d.width<minD.width)
                    d.width=minD.width;
                if(d.height<minD.height)
                    d.height=minD.height;
                frame.setSize(d);
            }
        });

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
                    lastFile = report;
                    openLastFileButton.setEnabled(true);

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
                    lastFile = report;
                    openLastFileButton.setEnabled(true);
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
                    lastFile = report;
                    openLastFileButton.setEnabled(true);
                } catch (Exception err) {
                    err.printStackTrace();
                }
            }
        });

        openLastFileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openLastFile();
            }
        });
    }

    public  void openLastFile(){
        Desktop desktop = Desktop.getDesktop();
        try {
            if(lastFile != null){
                desktop.open(lastFile);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
