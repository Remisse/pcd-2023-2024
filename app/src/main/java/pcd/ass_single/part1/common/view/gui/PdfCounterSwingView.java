package pcd.ass_single.part1.common.view.gui;

import pcd.ass_single.part1.common.Directory;
import pcd.ass_single.part1.common.Logger;
import pcd.ass_single.part1.common.ModelObserver;
import pcd.ass_single.part1.common.controller.PdfCounterController;
import pcd.ass_single.part1.common.view.PdfCounterView;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.List;

public class PdfCounterSwingView extends JFrame implements PdfCounterView, ActionListener {
    private static final Logger LOGGER = Logger.get();
    private final PdfCounterController<PdfCounterView> controller;
    private final JTextField totalPdfsCount;
    private final JTextField parsedPdfsCount;
    private final JTextField foundPdfsCount;
    private final JTextField directoryField;
    private final JTextField searchTermField;

    public PdfCounterSwingView(PdfCounterController<PdfCounterView> controller) {
        super("PDF Counter");
        this.controller = controller;

        setSize(1280, 720);
        setResizable(true);
        setVisible(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel(new GridLayout(10, 10));
        add(panel);

        panel.add(new JLabel("Total"));
        totalPdfsCount = createNonEditableField(panel, "0");
        panel.add(new JLabel("Parsed"));
        parsedPdfsCount = createNonEditableField(panel, "0");
        panel.add(new JLabel("Containing search term"));
        foundPdfsCount = createNonEditableField(panel, "0");

        panel.add(getDirectoryChooserButton());
        directoryField = createNonEditableField(panel, "");

        panel.add(new JLabel("Search term"));
        searchTermField = new JTextField(10);
        panel.add(searchTermField);

        JButton startButton = new JButton("Start");
        startButton.addActionListener(e -> {
            controller.setDirectory(new Directory(directoryField.getText()));
            controller.setSearchTerm(searchTermField.getText());
            actionPerformed(e);
        });
        panel.add(startButton);
        for (String name : List.of("Stop", "Suspend", "Resume")) {
            JButton button = new JButton(name);
            button.addActionListener(this);
            panel.add(button);
        }
    }

    private JButton getDirectoryChooserButton() {
        var directoryChooserButton = new JButton("Choose directory");
        directoryChooserButton.addActionListener(e -> {
            JFileChooser directoryChooser = new JFileChooser();
            directoryChooser.setDialogTitle("Select a directory");
            directoryChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            int result = directoryChooser.showOpenDialog(null);
            if (result == JFileChooser.APPROVE_OPTION) {
                File selectedDirectory = directoryChooser.getSelectedFile();
                directoryField.setText(selectedDirectory.getAbsolutePath());
            }
        });
        return directoryChooserButton;
    }

    public void actionPerformed(ActionEvent ev) {
        try {
            controller.processEvent(ev.getActionCommand());
        } catch (Exception ex) {
            LOGGER.debugLog(ex.toString());
        }
    }

    @Override
    public void notifyTotalPdfsCount(int count) {
        updateField(totalPdfsCount, "" + count);
    }

    @Override
    public void notifyParsedPdfsCount(int count) {
        updateField(parsedPdfsCount, "" + count);
    }

    @Override
    public void notifyFoundPdfsCount(int count) {
        updateField(foundPdfsCount, "" + count);
    }

    private static JTextField createNonEditableField(JPanel panel, String defaultText) {
        var res = new JTextField(defaultText);
        res.setEditable(false);
        panel.add(res);
        return res;
    }

    private static void updateField(JTextField field, String text) {
        try {
            SwingUtilities.invokeAndWait(() -> field.setText(text));
        } catch (Exception ex){
            LOGGER.debugLog(ex.toString());
        }
    }

    @Override
    public void display() {
        setVisible(true);
    }

    @Override
    public void onStop() {
    }
}
