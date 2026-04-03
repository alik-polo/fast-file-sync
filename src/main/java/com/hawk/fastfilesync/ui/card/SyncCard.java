package com.hawk.fastfilesync.ui.card;

import com.hawk.fastfilesync.app.session.AppSession;
import com.hawk.fastfilesync.app.session.manager.SessionManager;
import com.hawk.fastfilesync.enums.ConflictOption;
import com.hawk.fastfilesync.enums.SyncOption;
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
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

/**
 * Card component for syncing directories using scan results.
 */
public class SyncCard extends BaseCard {

  private final SessionManager sessionManager;

  private SyncOption syncOption = SyncOption.NEW;
  private ConflictOption conflictOption = ConflictOption.SAVE_BIGGEST;

  private SwingWorker<Void, Void> worker;

  /**
   * Constructs a SyncCard with the given session manager.
   *
   * @param sessionManager the session manager to handle sync sessions
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

    JProgressBar bar = createStyledProgressBar();
    bar.setVisible(false);

    start.addActionListener(e -> startSync(start, stop, bar));
    stop.addActionListener(e -> stopSync(start, stop, bar));

    add(actions);
    add(Box.createVerticalStrut(UiConstants.SPACING_M));
    add(bar);
  }

  /**
   * Creates the header component for the sync card.
   *
   * @return a JComponent containing the header
   */
  private JComponent createHeader() {
    JLabel title = new JLabel("Directory Sync");
    title.setFont(UiConstants.TITLE_FONT);
    title.setForeground(ThemeManager.theme().textPrimary());
    title.setAlignmentX(LEFT_ALIGNMENT);
    return title;
  }

  /**
   * Creates the info label component.
   *
   * @return a JComponent containing info text
   */
  private JComponent createInfo() {
    JLabel info = new JLabel("Using results from last scan");
    info.setFont(UiConstants.BODY_FONT);
    info.setForeground(ThemeManager.theme().textMuted());
    info.setAlignmentX(LEFT_ALIGNMENT);
    return info;
  }

  /**
   * Creates a section panel with a title and content.
   *
   * @param title the section title
   * @param content the section content component
   * @return a JPanel representing the section
   */
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

  /**
   * Creates the panel for selecting sync options.
   *
   * @return a JPanel containing sync option controls
   */
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

  /**
   * Creates the panel for selecting conflict handling options.
   *
   * @return a JPanel containing conflict option controls
   */
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

  /**
   * Creates a row with a label and component.
   *
   * @param labelText the row label
   * @param component the component for the row
   * @return a JPanel representing the row
   */
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

  /**
   * Creates a vertical column panel.
   *
   * @return a JPanel with vertical BoxLayout
   */
  private JPanel column() {
    JPanel panel = new JPanel();
    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
    panel.setOpaque(false);
    panel.setAlignmentX(LEFT_ALIGNMENT);
    return panel;
  }

  /**
   * Styles a combo box with theme colors and size.
   *
   * @param combo the JComboBox to style
   */
  private void styleCombo(JComboBox<?> combo) {
    combo.setFocusable(false);
    combo.setBackground(ThemeManager.theme().inputBackground());
    combo.setForeground(ThemeManager.theme().textPrimary());
    combo.setBorder(BorderFactory.createLineBorder(
        ThemeManager.theme().inputBorder(), 1));

    combo.setPreferredSize(new Dimension(180, 28));
  }

  /**
   * Creates a styled progress bar for sync operations.
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

  /**
   * Starts the directory sync process and updates UI components.
   *
   * @param start the start button
   * @param stop the stop button
   * @param bar the progress bar
   */
  private void startSync(JButton start, JButton stop, JProgressBar bar) {

    if (worker != null && !worker.isDone()) {
      return;
    }

    try {
      AppSession session = sessionManager.getCurrentSession()
          .orElseThrow(() -> new IllegalStateException("Scan must be executed first"));

      Path target = resolveTarget(session);
      if (target == null) {
        return;
      }

      start.setEnabled(false);
      stop.setEnabled(true);
      bar.setVisible(true);
      bar.setValue(0);

      worker = new SwingWorker<>() {

        @Override
        protected Void doInBackground() {
          try {
            session.runSync(
                session.getLeft(),
                session.getRight(),
                target,
                syncOption,
                conflictOption
            );
          } catch (Exception e) {
            SwingUtilities.invokeLater(() ->
                JOptionPane.showMessageDialog(
                    SyncCard.this,
                    "Sync failed: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
                )
            );
          }
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
          "Failed to start sync: " + e.getMessage(),
          "Error",
          JOptionPane.ERROR_MESSAGE
      );

      start.setEnabled(true);
      stop.setEnabled(false);
      bar.setValue(0);
    }
  }

  /**
   * Resolves the target directory based on the selected sync option.
   *
   * @param session the current session
   * @return the Path of the target directory or null if canceled
   */
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

  /**
   * Stops the currently running sync and resets UI components.
   *
   * @param start the start button
   * @param stop the stop button
   * @param bar the progress bar
   */
  private void stopSync(JButton start, JButton stop, JProgressBar bar) {
    if (worker != null && !worker.isDone()) {
      sessionManager.cancelCurrentSession();
      worker.cancel(true);
    }
    start.setEnabled(true);
    stop.setEnabled(false);
    bar.setValue(0);
  }
}