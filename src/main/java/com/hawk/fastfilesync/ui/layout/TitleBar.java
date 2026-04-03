package com.hawk.fastfilesync.ui.layout;

import com.hawk.fastfilesync.ui.style.UiConstants;
import com.hawk.fastfilesync.ui.theme.manager.ThemeManager;
import java.awt.BorderLayout;
import java.awt.Dimension;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

/**
 * Title bar component displayed at the top of the application.
 */
public class TitleBar extends JPanel {

  /**
   * Creates a TitleBar with a title label and styling.
   */
  public TitleBar() {
    setLayout(new BorderLayout());
    setBackground(ThemeManager.theme().sidebar());
    setPreferredSize(new Dimension(0, 60));
    setBorder(new EmptyBorder(0, 20, 0, 20));

    JLabel title = new JLabel("Fast File Sync");
    title.setFont(UiConstants.TITLE_FONT);
    title.setForeground(ThemeManager.theme().textPrimary());

    add(title, BorderLayout.WEST);
  }
}