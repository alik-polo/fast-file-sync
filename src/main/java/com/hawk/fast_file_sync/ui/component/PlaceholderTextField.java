package com.hawk.fast_file_sync.ui.component;

import com.hawk.fast_file_sync.ui.style.UIConstants;
import com.hawk.fast_file_sync.ui.theme.manager.ThemeManager;

import javax.swing.*;
import java.awt.*;

public class PlaceholderTextField extends JTextField {

  private final String placeholder;

  public PlaceholderTextField(String placeholder) {

    this.placeholder = placeholder;
    setFont(UIConstants.BODY_FONT);
  }

  @Override
  protected void paintComponent(Graphics g) {

    super.paintComponent(g);

    if (getText().isEmpty()) {

      Graphics2D g2 = (Graphics2D) g.create();

      g2.setColor(ThemeManager.theme().textMuted());
      g2.setFont(getFont().deriveFont(Font.ITALIC));

      Insets insets = getInsets();

      g2.drawString(
          placeholder,
          insets.left + 6,
          getHeight() / 2 +
              g2.getFontMetrics().getAscent() / 2 - 2
      );

      g2.dispose();
    }
  }
}