package com.hawk.fast_file_sync.reader;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.HashMap;
import java.util.Map;

public class SettingsFileReader {

  private static final String SETTINGS_FILE = "settings.json";
  private static final ObjectMapper mapper = new ObjectMapper();

  public static Map<String, String> readSettings() {
    File file = new File(SETTINGS_FILE);

    try {
      if (file.exists()) {
        return mapper.readValue(file, new TypeReference<>() {});
      }

      Map<String, String> defaults = defaultSettings();

      mapper.writerWithDefaultPrettyPrinter()
          .writeValue(file, defaults);

      return defaults;

    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  private static Map<String, String> defaultSettings() {
    Map<String, String> map = new HashMap<>();

    map.put("error-policy", "FAST_FAIL");
    map.put("executor", "SAFE_EXECUTOR");
    map.put("hidden-filter", "0");
    map.put("symlink-filter", "0");
    map.put("invalid-name-filter", "0");
    map.put("broken-filter", "0");

    return map;
  }
}