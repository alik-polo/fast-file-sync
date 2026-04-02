package com.hawk.fast_file_sync.ui.card;

import com.hawk.fast_file_sync.ui.style.UIConstants;
import com.hawk.fast_file_sync.ui.theme.manager.ThemeManager;

import javax.swing.*;
import java.awt.*;

public class WelcomeCard extends BaseCard {

  public WelcomeCard() {

    setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
    setAlignmentX(Component.LEFT_ALIGNMENT);

    add(createHeader());
    add(Box.createVerticalStrut(UIConstants.SPACING_L));

    add(createSection(
        "What is this?",
        "Fast File Sync helps you compare and synchronize directories quickly and efficiently."
    ));

    add(Box.createVerticalStrut(UIConstants.SPACING_M));

    add(createSection(
        "How it works",
        "1. Select left and right folders\n" +
            "2. Choose scan strategy (FAST / DEEP)\n" +
            "3. Review differences\n" +
            "4. Run synchronization"
    ));

    add(Box.createVerticalStrut(UIConstants.SPACING_M));

    add(createSection(
        "Features",
        "• Fast and deep scanning\n" +
            "• Smart synchronization modes\n" +
            "• Configurable filters\n" +
            "• Error handling strategies"
    ));

    add(Box.createVerticalStrut(UIConstants.SPACING_L));

    add(createNote());

    add(Box.createVerticalGlue());
  }

  private JComponent createHeader() {

    JPanel panel = new JPanel();
    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
    panel.setOpaque(false);
    panel.setAlignmentX(Component.LEFT_ALIGNMENT);

    JLabel title = new JLabel("Fast File Sync");
    title.setFont(new Font(UIConstants.TITLE_FONT.getName(), Font.BOLD, 26));
    title.setForeground(ThemeManager.theme().textPrimary());

    JLabel subtitle = new JLabel("Fast & reliable directory synchronization");
    subtitle.setFont(UIConstants.BODY_FONT);
    subtitle.setForeground(ThemeManager.theme().textMuted());

    panel.add(title);
    panel.add(Box.createVerticalStrut(6));
    panel.add(subtitle);

    return panel;
  }

  private JComponent createSection(String title, String content) {

    JPanel panel = new JPanel();
    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
    panel.setOpaque(false);
    panel.setAlignmentX(Component.LEFT_ALIGNMENT);

    JLabel sectionTitle = new JLabel(title);
    sectionTitle.setFont(UIConstants.BODY_FONT);
    sectionTitle.setForeground(ThemeManager.theme().textPrimary());

    JTextArea text = new JTextArea(content);
    text.setFont(UIConstants.BODY_FONT);
    text.setForeground(ThemeManager.theme().textSecondary());
    text.setOpaque(false);
    text.setEditable(false);
    text.setLineWrap(true);
    text.setWrapStyleWord(true);
    text.setBorder(BorderFactory.createEmptyBorder());

    panel.add(sectionTitle);
    panel.add(Box.createVerticalStrut(6));
    panel.add(text);

    return panel;
  }

  private JComponent createNote() {

    JPanel panel = new JPanel();
    panel.setLayout(new BorderLayout());
    panel.setOpaque(false);
    panel.setAlignmentX(Component.LEFT_ALIGNMENT);

    panel.setBorder(BorderFactory.createEmptyBorder(
        UIConstants.SPACING_S,
        UIConstants.SPACING_S,
        UIConstants.SPACING_S,
        UIConstants.SPACING_S
    ));

    JLabel note = new JLabel("⚠ This is a test version and may contain bugs.");
    note.setFont(UIConstants.BODY_FONT);
    note.setForeground(ThemeManager.theme().accent());

    panel.add(note, BorderLayout.WEST);

    return panel;
  }
}