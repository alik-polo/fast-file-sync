package com.hawk.fastfilesync.ui.component;

import com.hawk.fastfilesync.ui.theme.manager.ThemeManager;
import java.awt.AlphaComposite;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JComponent;
import javax.swing.Timer;

/**
 * A simple animated loading spinner component.
 * Draws rotating bars with fading effect.
 */
public class Spinner extends JComponent {

  private int angle = 0;
  private final Timer timer;

  /**
   * Creates a spinner with fixed size and animation timer.
   */
  public Spinner() {
    setPreferredSize(new Dimension(24, 24));
    setMinimumSize(new Dimension(24, 24));
    setMaximumSize(new Dimension(24, 24));

    timer = new Timer(100, e -> {
      angle += 30;
      repaint();
    });
  }

  /**
   * Starts the spinner animation and makes it visible.
   */
  public void start() {
    setVisible(true);
    timer.start();
  }

  /**
   * Stops the spinner animation and hides it.
   */
  public void stop() {
    timer.stop();
    setVisible(false);
  }

  @Override
  protected void paintComponent(Graphics g) {
    super.paintComponent(g);

    Graphics2D g2 = (Graphics2D) g.create();
    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

    int size = Math.min(getWidth(), getHeight());
    int center = size / 2;

    g2.translate(center, center);
    g2.rotate(Math.toRadians(angle));

    g2.setColor(ThemeManager.theme().accent());

    for (int i = 0; i < 12; i++) {
      float alpha = (float) i / 12;
      g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));

      g2.fillRoundRect(-2, -size / 2 + 4, 4, size / 6, 2, 2);
      g2.rotate(Math.PI / 6);
    }

    g2.dispose();
  }
}