package com.hawk.fastfilesync.ui.card;

import com.hawk.fastfilesync.app.session.AppSession;
import com.hawk.fastfilesync.app.session.manager.SessionManager;
import com.hawk.fastfilesync.cunsumer.ReportConsumer;
import com.hawk.fastfilesync.enums.DiffOption;
import com.hawk.fastfilesync.exception.UserException;
import com.hawk.fastfilesync.ui.component.PathField;
import com.hawk.fastfilesync.ui.component.Spinner;
import com.hawk.fastfilesync.ui.component.UiComponents;
import com.hawk.fastfilesync.ui.style.UiConstants;
import com.hawk.fastfilesync.ui.theme.manager.ThemeManager;
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
import javax.swing.SwingWorker;

/**
 * Card component for scanning directories.
 */
public class ScanCard extends BaseCard {

  private final SessionManager sessionManager;
  private final ReportConsumer reportConsumer;

  private String scanStrategy = DiffOption.FAST.name();

  private final Spinner spinner;
  private SwingWorker<Void, Void> worker;

  /**
   * Creates the scan card UI with directory inputs and controls.
   * Initializes layout, fields, strategy options, buttons, and spinner.
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

    spinner = new Spinner();
    spinner.setVisible(false);
    spinner.setAlignmentX(LEFT_ALIGNMENT);

    add(actions);
    add(Box.createVerticalStrut(UiConstants.SPACING_M));
    add(spinner);

    start.addActionListener(e -> startScan(leftField, rightField, start, stop));
    stop.addActionListener(e -> stopScan(start, stop));
  }

  private JComponent createHeader() {
    JLabel title = new JLabel("Directory Scan");
    title.setFont(UiConstants.TITLE_FONT);
    title.setForeground(ThemeManager.theme().textPrimary());
    title.setAlignmentX(LEFT_ALIGNMENT);
    return title;
  }

  private void startScan(
      PathField leftField,
      PathField rightField,
      JButton start,
      JButton stop) {

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
      showError("Please select both directories");
      return;
    }

    start.setEnabled(false);
    stop.setEnabled(true);
    spinner.start();

    AppSession session;
    try {
      session = sessionManager.createSession();
    } catch (Exception e) {
      handleSystemError(e);
      resetUI(start, stop);
      return;
    }

    worker = new SwingWorker<>() {

      @Override
      protected Void doInBackground() throws Exception {

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
        try {
          get();

        } catch (Exception e) {

          Throwable cause = e.getCause();

          if (cause instanceof UserException userEx) {
            showError(userEx.getMessage());
          } else if (cause instanceof java.util.concurrent.CancellationException) {
          } else {
            handleSystemError(cause);
          }
        }

        resetUI(start, stop);
      }
    };

    worker.execute();
  }

  private void stopScan(JButton start, JButton stop) {
    if (worker != null && !worker.isDone()) {
      worker.cancel(true);
      sessionManager.cancelCurrentSession();
    }

    resetUI(start, stop);
  }

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

  private void showError(String message) {
    JOptionPane.showMessageDialog(
        this,
        message,
        "Error",
        JOptionPane.ERROR_MESSAGE
    );
  }

  private void handleSystemError(Throwable e) {
    e.printStackTrace(); // logs
    showError("Something went wrong. Please try again.");
  }

  private void resetUI(JButton start, JButton stop) {
    start.setEnabled(true);
    stop.setEnabled(false);
    spinner.stop();
  }
}