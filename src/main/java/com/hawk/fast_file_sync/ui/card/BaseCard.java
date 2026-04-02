package com.hawk.fast_file_sync.ui.card;

import com.hawk.fast_file_sync.ui.style.UIConstants;
import com.hawk.fast_file_sync.ui.theme.manager.ThemeManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public abstract class BaseCard extends JPanel {

  protected BaseCard() {
    setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
    setBackground(ThemeManager.theme().surface());
    setBorder(new EmptyBorder(
        UIConstants.SPACING_L,
        UIConstants.SPACING_L,
        UIConstants.SPACING_L,
        UIConstants.SPACING_L));
  }

  protected JPanel wrap() {
    JPanel wrapper = new JPanel(new BorderLayout());
    wrapper.setOpaque(false);
    wrapper.add(this, BorderLayout.CENTER);
    return wrapper;
  }
}