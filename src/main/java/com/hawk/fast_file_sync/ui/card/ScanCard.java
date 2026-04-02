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
import java.nio.file.Path;

public class ScanCard extends BaseCard {

  private final SessionManager sessionManager;
  private final ReportConsumer reportConsumer;
  private String scanStrategy = "FAST";

  private SwingWorker<Void, Integer> worker;

  public ScanCard(SessionManager sessionManager, ReportConsumer reportConsumer) {
    this.sessionManager = sessionManager;
    this.reportConsumer = reportConsumer;

    setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

    add(createHeader());
    add(Box.createVerticalStrut(UIConstants.SPACING_L));

    PathField leftField = new PathField("Select left directory...");
    PathField rightField = new PathField("Select right directory...");

    add(leftField);
    add(Box.createVerticalStrut(UIConstants.SPACING_M));
    add(rightField);
    add(Box.createVerticalStrut(UIConstants.SPACING_M));

    add(createStrategyPanel());
    add(Box.createVerticalStrut(UIConstants.SPACING_L));

    JButton start = UIComponents.primaryButton("Start Scan");
    JButton stop = UIComponents.primaryButton("Stop Scan");
    stop.setEnabled(false);

    JPanel actions = new JPanel();
    actions.setOpaque(false);
    actions.setLayout(new BoxLayout(actions, BoxLayout.X_AXIS));

    actions.add(start);
    actions.add(Box.createHorizontalStrut(UIConstants.SPACING_S));
    actions.add(stop);

    JProgressBar bar = createStyledProgressBar();
    bar.setVisible(false);

    start.addActionListener(e -> startScan(leftField, rightField, start, stop, bar));
    stop.addActionListener(e -> stopScan(start, stop, bar));

    add(actions);
    add(Box.createVerticalStrut(UIConstants.SPACING_M));
    add(bar);
  }

  private JComponent createHeader() {
    JLabel title = new JLabel("Directory Scan");
    title.setFont(UIConstants.TITLE_FONT);
    title.setForeground(ThemeManager.theme().textPrimary());
    title.setAlignmentX(Component.LEFT_ALIGNMENT);
    return title;
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
    } else leftField.resetBorder();

    if (rightField.getText().isEmpty()) {
      rightField.setErrorBorder();
      valid = false;
    } else rightField.resetBorder();

    if (!valid) return;

    start.setEnabled(false);
    stop.setEnabled(true);
    bar.setVisible(true);
    bar.setValue(0);

    try {
      AppSession session = sessionManager.createSession();

      worker = new SwingWorker<>() {
        @Override
        protected Void doInBackground() throws Exception {

          Path left = Path.of(leftField.getText());
          Path right = Path.of(rightField.getText());

          try {
            session.runScan(
                left,
                right,
                scanStrategy.equals("FAST") ? DiffOption.FAST : DiffOption.DEEP,
                reportConsumer
            );
          } catch (OperationCancelledException ignored) {}

          return null;
        }

        @Override
        protected void done() {
          start.setEnabled(true);
          stop.setEnabled(false);
          bar.setValue(isCancelled() ? 0 : 100);
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

  private void stopScan(JButton start, JButton stop, JProgressBar bar) {
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

    JLabel label = new JLabel("Scan Strategy:");
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
    bar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 22));
    bar.setBackground(ThemeManager.theme().secondarySurface());
    bar.setForeground(ThemeManager.theme().accent());
    return bar;
  }
}