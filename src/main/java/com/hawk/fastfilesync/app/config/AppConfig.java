package com.hawk.fastfilesync.app.config;

import com.hawk.fastfilesync.constants.Constants;
import com.hawk.fastfilesync.cunsumer.ReportConsumer;
import com.hawk.fastfilesync.exception.UnreadableUserSettingsException;
import com.hawk.fastfilesync.filter.ScanFilter;
import com.hawk.fastfilesync.filter.impl.BrokenFilter;
import com.hawk.fastfilesync.filter.impl.HiddenFilter;
import com.hawk.fastfilesync.filter.impl.InvalidNameFilter;
import com.hawk.fastfilesync.filter.impl.SymlinkFilter;
import com.hawk.fastfilesync.scan.FileScanner;
import com.hawk.fastfilesync.scan.impl.StandardStreamScanner;
import com.hawk.fastfilesync.sync.SyncEngine;
import com.hawk.fastfilesync.sync.executor.SyncExecutor;
import com.hawk.fastfilesync.sync.executor.impl.SafeSyncExecutor;
import com.hawk.fastfilesync.sync.policy.ErrorHandlingPolicy;
import com.hawk.fastfilesync.sync.policy.impl.BestEffortPolicy;
import com.hawk.fastfilesync.sync.policy.impl.FastFailPolicy;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Singleton configuration class for the application.
 */
public class AppConfig {
  private static final AppConfig INSTANCE = new AppConfig();

  private final SyncExecutor safeExecutor = new SafeSyncExecutor();
  private final ErrorHandlingPolicy fastFailPolicy = new FastFailPolicy();
  private final ErrorHandlingPolicy bestEffortPolicy = new BestEffortPolicy();
  private final HiddenFilter hiddenFilter = new HiddenFilter();
  private final SymlinkFilter symlinkFilter = new SymlinkFilter();
  private final InvalidNameFilter invalidNameFilter = new InvalidNameFilter();
  private final BrokenFilter brokenFilter = new BrokenFilter();

  private Map<String, String> settings;

  private AppConfig() {}

  /**
   * Returns the singleton instance of {@code AppConfig}.
   *
   * @return the single AppConfig instance
   */
  public static AppConfig getInstance() {
    return INSTANCE;
  }

  /**
   * Configures the application settings.
   * This method must be called before accessing any configured components.
   *
   * @param settings a map of user-defined settings
   */
  public void configure(Map<String, String> settings) {
    this.settings = Map.copyOf(settings);
  }

  /**
   * Returns the {@link SyncExecutor} based on the configured executor type.
   *
   * @return the selected SyncExecutor
   * @throws UnreadableUserSettingsException if the executor setting is unknown
   */
  public SyncExecutor executor() {
    String executor = settings.get(Constants.EXECUTOR);
    if (Constants.SAFE_EXECUTOR.equals(executor)) {
      return safeExecutor;
    }
    throw new UnreadableUserSettingsException("Unknown value: " + executor);
  }

  /**
   * Returns the {@link ErrorHandlingPolicy} based on the configured policy.
   *
   * @return the selected ErrorHandlingPolicy
   * @throws UnreadableUserSettingsException if the error policy setting is unknown
   */
  public ErrorHandlingPolicy errorPolicy() {
    String errPolicy = settings.get(Constants.ERROR_POLICY);
    if (Constants.FAST_FAIL.equals(errPolicy)) {
      return fastFailPolicy;
    } else if (Constants.BEST_EFFORT.equals(errPolicy)) {
      return bestEffortPolicy;
    }
    throw new UnreadableUserSettingsException("Unknown value: " + errPolicy);
  }

  /**
   * Creates a {@link FileScanner} configured with the enabled filters.
   * Reads filter settings from the configuration and adds the corresponding
   * filters to the scanner.
   *
   * @return a configured FileScanner instance
   */
  public FileScanner fileScanner() {
    int hiddenFilter = Integer.parseInt(settings.get(Constants.HIDDEN_FILTER));
    List<ScanFilter> filers = new ArrayList<>();
    if (hiddenFilter == 1) {
      filers.add(this.hiddenFilter);
    }

    int symlinkFilter = Integer.parseInt(settings.get(Constants.SYMLINK_FILTER));
    if (symlinkFilter == 1) {
      filers.add(this.symlinkFilter);
    }

    int invalidNameFilter = Integer.parseInt(settings.get(Constants.INVALID_NAME_FILTER));
    if (invalidNameFilter == 1) {
      filers.add(this.invalidNameFilter);
    }

    int brokenFilter = Integer.parseInt(settings.get(Constants.BROKEN_FILTER));
    if (brokenFilter == 1) {
      filers.add(this.brokenFilter);
    }

    return new StandardStreamScanner(filers);
  }

  /**
   * Creates a new {@link SyncEngine} with the given consumer and error policy.
   *
   * @param consumer the {@link ReportConsumer} that will receive sync reports
   * @param policy the {@link ErrorHandlingPolicy} to apply during synchronization
   * @return a configured SyncEngine instance
   */
  public SyncEngine syncEngine(ReportConsumer consumer,
                               ErrorHandlingPolicy policy) {
    return new SyncEngine(consumer, policy);
  }

}
