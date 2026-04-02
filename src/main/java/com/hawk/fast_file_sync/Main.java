package com.hawk.fast_file_sync;

import com.formdev.flatlaf.FlatDarkLaf;
import com.hawk.fast_file_sync.app.config.AppConfig;
import com.hawk.fast_file_sync.app.session.manager.SessionManager;
import com.hawk.fast_file_sync.cunsumer.impl.UITextAreaReportConsumer;
import com.hawk.fast_file_sync.reader.SettingsFileReader;
import com.hawk.fast_file_sync.sync.policy.impl.FastFailPolicy;
import com.hawk.fast_file_sync.ui.MainFrame;
import com.hawk.fast_file_sync.ui.style.UIConstants;
import com.hawk.fast_file_sync.ui.theme.manager.ThemeManager;
import com.hawk.fast_file_sync.ui.theme.mod.StormyMorningDarkTheme;

import javax.swing.*;
import java.util.Map;

public class Main {
  public static void main(String[] args) {
    printMemory("START");

    Map<String, String> settings = SettingsFileReader.readSettings();

    AppConfig config = AppConfig.getInstance();
    config.configure(settings);

    JTextPane logPlane = new JTextPane();
    UITextAreaReportConsumer uiConsumer = new UITextAreaReportConsumer(logPlane);

    SessionManager sessionManager =
        new SessionManager(config, uiConsumer);

    ThemeManager.setTheme(new StormyMorningDarkTheme());

    FlatDarkLaf.setup();

    UIManager.put("Button.arc", UIConstants.BORDER_RADIUS);
    UIManager.put("Component.arc", UIConstants.BORDER_RADIUS);

    SwingUtilities.invokeLater(() ->
        new MainFrame(sessionManager, uiConsumer, logPlane).setVisible(true)
    );

    printMemory("STOP");
  }

  private static void printMemory(String step) {
    Runtime rt = Runtime.getRuntime();

    long used = rt.totalMemory() - rt.freeMemory();

    System.out.println(step);
    System.out.println("Used: " + used / 1024 / 1024 + " MB" + " | " + used / 1024 + " KB");
    System.out.println("Total: " + rt.totalMemory() / 1024 / 1024 + " MB");
    System.out.println("Max: " + rt.maxMemory() / 1024 / 1024 + " MB");
    System.out.println("------");
  }
}