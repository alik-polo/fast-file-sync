package com.hawk.fastfilesync.ui.card;

import com.hawk.fastfilesync.app.session.AppSession;
import com.hawk.fastfilesync.app.session.manager.SessionManager;
import com.hawk.fastfilesync.cunsumer.ReportConsumer;
import com.hawk.fastfilesync.enums.DiffOption;
import com.hawk.fastfilesync.ui.component.PathField;
import com.hawk.fastfilesync.ui.component.UiComponents;
import com.hawk.fastfilesync.ui.style.UiConstants;
import com.hawk.fastfilesync.ui.theme.manager.ThemeManager;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.nio.file.Path;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingWorker;

/**
 * Card component for scanning directories and displaying scan progress.
 */
public class ScanCard extends BaseCard {

  private final SessionManager sessionManager;
  private final ReportConsumer reportConsumer;
  private String scanStrategy = DiffOption.FAST.name();

  private SwingWorker<Void, Integer> worker;

  /**
   * Constructs a ScanCard with the given session manager and report consumer.
   *
   * @param sessionManager the session manager to handle scan sessions
   * @param reportConsumer the consumer to receive scan reports
   */
  public ScanCard(SessionManager sessionManager, ReportConsumer reportConsumer) {
    this.sessionManager = sessionManager;
    this.reportConsumer = reportConsumer;

    setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

    add(createHeader());
    add(Box.createVerticalStrut(UiConstants.SPACING_L));

    PathField leftField = new PathField("Select left directory...");
    PathField rightField = new PathField("Select right directory...");

    add(leftField);
    add(Box.createVerticalStrut(UiConstants.SPACING_M));
    add(rightField);
    add(Box.createVerticalStrut(UiConstants.SPACING_M));

    add(createStrategyPanel());
    add(Box.createVerticalStrut(UiConstants.SPACING_L));

    JPanel actions = new JPanel();
    actions.setOpaque(false);
    actions.setLayout(new BoxLayout(actions, BoxLayout.X_AXIS));

    JButton start = UiComponents.primaryButton("Start Scan");
    JButton stop = UiComponents.primaryButton("Stop Scan");
    stop.setEnabled(false);

    actions.add(start);
    actions.add(Box.createHorizontalStrut(UiConstants.SPACING_S));
    actions.add(stop);

    JProgressBar bar = createStyledProgressBar();
    bar.setVisible(false);

    start.addActionListener(e -> startScan(leftField, rightField, start, stop, bar));
    stop.addActionListener(e -> stopScan(start, stop, bar));

    add(actions);
    add(Box.createVerticalStrut(UiConstants.SPACING_M));
    add(bar);
  }

  /**
   * Creates the header component for the scan card.
   *
   * @return a JComponent containing the header
   */
  private JComponent createHeader() {
    JLabel title = new JLabel("Directory Scan");
    title.setFont(UiConstants.TITLE_FONT);
    title.setForeground(ThemeManager.theme().textPrimary());
    title.setAlignmentX(LEFT_ALIGNMENT);
    return title;
  }

  /**
   * Starts a directory scan using the provided input fields and UI components.
   *
   * @param leftField the field for the left directory
   * @param rightField the field for the right directory
   * @param start the start button
   * @param stop the stop button
   * @param bar the progress bar
   */
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

    if (!valid) {
      return;
    }

    start.setEnabled(false);
    stop.setEnabled(true);
    bar.setVisible(true);
    bar.setValue(0);

    try {
      AppSession session = sessionManager.createSession();

      worker = new SwingWorker<>() {
        @Override
        protected Void doInBackground() {

          Path left = Path.of(leftField.getText());
          Path right = Path.of(rightField.getText());

          session.runScan(
              left,
              right,
              scanStrategy.equals("FAST") ? DiffOption.FAST : DiffOption.DEEP,
              reportConsumer
          );
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

  /**
   * Stops the currently running scan and resets UI components.
   *
   * @param start the start button
   * @param stop the stop button
   * @param bar the progress bar
   */
  private void stopScan(JButton start, JButton stop, JProgressBar bar) {
    if (worker != null && !worker.isDone()) {
      sessionManager.cancelCurrentSession();
      worker.cancel(true);
    }

    start.setEnabled(true);
    stop.setEnabled(false);
    bar.setValue(0);
  }

  /**
   * Creates the panel to select scan strategy.
   *
   * @return a JPanel containing strategy selection controls
   */
  private JPanel createStrategyPanel() {

    JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
    panel.setOpaque(false);

    JLabel label = new JLabel("Scan Strategy:");
    label.setForeground(ThemeManager.theme().textSecondary());
    label.setFont(UiConstants.BODY_FONT);

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
    panel.add(Box.createHorizontalStrut(UiConstants.SPACING_S));
    panel.add(combo);

    return panel;
  }

  /**
   * Creates a styled progress bar for the scan process.
   *
   * @return a JProgressBar with custom styling
   */
  private JProgressBar createStyledProgressBar() {
    JProgressBar bar = new JProgressBar();
    bar.setStringPainted(true);
    bar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 22));
    bar.setBackground(ThemeManager.theme().secondarySurface());
    bar.setForeground(ThemeManager.theme().accent());
    return bar;
  }
}