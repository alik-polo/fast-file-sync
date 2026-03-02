package com.hawk.fast_file_sync.ui.cards;

import com.hawk.fast_file_sync.ui.style.UIConstants;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public abstract class BaseCard extends JPanel {

  protected BaseCard() {
    setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
    setBackground(UIConstants.CARD_BG);
    setBorder(new EmptyBorder(30, 30, 30, 30));
  }

  protected JPanel wrap() {
    JPanel wrapper = new JPanel(new BorderLayout());
    wrapper.setOpaque(false);
    wrapper.add(this, BorderLayout.CENTER);
    return wrapper;
  }
}