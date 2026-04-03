package com.hawk.fastfilesync.ui.card;

import com.hawk.fastfilesync.ui.style.UiConstants;
import com.hawk.fastfilesync.ui.theme.manager.ThemeManager;
import java.awt.BorderLayout;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;

/**
 * Card component displaying a welcome message and app overview.
 */
public class WelcomeCard extends BaseCard {

  /**
   * Constructs a WelcomeCard and initializes UI components.
   */
  @SuppressWarnings("checkstyle:TextBlockGoogleStyleFormatting")
  public WelcomeCard() {

    setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
    setAlignmentX(LEFT_ALIGNMENT);

    add(createHeader());
    add(Box.createVerticalStrut(UiConstants.SPACING_L));

    add(createSection(
        "What is this?",
        "Fast File Sync helps you compare and synchronize directories quickly and efficiently."
    ));

    add(Box.createVerticalStrut(UiConstants.SPACING_M));

    add(createSection(
        "How it works",
        """
            1. Select left and right folders
            2. Choose scan strategy (FAST / DEEP)
            3. Review differences
            4. Run synchronization"""
    ));

    add(Box.createVerticalStrut(UiConstants.SPACING_M));

    add(createSection(
        "Features",
        """
            • Fast and deep scanning
            • Smart synchronization modes
            • Configurable filters
            • Error handling strategies"""
    ));

    add(Box.createVerticalStrut(UiConstants.SPACING_L));

    add(createNote());

    add(Box.createVerticalGlue());
  }

  /**
   * Creates the header component with title and subtitle.
   *
   * @return a JComponent containing the header
   */
  private JComponent createHeader() {

    JPanel panel = new JPanel();
    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
    panel.setOpaque(false);
    panel.setAlignmentX(LEFT_ALIGNMENT);

    JLabel title = new JLabel("Fast File Sync");
    title.setFont(new Font(UiConstants.TITLE_FONT.getName(), Font.BOLD, 26));
    title.setForeground(ThemeManager.theme().textPrimary());

    JLabel subtitle = new JLabel("Fast & reliable directory synchronization");
    subtitle.setFont(UiConstants.BODY_FONT);
    subtitle.setForeground(ThemeManager.theme().textMuted());

    panel.add(title);
    panel.add(Box.createVerticalStrut(6));
    panel.add(subtitle);

    return panel;
  }

  /**
   * Creates a section with a title and content text.
   *
   * @param title the section title
   * @param content the section text content
   * @return a JComponent representing the section
   */
  private JComponent createSection(String title, String content) {

    JPanel panel = new JPanel();
    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
    panel.setOpaque(false);
    panel.setAlignmentX(LEFT_ALIGNMENT);

    JLabel sectionTitle = new JLabel(title);
    sectionTitle.setFont(UiConstants.BODY_FONT);
    sectionTitle.setForeground(ThemeManager.theme().textPrimary());

    JTextArea text = new JTextArea(content);
    text.setFont(UiConstants.BODY_FONT);
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

  /**
   * Creates a note panel with warning or additional information.
   *
   * @return a JComponent containing the note
   */
  private JComponent createNote() {

    JPanel panel = new JPanel();
    panel.setLayout(new BorderLayout());
    panel.setOpaque(false);
    panel.setAlignmentX(LEFT_ALIGNMENT);

    panel.setBorder(BorderFactory.createEmptyBorder(
        UiConstants.SPACING_S,
        UiConstants.SPACING_S,
        UiConstants.SPACING_S,
        UiConstants.SPACING_S
    ));

    JLabel note = new JLabel("⚠ This is a test version and may contain bugs.");
    note.setFont(UiConstants.BODY_FONT);
    note.setForeground(ThemeManager.theme().accent());

    panel.add(note, BorderLayout.WEST);

    return panel;
  }
}