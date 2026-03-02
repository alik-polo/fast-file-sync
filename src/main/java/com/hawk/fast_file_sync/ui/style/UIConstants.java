package com.hawk.fast_file_sync.ui.style;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.Color;
import java.awt.Font;

public final class UIConstants {

  private UIConstants() {}

  public static final Color PRIMARY = new Color(88, 101, 242);
  public static final Color SIDEBAR_BG = new Color(28, 28, 34);
  public static final Color SIDEBAR_GRADIENT_END = new Color(45, 45, 55);
  public static final Color CARD_BG = new Color(36, 36, 44);
  public static final Color BUTTON_BG = new Color(50, 50, 60);
  public static final Color BUTTON_HIGHLIGHT = new Color(88, 101, 242, 150);
  public static final Color BUTTON_TEXT = Color.WHITE;

  public static final Font TITLE_FONT = new Font("SansSerif", Font.BOLD, 22);

  // Граница для текстовых полей
  public static final Border INPUT_BORDER = BorderFactory.createCompoundBorder(
      BorderFactory.createLineBorder(new Color(100, 100, 100), 1),
      BorderFactory.createEmptyBorder(5, 10, 5, 10)
  );

  // Вторичный цвет для кнопок, подсказок и info
  public static final Color SECONDARY_COLOR = new Color(70, 70, 80);
}