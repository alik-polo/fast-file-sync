package com.hawk.fast_file_sync.ui.theme.mod;

import com.hawk.fast_file_sync.ui.theme.Theme;

import java.awt.*;

public class StormyMorningLightTheme implements Theme {

  @Override public Color background() { return new Color(232, 238, 242); } // Fog
  @Override public Color surface() { return new Color(255, 255, 255); }
  @Override public Color sidebar() { return new Color(214, 224, 231); }
  @Override public Color sidebarGradientEnd() { return new Color(232, 238, 242); }

  @Override public Color primary() { return new Color(95, 115, 130); }
  @Override public Color primaryHover() { return new Color(111, 163, 200); }
  @Override public Color accent() { return new Color(111, 163, 200); }

  @Override public Color textPrimary() { return new Color(31, 42, 48); }
  @Override public Color textSecondary() { return new Color(47, 62, 70); }
  @Override public Color textMuted() { return new Color(95, 115, 130); }

  @Override public Color inputBackground() { return Color.WHITE; }
  @Override public Color inputBorder() { return new Color(167, 182, 194); }
  @Override public Color inputBorderError() { return new Color(200, 60, 60); }

  @Override public Color buttonBackground() { return new Color(214, 224, 231); }
  @Override public Color buttonHover() { return new Color(167, 182, 194); }
  @Override public Color buttonText() { return textPrimary(); }

  @Override public Color secondarySurface() { return new Color(240, 245, 248); }
}