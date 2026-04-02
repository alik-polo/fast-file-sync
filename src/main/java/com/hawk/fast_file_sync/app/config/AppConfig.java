package com.hawk.fast_file_sync.app.config;

import com.hawk.fast_file_sync.constants.Constants;
import com.hawk.fast_file_sync.cunsumer.ReportConsumer;
import com.hawk.fast_file_sync.exception.UnreadableUserSettingsException;
import com.hawk.fast_file_sync.filter.ScanFilter;
import com.hawk.fast_file_sync.filter.impl.BrokenFilter;
import com.hawk.fast_file_sync.filter.impl.HiddenFilter;
import com.hawk.fast_file_sync.filter.impl.InvalidNameFilter;
import com.hawk.fast_file_sync.filter.impl.SymlinkFilter;
import com.hawk.fast_file_sync.scan.FileScanner;
import com.hawk.fast_file_sync.scan.impl.StandardStreamScanner;
import com.hawk.fast_file_sync.sync.SyncEngine;
import com.hawk.fast_file_sync.sync.executor.SyncExecutor;
import com.hawk.fast_file_sync.sync.executor.impl.SafeSyncExecutor;
import com.hawk.fast_file_sync.sync.policy.ErrorHandlingPolicy;
import com.hawk.fast_file_sync.sync.policy.impl.FastFailPolicy;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AppConfig {
  private static final AppConfig INSTANCE = new AppConfig();

  private final SyncExecutor safeExecutor = new SafeSyncExecutor();
  private final ErrorHandlingPolicy fastFailPolicy = new FastFailPolicy();
  private final HiddenFilter hiddenFilter = new HiddenFilter();
  private final SymlinkFilter symlinkFilter = new SymlinkFilter();
  private final InvalidNameFilter invalidNameFilter = new InvalidNameFilter();
  private final BrokenFilter brokenFilter = new BrokenFilter();

  private Map<String, String> settings;

  private AppConfig() {}

  public static AppConfig getInstance() {
    return INSTANCE;
  }

  public void configure(Map<String, String> settings) {
    this.settings = settings;
  }

  public SyncExecutor executor() {
    String executor = settings.get(Constants.EXECUTOR);
    if (Constants.SAFE_EXECUTOR.equals(executor)) {
      return safeExecutor;
    }
    throw new UnreadableUserSettingsException("Unknown value: " + executor);
  }

  public ErrorHandlingPolicy errorPolicy() {
    String errPolicy = settings.get(Constants.ERROR_POLICY);
    if (Constants.FAST_FAIL.equals(errPolicy)) {
      return fastFailPolicy;
    }
    throw new UnreadableUserSettingsException("Unknown value: " + errPolicy);
  }

  public FileScanner fileScanner() {
    int hiddenFilter = Integer.parseInt(settings.get(Constants.HIDDEN_FILTER));
    int symlinkFilter = Integer.parseInt(settings.get(Constants.SYMLINK_FILTER));
    int invalidNameFilter = Integer.parseInt(settings.get(Constants.INVALID_NAME_FILTER));
    int brokenFilter = Integer.parseInt(settings.get(Constants.BROKEN_FILTER));

    List<ScanFilter> filers = new ArrayList<>();
    if (hiddenFilter == 1) {
      filers.add(this.hiddenFilter);
    }

    if (symlinkFilter == 1) {
      filers.add(this.symlinkFilter);
    }

    if (invalidNameFilter == 1) {
      filers.add(this.invalidNameFilter);
    }

    if (brokenFilter == 1) {
      filers.add(this.brokenFilter);
    }

    return new StandardStreamScanner(filers);
  }

  public SyncEngine syncEngine(ReportConsumer consumer,
                               ErrorHandlingPolicy policy) {
    return new SyncEngine(consumer, policy);
  }

}
