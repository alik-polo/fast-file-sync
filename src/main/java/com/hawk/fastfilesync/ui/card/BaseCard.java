package com.hawk.fastfilesync.ui.card;

import com.hawk.fastfilesync.ui.style.UiConstants;
import com.hawk.fastfilesync.ui.theme.manager.ThemeManager;
import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

/**
 * Base class for all card components in the UI.
 */
public abstract class BaseCard extends JPanel {

  /**
   * Constructs a new BaseCard with default layout and styling.
   */
  protected BaseCard() {
    setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
    setOpaque(false);
    setBorder(new EmptyBorder(
        UiConstants.SPACING_L,
        UiConstants.SPACING_L,
        UiConstants.SPACING_L,
        UiConstants.SPACING_L));
  }

  /**
   * Paints the card component with rounded corners and surface color.
   *
   * @param g the Graphics context
   */
  @Override
  protected void paintComponent(Graphics g) {

    Graphics2D g2 = (Graphics2D) g.create();
    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

    g2.setColor(ThemeManager.theme().surface());
    g2.fillRoundRect(
        0, 0,
        getWidth(), getHeight(),
        UiConstants.BORDER_RADIUS,
        UiConstants.BORDER_RADIUS
    );

    g2.dispose();
    super.paintComponent(g);
  }

  /**
   * Wraps this card in a transparent JPanel with padding.
   *
   * @return a JPanel containing this card
   */
  protected JPanel wrap() {
    JPanel wrapper = new JPanel(new BorderLayout());
    wrapper.setOpaque(false);
    wrapper.setBorder(new EmptyBorder(
        UiConstants.SPACING_M,
        UiConstants.SPACING_M,
        UiConstants.SPACING_M,
        UiConstants.SPACING_M));
    wrapper.add(this, BorderLayout.CENTER);
    return wrapper;
  }
}