package com.hawk.fast_file_sync.ui.theme;

import java.awt.*;

public interface Theme {

  // Base
  Color background();
  Color surface();
  Color sidebar();
  Color sidebarGradientEnd();

  // Primary palette
  Color primary();
  Color primaryHover();
  Color accent();

  // Text
  Color textPrimary();
  Color textSecondary();
  Color textMuted();

  // Inputs
  Color inputBackground();
  Color inputBorder();
  Color inputBorderError();

  // Buttons
  Color buttonBackground();
  Color buttonHover();
  Color buttonText();

  // Misc
  Color secondarySurface();
}