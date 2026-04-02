package com.hawk.fast_file_sync.ui.card;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hawk.fast_file_sync.reader.SettingsFileReader;
import com.hawk.fast_file_sync.ui.component.UIComponents;
import com.hawk.fast_file_sync.ui.style.UIConstants;
import com.hawk.fast_file_sync.ui.theme.manager.ThemeManager;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class SettingsCard extends BaseCard {

  private static final String SETTINGS_PATH = "settings.json";
  private final ObjectMapper mapper = new ObjectMapper();

  private JComboBox<String> errorPolicyBox;
  private JComboBox<String> executorBox;

  private JComboBox<String> hiddenFilter;
  private JComboBox<String> symlinkFilter;
  private JComboBox<String> invalidNameFilter;
  private JComboBox<String> brokenFilter;

  public SettingsCard() {

    setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

    add(createHeader());
    add(Box.createVerticalStrut(UIConstants.SPACING_L));

    initFields();
    loadSettings();

    add(createSection("Execution", createExecutionSettings()));
    add(Box.createVerticalStrut(UIConstants.SPACING_L));

    add(createSection("Filters", createFilterSettings()));
    add(Box.createVerticalStrut(UIConstants.SPACING_L));

    JButton save = UIComponents.primaryButton("Save Settings");
    save.addActionListener(e -> saveSettings());

    add(save);
  }

  private JComponent createHeader() {
    JLabel title = new JLabel("Settings");
    title.setFont(UIConstants.TITLE_FONT);
    title.setForeground(ThemeManager.theme().textPrimary());
    title.setAlignmentX(Component.LEFT_ALIGNMENT);
    return title;
  }

  private JPanel createSection(String title, JComponent content) {

    JPanel panel = new JPanel();
    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
    panel.setOpaque(false);
    panel.setAlignmentX(Component.LEFT_ALIGNMENT);

    JLabel label = new JLabel(title);
    label.setFont(UIConstants.BODY_FONT);
    label.setForeground(ThemeManager.theme().textSecondary());

    panel.add(label);
    panel.add(Box.createVerticalStrut(10));
    panel.add(content);

    return panel;
  }

  private JPanel createExecutionSettings() {

    JPanel panel = column();

    panel.add(createRow(
        "Error Policy",
        "Stop on first error or continue processing.",
        errorPolicyBox
    ));

    panel.add(Box.createVerticalStrut(UIConstants.SPACING_S));

    panel.add(createRow(
        "Executor",
        "Execution strategy used internally.",
        executorBox
    ));

    return panel;
  }

  private JPanel createFilterSettings() {

    JPanel panel = column();

    panel.add(createRow("Hidden Files", "Skip hidden files", hiddenFilter));
    panel.add(Box.createVerticalStrut(UIConstants.SPACING_S));

    panel.add(createRow("Symlinks", "Skip symbolic links", symlinkFilter));
    addSpacer(panel);

    panel.add(createRow("Invalid Names", "Skip invalid file names", invalidNameFilter));
    addSpacer(panel);

    panel.add(createRow("Broken Files", "Skip corrupted files", brokenFilter));

    return panel;
  }

  private JPanel createRow(String title, String description, JComponent component) {

    JPanel row = new JPanel(new BorderLayout());
    row.setOpaque(false);
    row.setAlignmentX(Component.LEFT_ALIGNMENT);

    JPanel text = new JPanel();
    text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));
    text.setOpaque(false);

    JLabel label = new JLabel(title);
    label.setFont(UIConstants.BODY_FONT);
    label.setForeground(ThemeManager.theme().textPrimary());

    JLabel desc = new JLabel(description);
    desc.setFont(new Font("Dialog", Font.PLAIN, 12));
    desc.setForeground(ThemeManager.theme().textMuted());

    text.add(label);
    text.add(Box.createVerticalStrut(2));
    text.add(desc);

    row.add(text, BorderLayout.WEST);
    row.add(component, BorderLayout.EAST);

    row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

    return row;
  }

  private JPanel column() {
    JPanel panel = new JPanel();
    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
    panel.setOpaque(false);
    panel.setAlignmentX(Component.LEFT_ALIGNMENT);
    return panel;
  }

  private void addSpacer(JPanel panel) {
    panel.add(Box.createVerticalStrut(UIConstants.SPACING_S));
  }

  private void initFields() {
    errorPolicyBox = createCombo(new String[]{"FAST_FAIL", "BEST_EFFORT"});
    executorBox = createCombo(new String[]{"SAFE_EXECUTOR"});

    hiddenFilter = createYesNo();
    symlinkFilter = createYesNo();
    invalidNameFilter = createYesNo();
    brokenFilter = createYesNo();
  }

  private JComboBox<String> createCombo(String[] values) {
    JComboBox<String> box = new JComboBox<>(values);
    styleCombo(box);
    return box;
  }

  private JComboBox<String> createYesNo() {
    return createCombo(new String[]{"NO", "YES"});
  }

  private void styleCombo(JComboBox<String> box) {
    box.setFocusable(false);
    box.setBackground(ThemeManager.theme().inputBackground());
    box.setForeground(ThemeManager.theme().textPrimary());
    box.setBorder(BorderFactory.createLineBorder(
        ThemeManager.theme().inputBorder(), 1));
  }

  private void loadSettings() {
    try {
      Map<String, String> settings = SettingsFileReader.readSettings();

      errorPolicyBox.setSelectedItem(settings.getOrDefault("error-policy", "FAST_FAIL"));
      executorBox.setSelectedItem(settings.getOrDefault("executor", "SAFE_EXECUTOR"));

      hiddenFilter.setSelectedItem(toYesNo(settings.get("hidden-filter")));
      symlinkFilter.setSelectedItem(toYesNo(settings.get("symlink-filter")));
      invalidNameFilter.setSelectedItem(toYesNo(settings.get("invalid-name-filter")));
      brokenFilter.setSelectedItem(toYesNo(settings.get("broken-filter")));

    } catch (Exception e) {
      JOptionPane.showMessageDialog(this,
          "Failed to load settings: " + e.getMessage(),
          "Error",
          JOptionPane.ERROR_MESSAGE);
    }
  }

  private String toYesNo(String value) {
    return "1".equals(value) ? "YES" : "NO";
  }

  private String toBinary(String value) {
    return "YES".equals(value) ? "1" : "0";
  }

  private void saveSettings() {
    try {
      Map<String, String> settings = getSettingsMap();

      mapper.writerWithDefaultPrettyPrinter()
          .writeValue(new File(SETTINGS_PATH), settings);

      int result = JOptionPane.showConfirmDialog(
          this,
          "Settings saved. Restart application now?",
          "Restart Required",
          JOptionPane.YES_NO_OPTION
      );

      if (result == JOptionPane.YES_OPTION) {
        restartApplication();
      }

    } catch (Exception e) {
      JOptionPane.showMessageDialog(this,
          "Failed to save settings: " + e.getMessage(),
          "Error",
          JOptionPane.ERROR_MESSAGE);
    }
  }

  private Map<String, String> getSettingsMap() {
    Map<String, String> settings = new HashMap<>();

    settings.put("error-policy", (String) errorPolicyBox.getSelectedItem());
    settings.put("executor", (String) executorBox.getSelectedItem());

    settings.put("hidden-filter", toBinary((String) hiddenFilter.getSelectedItem()));
    settings.put("symlink-filter", toBinary((String) symlinkFilter.getSelectedItem()));
    settings.put("invalid-name-filter", toBinary((String) invalidNameFilter.getSelectedItem()));
    settings.put("broken-filter", toBinary((String) brokenFilter.getSelectedItem()));

    return settings;
  }

  private void restartApplication() {
    try {
      String java = System.getProperty("java.home") + "/bin/java";

      String jarPath = new File(
          SettingsCard.class.getProtectionDomain()
              .getCodeSource()
              .getLocation()
              .toURI()
      ).getPath();

      new ProcessBuilder(java, "-jar", jarPath).start();
      System.exit(0);

    } catch (Exception e) {
      JOptionPane.showMessageDialog(this,
          "Failed to restart application: " + e.getMessage(),
          "Error",
          JOptionPane.ERROR_MESSAGE);
    }
  }
}