package com.hawk.fast_file_sync.ui.theme.manager;

import com.hawk.fast_file_sync.ui.theme.Theme;

public final class ThemeManager {

  private static Theme currentTheme;

  private ThemeManager() {}

  public static void setTheme(Theme theme) {
    currentTheme = theme;
  }

  public static Theme theme() {
    return currentTheme;
  }
}