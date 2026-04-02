package com.hawk.fast_file_sync.ui.card;

import com.hawk.fast_file_sync.app.session.AppSession;
import com.hawk.fast_file_sync.app.session.manager.SessionManager;
import com.hawk.fast_file_sync.cunsumer.ReportConsumer;
import com.hawk.fast_file_sync.enums.DiffOption;
import com.hawk.fast_file_sync.exception.OperationCancelledException;
import com.hawk.fast_file_sync.ui.component.PathField;
import com.hawk.fast_file_sync.ui.component.UIComponents;
import com.hawk.fast_file_sync.ui.style.UIConstants;
import com.hawk.fast_file_sync.ui.theme.manager.ThemeManager;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.*;
import java.io.File;
import java.nio.file.Path;
import java.util.List;

public class ScanCard extends BaseCard {

  private final SessionManager sessionManager;
  private final ReportConsumer reportConsumer;
  private String scanStrategy = "FAST";

  private SwingWorker<Void, Integer> worker;

  public ScanCard(SessionManager sessionManager, ReportConsumer reportConsumer) {
    this.sessionManager = sessionManager;
    this.reportConsumer = reportConsumer;

    JLabel title = new JLabel("Directory Scan");
    title.setFont(UIConstants.TITLE_FONT);
    title.setForeground(ThemeManager.theme().textPrimary());
    title.setAlignmentX(Component.LEFT_ALIGNMENT);

    add(title);
    add(Box.createVerticalStrut(UIConstants.SPACING_L));

    PathField leftField = new PathField("Select left directory...");
    PathField rightField = new PathField("Select right directory...");

    add(leftField);
    add(Box.createVerticalStrut(UIConstants.SPACING_M));
    add(rightField);
    add(Box.createVerticalStrut(UIConstants.SPACING_M));

    add(createStrategyPanel());
    add(Box.createVerticalStrut(UIConstants.SPACING_M));

    JButton start = UIComponents.primaryButton("Start Scan");
    start.setAlignmentX(Component.LEFT_ALIGNMENT);

    JButton stop = UIComponents.primaryButton("Stop Scan");
    stop.setAlignmentX(Component.LEFT_ALIGNMENT);
    stop.setEnabled(false);

    JProgressBar bar = createStyledProgressBar();
    bar.setVisible(false);

    start.addActionListener(e ->
        startScan(leftField, rightField, start, stop, bar)
    );

    stop.addActionListener(e ->
        stopScan(start, stop, bar)
    );

    add(start);
    add(Box.createVerticalStrut(UIConstants.SPACING_S));
    add(stop);
    add(Box.createVerticalStrut(UIConstants.SPACING_M));
    add(bar);
  }

  private void startScan(
      PathField leftField,
      PathField rightField,
      JButton start,
      JButton stop,
      JProgressBar bar) {

    reportConsumer.clear();

    boolean valid = true;

    if (leftField.getText().isEmpty()) {
      leftField.setErrorBorder();
      valid = false;
    } else {
      leftField.resetBorder();
    }

    if (rightField.getText().isEmpty()) {
      rightField.setErrorBorder();
      valid = false;
    } else {
      rightField.resetBorder();
    }

    if (!valid)
      return;

    start.setEnabled(false);
    stop.setEnabled(true);
    bar.setVisible(true);
    bar.setValue(0);

    try {
      AppSession session = sessionManager.createSession();

/*      sessionManager.getCurrentSession().ifPresent(s -> {
        s.reportConsumer().setProgressListener(progress -> {
          SwingUtilities.invokeLater(() -> bar.setValue(progress));
        });
      });*/

      worker = new SwingWorker<>() {

        @Override
        protected Void doInBackground() throws Exception {

          Path left = Path.of(leftField.getText());
          Path right = Path.of(rightField.getText());

          try {
            session.runScan(left, right, scanStrategy.equals("FAST") ? DiffOption.FAST : DiffOption.DEEP, reportConsumer);
          } catch (OperationCancelledException e) {
            System.out.println("Scan cancelled");
          }

          return null;
        }

        @Override
        protected void done() {
          start.setEnabled(true);
          stop.setEnabled(false);

          if (isCancelled()) {
            bar.setValue(0);
          } else {
            bar.setValue(100);
          }
        }
      };

      worker.execute();

    } catch (Exception e) {
      JOptionPane.showMessageDialog(
          this,
          "Failed to start scan: " + e.getMessage(),
          "Error",
          JOptionPane.ERROR_MESSAGE
      );
      start.setEnabled(true);
      stop.setEnabled(false);
      bar.setValue(0);
    }
  }

  private void stopScan(
      JButton start,
      JButton stop,
      JProgressBar bar) {

    if (worker != null && !worker.isDone()) {
      sessionManager.cancelCurrentSession();
      worker.cancel(true);
    }

    start.setEnabled(true);
    stop.setEnabled(false);
    bar.setValue(0);
  }

  private JPanel createStrategyPanel() {

    JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
    panel.setOpaque(false);
    panel.setAlignmentX(Component.LEFT_ALIGNMENT);

    JLabel label = new JLabel("Scan Strategy: ");
    label.setForeground(ThemeManager.theme().textSecondary());
    label.setFont(UIConstants.BODY_FONT);

    JComboBox<String> combo = new JComboBox<>(new String[]{"FAST", "DEEP"});
    combo.setSelectedItem(scanStrategy);
    combo.setFocusable(false);
    combo.setBackground(ThemeManager.theme().inputBackground());
    combo.setForeground(ThemeManager.theme().textPrimary());
    combo.setBorder(BorderFactory.createLineBorder(
        ThemeManager.theme().inputBorder(), 1));

    combo.addActionListener(e ->
        scanStrategy = (String) combo.getSelectedItem());

    panel.add(label);
    panel.add(Box.createHorizontalStrut(UIConstants.SPACING_S));
    panel.add(combo);

    return panel;
  }

  private JProgressBar createStyledProgressBar() {

    JProgressBar bar = new JProgressBar();
    bar.setStringPainted(true);
    bar.setAlignmentX(Component.LEFT_ALIGNMENT);
    bar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 25));
    bar.setBackground(ThemeManager.theme().secondarySurface());
    bar.setForeground(ThemeManager.theme().accent());

    return bar;
  }

}