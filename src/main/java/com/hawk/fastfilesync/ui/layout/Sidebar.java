package com.hawk.fastfilesync.ui.layout;

import com.hawk.fastfilesync.ui.style.UiConstants;
import com.hawk.fastfilesync.ui.theme.manager.ThemeManager;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

/**
 * Sidebar component for navigation between different cards in a CardLayout.
 */
public class Sidebar extends JPanel {

  private JButton selectedButton = null;
  private final List<JButton> buttons = new ArrayList<>();
  private final CardLayout layout;
  private final JPanel contentPanel;

  /**
   * Creates a Sidebar with the given layout and content panel.
   *
   * @param layout the CardLayout managing the content panel
   * @param contentPanel the panel containing different cards
   */
  public Sidebar(CardLayout layout, JPanel contentPanel) {
    this.layout = layout;
    this.contentPanel = contentPanel;

    setPreferredSize(new Dimension(240, 0));
    setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
    setBorder(new EmptyBorder(25, 15, 25, 15));
    setBackground(ThemeManager.theme().sidebar());

    add(Box.createVerticalStrut(10));

    addNav("🏠 Home", "home");
    addNav("📁 Scan", "scan");
    addNav("🔄 Sync", "sync");
    addNav("⚙ Settings", "settings");

    add(Box.createVerticalGlue());

    selectButton("home");
  }

  /**
   * Adds a navigation button to the sidebar.
   *
   * @param text the text of the button
   * @param card the card name associated with the button
   */
  private void addNav(String text, String card) {
    JButton btn = createNavButton(text, card);
    buttons.add(btn);
    add(btn);
    add(Box.createVerticalStrut(10));
  }

  /**
   * Selects the navigation button corresponding to the given card
   * and shows the card in the content panel.
   *
   * @param card the name of the card to display
   */
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

  /**
   * Creates a styled navigation button.
   *
   * @param text the text of the button
   * @param card the card name associated with the button
   * @return the created JButton
   */
  private JButton createNavButton(String text, String card) {

    JButton btn = new JButton(text) {

      private boolean hovered = false;

      {
        addMouseListener(new MouseAdapter() {
          @Override public void mouseEntered(MouseEvent e) {
            hovered = true;
            repaint();
          }

          @Override public void mouseExited(MouseEvent e) {
            hovered = false;
            repaint();
          }
        });
      }

      @Override
      protected void paintComponent(Graphics g) {

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        Color bg = ThemeManager.theme().buttonBackground();

        if (hovered) {
          bg = ThemeManager.theme().buttonHover();
        }

        g2.setColor(bg);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(),
            UiConstants.BORDER_RADIUS,
            UiConstants.BORDER_RADIUS);

        if (this == selectedButton) {
          g2.setColor(ThemeManager.theme().accent());
          g2.fillRoundRect(0, 0, 4, getHeight(),
              UiConstants.BORDER_RADIUS,
              UiConstants.BORDER_RADIUS);
        }

        g2.dispose();

        super.paintComponent(g);
      }
    };

    btn.setForeground(ThemeManager.theme().textPrimary());
    btn.setFont(UiConstants.BODY_FONT);
    btn.setFocusPainted(false);
    btn.setContentAreaFilled(false);
    btn.setBorder(new EmptyBorder(12, 16, 12, 16));
    btn.setAlignmentX(LEFT_ALIGNMENT);
    btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
    btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    btn.setHorizontalAlignment(SwingConstants.LEFT);
    btn.setActionCommand(card);

    btn.addActionListener(e -> selectButton(card));

    return btn;
  }
}