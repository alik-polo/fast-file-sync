package com.hawk.fastfilesync.ui.component;

import com.hawk.fastfilesync.ui.theme.manager.ThemeManager;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JButton;

/**
 * Utility class for creating styled UI components.
 */
public final class UiComponents {

  private UiComponents() {}

  /**
   * Creates a primary styled button with hover effect.
   *
   * @param text the button text
   * @return a JButton with primary styling
   */
  public static JButton primaryButton(String text) {

    JButton btn = new JButton(text);
    btn.setFocusPainted(false);
    btn.setForeground(ThemeManager.theme().buttonText());
    btn.setBackground(ThemeManager.theme().primary());
    btn.setMaximumSize(new Dimension(220, 48));
    btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

    btn.addMouseListener(new MouseAdapter() {
      public void mouseEntered(MouseEvent e) {
        btn.setBackground(ThemeManager.theme().primaryHover());
      }

      public void mouseExited(MouseEvent e) {
        btn.setBackground(ThemeManager.theme().primary());
      }
    });

    return btn;
  }
}