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

    JLabel title = new JLabel("Settings");
    title.setFont(UIConstants.TITLE_FONT);
    title.setForeground(ThemeManager.theme().textPrimary());
    title.setAlignmentX(Component.LEFT_ALIGNMENT);

    add(title);
    add(Box.createVerticalStrut(UIConstants.SPACING_L));

    initFields();
    loadSettings();

    add(createRow("Error Policy", errorPolicyBox));
    add(createRow("Executor", executorBox));

    add(Box.createVerticalStrut(UIConstants.SPACING_M));

    add(createRow("Hidden Files", hiddenFilter));
    add(createRow("Symlinks", symlinkFilter));
    add(createRow("Invalid Names", invalidNameFilter));
    add(createRow("Broken Files", brokenFilter));

    add(Box.createVerticalStrut(UIConstants.SPACING_L));

    JButton save = UIComponents.primaryButton("Save");
    save.setAlignmentX(Component.LEFT_ALIGNMENT);

    save.addActionListener(e -> saveSettings());

    add(save);
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

  private JPanel createRow(String labelText, JComponent component) {
    JPanel panel = new JPanel(new BorderLayout());
    panel.setOpaque(false);
    panel.setAlignmentX(Component.LEFT_ALIGNMENT);

    JLabel label = new JLabel(labelText);
    label.setFont(UIConstants.BODY_FONT);
    label.setForeground(ThemeManager.theme().textSecondary());

    panel.add(label, BorderLayout.WEST);
    panel.add(component, BorderLayout.EAST);

    panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

    return panel;
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
      Map<String, String> settings = getStringStringMap();

      mapper.writerWithDefaultPrettyPrinter()
          .writeValue(new File(SETTINGS_PATH), settings);

      JOptionPane.showMessageDialog(this,
          "Settings saved",
          "Success",
          JOptionPane.INFORMATION_MESSAGE);

    } catch (Exception e) {
      JOptionPane.showMessageDialog(this,
          "Failed to save settings: " + e.getMessage(),
          "Error",
          JOptionPane.ERROR_MESSAGE);
    }
  }

  private Map<String, String> getStringStringMap() {
    Map<String, String> settings = new HashMap<>();

    settings.put("error-policy", (String) errorPolicyBox.getSelectedItem());
    settings.put("executor", (String) executorBox.getSelectedItem());

    settings.put("hidden-filter", toBinary((String) hiddenFilter.getSelectedItem()));
    settings.put("symlink-filter", toBinary((String) symlinkFilter.getSelectedItem()));
    settings.put("invalid-name-filter", toBinary((String) invalidNameFilter.getSelectedItem()));
    settings.put("broken-filter", toBinary((String) brokenFilter.getSelectedItem()));
    return settings;
  }
}