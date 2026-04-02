package com.hawk.fast_file_sync.ui.card;

import com.hawk.fast_file_sync.app.session.AppSession;
import com.hawk.fast_file_sync.app.session.manager.SessionManager;
import com.hawk.fast_file_sync.enums.ConflictOption;
import com.hawk.fast_file_sync.enums.SyncOption;
import com.hawk.fast_file_sync.exception.OperationCancelledException;
import com.hawk.fast_file_sync.ui.component.UIComponents;
import com.hawk.fast_file_sync.ui.style.UIConstants;
import com.hawk.fast_file_sync.ui.theme.manager.ThemeManager;

import javax.swing.*;
import java.awt.*;
import java.nio.file.Path;

public class SyncCard extends BaseCard {

  private final SessionManager sessionManager;

  private SyncOption syncOption = SyncOption.NEW;
  private ConflictOption conflictOption = ConflictOption.SAVE_BIGGEST;

  private SwingWorker<Void, Void> worker;

  public SyncCard(SessionManager sessionManager) {
    this.sessionManager = sessionManager;

    setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

    add(createHeader());
    add(Box.createVerticalStrut(UIConstants.SPACING_L));

    add(createInfo());
    add(Box.createVerticalStrut(UIConstants.SPACING_M));

    add(createSection("Sync Mode", createSyncOptionsPanel()));
    add(Box.createVerticalStrut(UIConstants.SPACING_M));

    add(createSection("Conflict Handling", createConflictOptionsPanel()));
    add(Box.createVerticalStrut(UIConstants.SPACING_L));

    JButton start = UIComponents.primaryButton("Start Sync");
    JButton stop = UIComponents.primaryButton("Stop Sync");
    stop.setEnabled(false);

    JPanel actions = new JPanel();
    actions.setOpaque(false);
    actions.setLayout(new BoxLayout(actions, BoxLayout.X_AXIS));

    actions.add(start);
    actions.add(Box.createHorizontalStrut(UIConstants.SPACING_S));
    actions.add(stop);

    JProgressBar bar = createStyledProgressBar();
    bar.setVisible(false);

    start.addActionListener(e -> startSync(start, stop, bar));
    stop.addActionListener(e -> stopSync(start, stop, bar));

    add(actions);
    add(Box.createVerticalStrut(UIConstants.SPACING_M));
    add(bar);
  }

  private JComponent createHeader() {
    JLabel title = new JLabel("Directory Sync");
    title.setFont(UIConstants.TITLE_FONT);
    title.setForeground(ThemeManager.theme().textPrimary());
    title.setAlignmentX(Component.LEFT_ALIGNMENT);
    return title;
  }

  private JComponent createInfo() {
    JLabel info = new JLabel("Using results from last scan");
    info.setFont(UIConstants.BODY_FONT);
    info.setForeground(ThemeManager.theme().textMuted());
    info.setAlignmentX(Component.LEFT_ALIGNMENT);
    return info;
  }

  private JPanel createSection(String title, JComponent content) {

    JPanel panel = column();

    JLabel label = new JLabel(title);
    label.setFont(UIConstants.BODY_FONT);
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
    row.setAlignmentX(Component.LEFT_ALIGNMENT);

    JLabel label = new JLabel(labelText);
    label.setFont(UIConstants.BODY_FONT);
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
    panel.setAlignmentX(Component.LEFT_ALIGNMENT);
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

  private JProgressBar createStyledProgressBar() {
    JProgressBar bar = new JProgressBar();
    bar.setStringPainted(true);
    bar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 22));
    bar.setBackground(ThemeManager.theme().secondarySurface());
    bar.setForeground(ThemeManager.theme().accent());
    return bar;
  }

  private void startSync(JButton start, JButton stop, JProgressBar bar) {

    if (worker != null && !worker.isDone()) return;

    try {
      AppSession session = sessionManager.getCurrentSession()
          .orElseThrow(() -> new IllegalStateException("Scan must be executed first"));

      Path target = resolveTarget(session);
      if (target == null) return;

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
          } catch (OperationCancelledException ignored) {
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

  private Path resolveTarget(AppSession session) {
    return switch (syncOption) {
      case LEFT -> session.getLeft();
      case RIGHT -> session.getRight();
      case NEW -> {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Select target directory");
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        int result = chooser.showOpenDialog(this);
        if (result != JFileChooser.APPROVE_OPTION) yield null;

        yield chooser.getSelectedFile().toPath();
      }
    };
  }

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