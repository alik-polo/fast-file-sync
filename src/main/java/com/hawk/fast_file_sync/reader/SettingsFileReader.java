package com.hawk.fast_file_sync.reader;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Resources;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.Map;

public class SettingsFileReader {
  private static final String SETTING_RESOURCE = "settings.json";
  private static final ObjectMapper mapper = new ObjectMapper();

  public static Map<String, String> readSettings() {
    try (InputStream is = Resources.getResource(SETTING_RESOURCE).openStream()) {
      return mapper.readValue(
          is,
          new TypeReference<>() {
          }
      );
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

}
