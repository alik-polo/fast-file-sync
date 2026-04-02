package com.hawk.fast_file_sync.ui.layout;

import com.hawk.fast_file_sync.ui.theme.manager.ThemeManager;

import javax.swing.*;
import java.awt.*;

public class GradientPanel extends JPanel {

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