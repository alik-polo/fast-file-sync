package com.hawk.fast_file_sync.ui.cards;

import com.hawk.fast_file_sync.app.config.AppConfig;
import com.hawk.fast_file_sync.app.session.AppSession;
import com.hawk.fast_file_sync.app.session.manager.SessionManager;
import com.hawk.fast_file_sync.model.BufferSnapshot;
import com.hawk.fast_file_sync.sync.policy.impl.FastFailPolicy;
import com.hawk.fast_file_sync.ui.components.UIComponents;
import com.hawk.fast_file_sync.ui.style.UIConstants;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.*;
import java.io.File;

public class ScanCard extends BaseCard {

  private String scanStrategy = "FAST";

  public ScanCard() {
    setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
    setBackground(UIConstants.CARD_BG);

    JLabel title = new JLabel("Directory Scan");
    title.setFont(UIConstants.TITLE_FONT);
    title.setAlignmentX(Component.LEFT_ALIGNMENT);

    add(title);
    add(Box.createVerticalStrut(20));

    PathField leftField = new PathField("Select left directory...");
    PathField rightField = new PathField("Select right directory...");

    add(leftField);
    add(Box.createVerticalStrut(15));
    add(rightField);
    add(Box.createVerticalStrut(15));

    JPanel strategyPanel = new JPanel();
    strategyPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
    strategyPanel.setBackground(UIConstants.CARD_BG);
    strategyPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

    JLabel strategyLabel = new JLabel("Scan Strategy: ");
    strategyLabel.setForeground(Color.WHITE);

    String[] options = {"FAST", "DEEP"};
    JComboBox<String> strategyCombo = new JComboBox<>(options);
    strategyCombo.setSelectedItem(scanStrategy);
    strategyCombo.setFocusable(false);
    strategyCombo.addActionListener(e -> scanStrategy = (String) strategyCombo.getSelectedItem());

    strategyPanel.add(strategyLabel);
    strategyPanel.add(Box.createHorizontalStrut(10));
    strategyPanel.add(strategyCombo);

    add(strategyPanel);
    add(Box.createVerticalStrut(15));

    JButton start = UIComponents.primaryButton("Start Scan");
    start.setAlignmentX(Component.LEFT_ALIGNMENT);

    JProgressBar bar = new JProgressBar();
    bar.setStringPainted(true);
    bar.setVisible(false);
    bar.setAlignmentX(Component.LEFT_ALIGNMENT);

    start.addActionListener(e -> {
      boolean valid = true;
      if (leftField.getText().isEmpty()) {
        leftField.setBorder(new LineBorder(Color.RED, 2));
        valid = false;
      } else leftField.setBorder(UIConstants.INPUT_BORDER);

      if (rightField.getText().isEmpty()) {
        rightField.setBorder(new LineBorder(Color.RED, 2));
        valid = false;
      } else rightField.setBorder(UIConstants.INPUT_BORDER);

      if (!valid) return;

      start.setEnabled(false);
      bar.setVisible(true);
      bar.setValue(0);

      System.out.println("Selected scan strategy: " + scanStrategy);

      new SwingWorker<Void, Integer>() {
        protected Void doInBackground() throws Exception {
          for (int i = 0; i <= 100; i += 4) {
            Thread.sleep(60);
            publish(i);
          }
          return null;
        }

        protected void process(java.util.List<Integer> chunks) {
          bar.setValue(chunks.get(chunks.size() - 1));
        }

        protected void done() {
          start.setEnabled(true);
        }
      }.execute();
    });

    add(start);
    add(Box.createVerticalStrut(15));
    add(bar);
  }

  private static class PathField extends JPanel {
    private final PlaceholderTextField field;

    public PathField(String placeholder) {
      setLayout(new BorderLayout());
      setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
      setBackground(UIConstants.CARD_BG);

      field = new PlaceholderTextField(placeholder);
      field.setBorder(UIConstants.INPUT_BORDER);
      field.setBackground(new Color(50, 50, 60));
      field.setForeground(Color.WHITE);
      field.setCaretColor(Color.WHITE);

      new DropTarget(field, new DropTargetAdapter() {
        public void drop(DropTargetDropEvent dtde) {
          try {
            dtde.acceptDrop(DnDConstants.ACTION_COPY);
            java.util.List<File> droppedFiles = (java.util.List<File>) dtde.getTransferable()
                .getTransferData(DataFlavor.javaFileListFlavor);
            if (!droppedFiles.isEmpty() && droppedFiles.get(0).isDirectory()) {
              field.setText(droppedFiles.get(0).getAbsolutePath());
              field.setBorder(UIConstants.INPUT_BORDER);
            }
          } catch (Exception ex) {
            ex.printStackTrace();
          }
        }
      });

      JButton browse = getJButton();

      add(field, BorderLayout.CENTER);
      add(browse, BorderLayout.EAST);
    }

    private JButton getJButton() {
      JButton browse = new JButton("\uD83D\uDCC1");
      browse.setFocusable(false);
      browse.setPreferredSize(new Dimension(40, 40));
      browse.setBackground(UIConstants.SECONDARY_COLOR);
      browse.setForeground(Color.WHITE);
      browse.setBorder(UIConstants.INPUT_BORDER);

      browse.addActionListener(e -> {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int result = chooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
          File selected = chooser.getSelectedFile();
          field.setText(selected.getAbsolutePath());
        }
      });
      return browse;
    }

    public String getText() {
      return field.getText();
    }

    public void setBorder(LineBorder border) {
      field.setBorder(border);
    }
  }

  /** Текстовое поле с placeholder */
  private static class PlaceholderTextField extends JTextField {
    private final String placeholder;

    public PlaceholderTextField(String placeholder) {
      this.placeholder = placeholder;
    }

    @Override
    protected void paintComponent(Graphics g) {
      super.paintComponent(g);
      if (getText().isEmpty()) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setColor(Color.GRAY);
        g2.setFont(getFont().deriveFont(Font.ITALIC));
        Insets insets = getInsets();
        g2.drawString(placeholder, insets.left + 5, getHeight() / 2 + g2.getFontMetrics().getAscent() / 2 - 2);
        g2.dispose();
      }
    }
  }
}