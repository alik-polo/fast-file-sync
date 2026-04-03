package com.hawk.fastfilesync.ui.component;

import com.hawk.fastfilesync.ui.style.UiConstants;
import com.hawk.fastfilesync.ui.theme.manager.ThemeManager;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import javax.swing.JTextField;

/**
 * JTextField that displays a placeholder text when empty.
 */
public class PlaceholderTextField extends JTextField {

  private final String placeholder;

  /**
   * Constructs a PlaceholderTextField with specified placeholder text.
   *
   * @param placeholder the placeholder text to display when field is empty
   */
  public PlaceholderTextField(String placeholder) {

    this.placeholder = placeholder;
    setFont(UiConstants.BODY_FONT);
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
          getHeight() / 2 + g2.getFontMetrics().getAscent() / 2 - 2
      );

      g2.dispose();
    }
  }
}