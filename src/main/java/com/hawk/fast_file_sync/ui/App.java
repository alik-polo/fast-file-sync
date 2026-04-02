package com.hawk.fast_file_sync.ui;

import com.formdev.flatlaf.FlatDarkLaf;
import com.hawk.fast_file_sync.app.config.AppConfig;
import com.hawk.fast_file_sync.app.session.manager.SessionManager;
import com.hawk.fast_file_sync.cunsumer.impl.UITextAreaReportConsumer;
import com.hawk.fast_file_sync.reader.SettingsFileReader;
import com.hawk.fast_file_sync.sync.policy.impl.FastFailPolicy;
import com.hawk.fast_file_sync.ui.style.UIConstants;
import com.hawk.fast_file_sync.ui.theme.manager.ThemeManager;
import com.hawk.fast_file_sync.ui.theme.mod.StormyMorningDarkTheme;

import javax.swing.*;
import java.util.Map;

public class App {
  public static void main(String[] args) {
    Map<String, String> settings = SettingsFileReader.readSettings();

    AppConfig config = AppConfig.getInstance();
    config.configure(settings);

    JTextArea logArea = new JTextArea();
    UITextAreaReportConsumer uiConsumer = new UITextAreaReportConsumer(logArea);

    SessionManager sessionManager =
        new SessionManager(config, uiConsumer, new FastFailPolicy());

    ThemeManager.setTheme(new StormyMorningDarkTheme());

    FlatDarkLaf.setup();

    UIManager.put("Button.arc", UIConstants.BORDER_RADIUS);
    UIManager.put("Component.arc", UIConstants.BORDER_RADIUS);

    SwingUtilities.invokeLater(() ->
        new MainFrame(sessionManager, uiConsumer, logArea)
    );
  }
}