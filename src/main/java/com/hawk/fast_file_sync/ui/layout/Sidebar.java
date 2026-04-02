package com.hawk.fast_file_sync.ui.layout;

import com.hawk.fast_file_sync.ui.style.UIConstants;
import com.hawk.fast_file_sync.ui.theme.manager.ThemeManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class Sidebar extends JPanel {

  private JButton selectedButton = null;
  private final List<JButton> buttons = new ArrayList<>();
  private final CardLayout layout;
  private final JPanel contentPanel;

  public Sidebar(CardLayout layout, JPanel contentPanel) {
    this.layout = layout;
    this.contentPanel = contentPanel;

    setPreferredSize(new Dimension(240, 0));
    setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
    setBorder(new EmptyBorder(25, 15, 25, 15));
    setBackground(ThemeManager.theme().sidebar());

    add(Box.createVerticalStrut(20));

    addNav("🏠 Home", "home");
    addNav("📁 Scan", "scan");
    addNav("🔄 Sync", "sync");
    addNav("⚙ Settings", "settings");

    add(Box.createVerticalGlue());

    selectButton("Home");
  }

  private void addNav(String text, String card) {
    JButton btn = createNavButton(text, card);
    buttons.add(btn);
    add(btn);
    add(Box.createVerticalStrut(12));
  }

  public void selectButton(String card) {
    for (JButton btn : buttons) {
      if (btn.getActionCommand().equals(card)) {
        selectedButton = btn;
        layout.show(contentPanel, card);
        repaint();
        break;
      }
    }
  }

  private JButton createNavButton(String text, String card) {

    JButton btn = new JButton(text) {

      @Override
      protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setColor(ThemeManager.theme().buttonBackground());
        g2.fillRoundRect(0, 0, getWidth(), getHeight(),
            UIConstants.BORDER_RADIUS,
            UIConstants.BORDER_RADIUS);

        if (this == selectedButton) {
          g2.setColor(ThemeManager.theme().primary());
          g2.fillRoundRect(0, 0, 5, getHeight(),
              UIConstants.BORDER_RADIUS,
              UIConstants.BORDER_RADIUS);
        }

        g2.dispose();
        super.paintComponent(g);
      }
    };

    btn.setForeground(ThemeManager.theme().textPrimary());
    btn.setFocusPainted(false);
    btn.setContentAreaFilled(false);
    btn.setBorder(new EmptyBorder(12, 18, 12, 18));
    btn.setAlignmentX(Component.LEFT_ALIGNMENT);
    btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
    btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    btn.setActionCommand(card);

    btn.addActionListener(e -> selectButton(card));

    // Hover effect
    btn.addMouseListener(new MouseAdapter() {
      public void mouseEntered(MouseEvent e) {
        btn.setForeground(ThemeManager.theme().accent());
      }

      public void mouseExited(MouseEvent e) {
        btn.setForeground(ThemeManager.theme().textPrimary());
      }
    });

    return btn;
  }
}