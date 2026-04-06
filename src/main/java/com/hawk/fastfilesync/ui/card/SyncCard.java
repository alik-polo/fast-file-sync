package com.hawk.fastfilesync.ui.card;

import com.hawk.fastfilesync.app.session.AppSession;
import com.hawk.fastfilesync.app.session.manager.SessionManager;
import com.hawk.fastfilesync.enums.ConflictOption;
import com.hawk.fastfilesync.enums.SyncOption;
import com.hawk.fastfilesync.exception.UserException;
import com.hawk.fastfilesync.ui.component.Spinner;
import com.hawk.fastfilesync.ui.component.UiComponents;
import com.hawk.fastfilesync.ui.style.UiConstants;
import com.hawk.fastfilesync.ui.theme.manager.ThemeManager;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.nio.file.Path;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingWorker;

/**
 * Card component for syncing directories using scan results.
 */
public class SyncCard extends BaseCard {

  private final SessionManager sessionManager;

  private SyncOption syncOption = SyncOption.NEW;
  private ConflictOption conflictOption = ConflictOption.SAVE_BIGGEST;

  private final Spinner spinner;
  private SwingWorker<Void, Void> worker;

  /**
   * Creates the sync card UI with controls and actions.
   * Initializes layout, sections, buttons, and spinner.
   */
  public SyncCard(SessionManager sessionManager) {
    this.sessionManager = sessionManager;

    setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

    add(createHeader());
    add(Box.createVerticalStrut(UiConstants.SPACING_L));

    add(createInfo());
    add(Box.createVerticalStrut(UiConstants.SPACING_M));

    add(createSection("Sync Mode", createSyncOptionsPanel()));
    add(Box.createVerticalStrut(UiConstants.SPACING_M));

    add(createSection("Conflict Handling", createConflictOptionsPanel()));
    add(Box.createVerticalStrut(UiConstants.SPACING_L));

    JPanel actions = new JPanel();
    actions.setOpaque(false);
    actions.setLayout(new BoxLayout(actions, BoxLayout.X_AXIS));

    JButton start = UiComponents.primaryButton("Start Sync");
    JButton stop = UiComponents.primaryButton("Stop Sync");

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

    start.addActionListener(e -> startSync(start, stop));
    stop.addActionListener(e -> stopSync(start, stop));
  }

  private JComponent createHeader() {
    JLabel title = new JLabel("Directory Sync");
    title.setFont(UiConstants.TITLE_FONT);
    title.setForeground(ThemeManager.theme().textPrimary());
    title.setAlignmentX(LEFT_ALIGNMENT);
    return title;
  }

  private JComponent createInfo() {
    JLabel info = new JLabel("Using results from last scan");
    info.setFont(UiConstants.BODY_FONT);
    info.setForeground(ThemeManager.theme().textMuted());
    info.setAlignmentX(LEFT_ALIGNMENT);
    return info;
  }

  private JPanel createSection(String title, JComponent content) {
    JPanel panel = column();

    JLabel label = new JLabel(title);
    label.setFont(UiConstants.BODY_FONT);
    label.setForeground(ThemeManager.theme().textSecondary());

    panel.add(label);
    panel.add(Box.createVerticalStrut(8));
    panel.add(content);

    return panel;
  }

  private JPanel createSyncOptionsPanel() {

    JComboBox<SyncOption> combo = new JComboBox<>(SyncOption.values());
    combo.setSelectedItem(syncOption);
    styleCombo(combo);

    combo.addActionListener(e ->
        syncOption = (SyncOption) combo.getSelectedItem()
    );

    JPanel panel = column();
    panel.add(createRow("Mode", combo));

    return panel;
  }

  private JPanel createConflictOptionsPanel() {

    JComboBox<ConflictOption> combo = new JComboBox<>(ConflictOption.values());
    combo.setSelectedItem(conflictOption);
    styleCombo(combo);

    combo.addActionListener(e ->
        conflictOption = (ConflictOption) combo.getSelectedItem()
    );

    JPanel panel = column();
    panel.add(createRow("Strategy", combo));

    return panel;
  }

  private JPanel createRow(String labelText, JComponent component) {

    JPanel row = new JPanel(new BorderLayout());
    row.setOpaque(false);
    row.setAlignmentX(LEFT_ALIGNMENT);

    JLabel label = new JLabel(labelText);
    label.setFont(UiConstants.BODY_FONT);
    label.setForeground(ThemeManager.theme().textPrimary());

    row.add(label, BorderLayout.WEST);
    row.add(component, BorderLayout.EAST);

    row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

    return row;
  }

  private JPanel column() {
    JPanel panel = new JPanel();
    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
    panel.setOpaque(false);
    panel.setAlignmentX(LEFT_ALIGNMENT);
    return panel;
  }

  private void styleCombo(JComboBox<?> combo) {
    combo.setFocusable(false);
    combo.setBackground(ThemeManager.theme().inputBackground());
    combo.setForeground(ThemeManager.theme().textPrimary());
    combo.setBorder(BorderFactory.createLineBorder(
        ThemeManager.theme().inputBorder(), 1));

    combo.setPreferredSize(new Dimension(180, 28));
  }

  private void startSync(JButton start, JButton stop) {

    if (worker != null && !worker.isDone()) {
      return;
    }

    AppSession session;
    try {
      session = sessionManager.getCurrentSession()
          .orElseThrow(() -> new UserException("Please run scan first"));
    } catch (Exception e) {
      handleSystemError(e);
      return;
    }

    Path target = resolveTarget(session);
    if (target == null) {
      return;
    }

    start.setEnabled(false);
    stop.setEnabled(true);
    spinner.start();

    worker = new SwingWorker<>() {

      @Override
      protected Void doInBackground() throws Exception {
        session.runSync(
            session.getLeft(),
            session.getRight(),
            target,
            syncOption,
            conflictOption
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

        sessionManager.cancelCurrentSession();
        resetUI(start, stop);
      }
    };

    worker.execute();
  }

  private Path resolveTarget(AppSession session) {
    return switch (syncOption) {
      case LEFT -> session.getLeft();
      case RIGHT -> session.getRight();
      case NEW -> {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Select target directory");
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        int result = chooser.showOpenDialog(this);
        if (result != JFileChooser.APPROVE_OPTION) {
          yield null;
        }

        yield chooser.getSelectedFile().toPath();
      }
    };
  }

  private void stopSync(JButton start, JButton stop) {
    if (worker != null && !worker.isDone()) {
      worker.cancel(true);
      sessionManager.cancelCurrentSession();
    }

    resetUI(start, stop);
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
    e.printStackTrace(); // log
    showError("Something went wrong. Please try again.");
  }

  private void resetUI(JButton start, JButton stop) {
    start.setEnabled(true);
    stop.setEnabled(false);
    spinner.stop();
  }
}