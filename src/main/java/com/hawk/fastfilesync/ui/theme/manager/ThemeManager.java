package com.hawk.fastfilesync.ui.theme.manager;

import com.hawk.fastfilesync.ui.theme.Theme;

/**
 * Manages the current UI theme.
 */
public final class ThemeManager {

  private static Theme currentTheme;

  private ThemeManager() {}

  /**
   * Sets the current theme.
   *
   * @param theme the theme to set
   */
  public static void setTheme(Theme theme) {
    currentTheme = theme;
  }

  /**
   * Returns the current theme.
   *
   * @return the current Theme
   */
  public static Theme theme() {
    return currentTheme;
  }
}