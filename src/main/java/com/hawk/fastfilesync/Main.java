package com.hawk.fastfilesync;

import com.formdev.flatlaf.FlatDarkLaf;
import com.hawk.fastfilesync.app.config.AppConfig;
import com.hawk.fastfilesync.app.session.manager.SessionManager;
import com.hawk.fastfilesync.cunsumer.impl.UiTextAreaReportConsumer;
import com.hawk.fastfilesync.reader.SettingsFileReader;
import com.hawk.fastfilesync.ui.MainFrame;
import com.hawk.fastfilesync.ui.style.UiConstants;
import com.hawk.fastfilesync.ui.theme.manager.ThemeManager;
import com.hawk.fastfilesync.ui.theme.mod.StormyMorningDarkTheme;
import java.util.Map;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * Entry point for the Fast File Sync application.
 */
public class Main {

  /**
   * Entry point for the Fast File Sync application.
   */
  public static void main(String[] args) {
    Map<String, String> settings = SettingsFileReader.readSettings();

    AppConfig config = AppConfig.getInstance();
    config.configure(settings);

    ThemeManager.setTheme(new StormyMorningDarkTheme());

    FlatDarkLaf.setup();

    UIManager.put("Button.arc", UiConstants.BORDER_RADIUS);
    UIManager.put("Component.arc", UiConstants.BORDER_RADIUS);

    JTextPane logPlane = new JTextPane();
    UiTextAreaReportConsumer uiConsumer = new UiTextAreaReportConsumer(logPlane);

    SessionManager sessionManager =
        new SessionManager(config, uiConsumer);

    SwingUtilities.invokeLater(() ->
        new MainFrame(sessionManager, uiConsumer, logPlane).setVisible(true)
    );
  }
}