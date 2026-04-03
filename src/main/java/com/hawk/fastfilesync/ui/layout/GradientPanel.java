package com.hawk.fastfilesync.ui.layout;

import com.hawk.fastfilesync.ui.theme.manager.ThemeManager;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LayoutManager;
import java.awt.RenderingHints;
import javax.swing.JPanel;

/**
 * JPanel with a vertical gradient background.
 */
public class GradientPanel extends JPanel {

  /**
   * Constructs a GradientPanel with the specified layout manager.
   *
   * @param layout the layout manager to use
   */
  public GradientPanel(LayoutManager layout) {
    super(layout);
    setOpaque(false);
  }

  @Override
  protected void paintComponent(Graphics g) {

    Graphics2D g2 = (Graphics2D) g.create();
    g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

    Color top = ThemeManager.theme().background();
    Color bottom = ThemeManager.theme().surface();

    GradientPaint gp = new GradientPaint(
        0, 0, top,
        0, getHeight(), bottom
    );

    g2.setPaint(gp);
    g2.fillRect(0, 0, getWidth(), getHeight());

    g2.dispose();

    super.paintComponent(g);
  }
}