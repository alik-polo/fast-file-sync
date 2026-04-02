package com.hawk.fast_file_sync.ui.card;

import com.hawk.fast_file_sync.ui.style.UIConstants;
import com.hawk.fast_file_sync.ui.theme.manager.ThemeManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public abstract class BaseCard extends JPanel {

  protected BaseCard() {
    setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
    setOpaque(false);
    setBorder(new EmptyBorder(
        UIConstants.SPACING_L,
        UIConstants.SPACING_L,
        UIConstants.SPACING_L,
        UIConstants.SPACING_L));
  }

  @Override
  protected void paintComponent(Graphics g) {

    Graphics2D g2 = (Graphics2D) g.create();
    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

    g2.setColor(ThemeManager.theme().surface());
    g2.fillRoundRect(
        0, 0,
        getWidth(), getHeight(),
        UIConstants.BORDER_RADIUS,
        UIConstants.BORDER_RADIUS
    );

    g2.dispose();
    super.paintComponent(g);
  }

  protected JPanel wrap() {
    JPanel wrapper = new JPanel(new BorderLayout());
    wrapper.setOpaque(false);
    wrapper.setBorder(new EmptyBorder(
        UIConstants.SPACING_M,
        UIConstants.SPACING_M,
        UIConstants.SPACING_M,
        UIConstants.SPACING_M));
    wrapper.add(this, BorderLayout.CENTER);
    return wrapper;
  }
}