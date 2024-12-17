package pcd.ass_single.part1.threads.view;

import pcd.ass_single.part1.common.Directory;
import pcd.ass_single.part1.common.ModelObserver;
import pcd.ass_single.part1.threads.controller.PdfCounterController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

public class PdfCounterSwingView extends JFrame implements ActionListener, ModelObserver {
    private final PdfCounterController controller;
    private final JLabel pendingPdfsCount;
    private final JLabel scannedPdfsCount;
    private final JLabel relevantPdfsCount;
    private final JFileChooser dirChooser;
    private final JTextField word;

    public PdfCounterSwingView(PdfCounterController controller) {
        super("PDF Counter");
        this.controller = controller;

        setSize(1280, 720);
        setResizable(true);

        JPanel panel = new JPanel();

        pendingPdfsCount = new JLabel();
        panel.add(pendingPdfsCount);
        scannedPdfsCount = new JLabel();
        panel.add(scannedPdfsCount);
        relevantPdfsCount = new JLabel();
        panel.add(relevantPdfsCount);

        dirChooser = new JFileChooser();
        dirChooser.setCurrentDirectory(new java.io.File("."));
        dirChooser.setDialogTitle("Choose the directory the search should be performed on.");
        dirChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        panel.add(dirChooser);

        word = new JTextField(10);
        panel.add(word);

        JButton startButton = new JButton("Start");
        startButton.addActionListener(e -> {
            controller.setDirectory(new Directory(dirChooser.getSelectedFile().toURI()));
            controller.setWord(word.getText());
            actionPerformed(e);
        });
        panel.add(startButton);
        for (String name : List.of("Stop", "Suspend", "Resume")) {
            JButton button = new JButton(name);
            button.addActionListener(this);
            panel.add(button);
        }

        setLayout(new GridLayout());
        add(panel, BorderLayout.NORTH);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent ev) {
                System.exit(-1);
            }
        });
    }

    public void actionPerformed(ActionEvent ev) {
        try {
            controller.processEvent(ev.getActionCommand());
        } catch (Exception ex) {
        }
    }

    @Override
    public void notifyPendingPdfsCount(int count) {
        updateLabel(pendingPdfsCount, "PDFs found: " + count);
    }

    @Override
    public void notifyScannedPdfsCount(int count) {
        updateLabel(scannedPdfsCount, "PDFs parsed: " + count);
    }

    @Override
    public void notifyRelevantPdfsCount(int count) {
        updateLabel(relevantPdfsCount, "PDFs satisfying criteria: " + count);
    }

    private static void updateLabel(JLabel label, String text) {
        try {
            SwingUtilities.invokeLater(() -> label.setText(text));
        } catch (Exception ex){
            ex.printStackTrace();
        }
    }
}
