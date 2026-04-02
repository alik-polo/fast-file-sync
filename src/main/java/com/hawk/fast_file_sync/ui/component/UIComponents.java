package com.hawk.fast_file_sync.ui.component;

import com.hawk.fast_file_sync.ui.theme.manager.ThemeManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public final class UIComponents {

  private UIComponents() {}

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