package com.hawk.fastfilesync.ui.card;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hawk.fastfilesync.reader.SettingsFileReader;
import com.hawk.fastfilesync.ui.component.UiComponents;
import com.hawk.fastfilesync.ui.style.UiConstants;
import com.hawk.fastfilesync.ui.theme.manager.ThemeManager;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/**
 * Card component for displaying and editing application settings.
 */
public class SettingsCard extends BaseCard {

  private static final String SETTINGS_PATH = "settings.json";
  private final ObjectMapper mapper = new ObjectMapper();

  private JComboBox<String> errorPolicyBox;
  private JComboBox<String> executorBox;

  private JComboBox<String> hiddenFilter;
  private JComboBox<String> symlinkFilter;
  private JComboBox<String> invalidNameFilter;
  private JComboBox<String> brokenFilter;

  /**
   * Constructs a SettingsCard and initializes UI components and settings.
   */
  public SettingsCard() {

    setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

    add(createHeader());
    add(Box.createVerticalStrut(UiConstants.SPACING_L));

    initFields();
    loadSettings();

    add(createSection("Execution", createExecutionSettings()));
    add(Box.createVerticalStrut(UiConstants.SPACING_L));

    add(createSection("Filters", createFilterSettings()));
    add(Box.createVerticalStrut(UiConstants.SPACING_L));

    JButton save = UiComponents.primaryButton("Save Settings");
    save.addActionListener(e -> saveSettings());

    add(save);
  }

  /**
   * Creates the header component for the settings card.
   *
   * @return a JComponent containing the header
   */
  private JComponent createHeader() {
    JLabel title = new JLabel("Settings");
    title.setFont(UiConstants.TITLE_FONT);
    title.setForeground(ThemeManager.theme().textPrimary());
    title.setAlignmentX(LEFT_ALIGNMENT);
    return title;
  }

  /**
   * Creates a section panel with a title and content.
   *
   * @param title the section title
   * @param content the section content component
   * @return a JPanel representing the section
   */
  private JPanel createSection(String title, JComponent content) {

    JPanel panel = new JPanel();
    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
    panel.setOpaque(false);
    panel.setAlignmentX(LEFT_ALIGNMENT);

    JLabel label = new JLabel(title);
    label.setFont(UiConstants.BODY_FONT);
    label.setForeground(ThemeManager.theme().textSecondary());

    panel.add(label);
    panel.add(Box.createVerticalStrut(10));
    panel.add(content);

    return panel;
  }

  /**
   * Creates UI components for execution-related settings.
   *
   * @return a JPanel containing execution settings
   */
  private JPanel createExecutionSettings() {

    JPanel panel = column();

    panel.add(createRow(
        "Error Policy",
        "Stop on first error or continue processing.",
        errorPolicyBox
    ));

    panel.add(Box.createVerticalStrut(UiConstants.SPACING_S));

    panel.add(createRow(
        "Executor",
        "Execution strategy used internally.",
        executorBox
    ));

    return panel;
  }

  /**
   * Creates UI components for file filter settings.
   *
   * @return a JPanel containing filter settings
   */
  private JPanel createFilterSettings() {

    JPanel panel = column();

    panel.add(createRow("Hidden Files", "Skip hidden files", hiddenFilter));
    panel.add(Box.createVerticalStrut(UiConstants.SPACING_S));

    panel.add(createRow("Symlinks", "Skip symbolic links", symlinkFilter));
    addSpacer(panel);

    panel.add(createRow("Invalid Names", "Skip invalid file names", invalidNameFilter));
    addSpacer(panel);

    panel.add(createRow("Broken Files", "Skip corrupted files", brokenFilter));

    return panel;
  }

  /**
   * Creates a row with a label, description, and input component.
   *
   * @param title the row label
   * @param description the row description
   * @param component the input component
   * @return a JPanel representing the row
   */
  private JPanel createRow(String title, String description, JComponent component) {

    JPanel row = new JPanel(new BorderLayout());
    row.setOpaque(false);
    row.setAlignmentX(LEFT_ALIGNMENT);

    JPanel text = new JPanel();
    text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));
    text.setOpaque(false);

    JLabel label = new JLabel(title);
    label.setFont(UiConstants.BODY_FONT);
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

  /**
   * Creates a vertical column panel.
   *
   * @return a JPanel with vertical BoxLayout
   */
  private JPanel column() {
    JPanel panel = new JPanel();
    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
    panel.setOpaque(false);
    panel.setAlignmentX(LEFT_ALIGNMENT);
    return panel;
  }

  /**
   * Adds vertical spacing to a panel.
   *
   * @param panel the panel to add spacing
   */
  private void addSpacer(JPanel panel) {
    panel.add(Box.createVerticalStrut(UiConstants.SPACING_S));
  }

  /**
   * Initializes combo box fields for settings.
   */
  private void initFields() {
    errorPolicyBox = createCombo(new String[]{"FAST_FAIL", "BEST_EFFORT"});
    executorBox = createCombo(new String[]{"SAFE_EXECUTOR"});

    hiddenFilter = createYesNo();
    symlinkFilter = createYesNo();
    invalidNameFilter = createYesNo();
    brokenFilter = createYesNo();
  }

  /**
   * Creates a combo box with the given values.
   *
   * @param values the combo box options
   * @return a styled JComboBox
   */
  private JComboBox<String> createCombo(String[] values) {
    JComboBox<String> box = new JComboBox<>(values);
    styleCombo(box);
    return box;
  }

  /**
   * Creates a yes/no combo box.
   *
   * @return a JComboBox with YES/NO options
   */
  private JComboBox<String> createYesNo() {
    return createCombo(new String[]{"NO", "YES"});
  }

  /**
   * Applies styling to a combo box.
   *
   * @param box the JComboBox to style
   */
  private void styleCombo(JComboBox<String> box) {
    box.setFocusable(false);
    box.setBackground(ThemeManager.theme().inputBackground());
    box.setForeground(ThemeManager.theme().textPrimary());
    box.setBorder(BorderFactory.createLineBorder(
        ThemeManager.theme().inputBorder(), 1));
  }

  /**
   * Loads settings from the settings file into UI fields.
   */
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

  /**
   * Converts a binary string to YES/NO.
   *
   * @param value the binary value
   * @return "YES" if 1, otherwise "NO"
   */
  private String toYesNo(String value) {
    return "1".equals(value) ? "YES" : "NO";
  }

  /**
   * Converts YES/NO to binary string.
   *
   * @param value the YES/NO value
   * @return "1" if YES, otherwise "0"
   */
  private String toBinary(String value) {
    return "YES".equals(value) ? "1" : "0";
  }

  /**
   * Saves current settings to the settings file.
   */
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

  /**
   * Retrieves current settings from UI fields as a map.
   *
   * @return a map of settings keys and values
   */
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

  /**
   * Restarts the application programmatically.
   */
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