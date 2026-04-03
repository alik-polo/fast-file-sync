package com.hawk.fastfilesync.ui.theme;

import java.awt.Color;

/**
 * Interface defining the color palette for a UI theme.
 */
public interface Theme {

  /** Returns the background color. */
  Color background();

  /** Returns the main surface color. */
  Color surface();

  /** Returns the sidebar color. */
  Color sidebar();

  /** Returns the sidebar gradient end color. */
  Color sidebarGradientEnd();

  /** Returns the primary color. */
  Color primary();

  /** Returns the primary hover color. */
  Color primaryHover();

  /** Returns the accent color. */
  Color accent();

  /** Returns the primary text color. */
  Color textPrimary();

  /** Returns the secondary text color. */
  Color textSecondary();

  /** Returns the muted text color. */
  Color textMuted();

  /** Returns the input background color. */
  Color inputBackground();

  /** Returns the input border color. */
  Color inputBorder();

  /** Returns the input border color for errors. */
  Color inputBorderError();

  /** Returns the button background color. */
  Color buttonBackground();

  /** Returns the button hover color. */
  Color buttonHover();

  /** Returns the button text color. */
  Color buttonText();

  /** Returns the secondary surface color. */
  Color secondarySurface();
}