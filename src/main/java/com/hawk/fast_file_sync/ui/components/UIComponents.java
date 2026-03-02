package com.hawk.fast_file_sync.ui.components;

import com.hawk.fast_file_sync.ui.style.UIConstants;

import javax.swing.*;
import java.awt.*;

public final class UIComponents {

  private UIComponents() {}

  public static JButton primaryButton(String text) {
    JButton btn = new JButton(text);
    btn.setBackground(UIConstants.PRIMARY);
    btn.setForeground(Color.WHITE);
    btn.setFocusPainted(false);
    btn.setMaximumSize(new Dimension(200, 45));
    return btn;
  }
}