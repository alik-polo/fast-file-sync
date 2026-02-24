package com.hawk.fast_file_sync.app.config;

import com.hawk.fast_file_sync.cunsumer.ReportConsumer;
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
import java.util.List;

public class AppConfig {

  private static final AppConfig INSTANCE = new AppConfig();

  private final SyncExecutor syncExecutor = new SafeSyncExecutor();
  private final HiddenFilter hiddenFilter = new HiddenFilter();
  private final SymlinkFilter symlinkFilter = new SymlinkFilter();
  private final InvalidNameFilter invalidNameFilter = new InvalidNameFilter();
  private final BrokenFilter brokenFilter = new BrokenFilter();

  private AppConfig() {}

  public static AppConfig getInstance() {
    return INSTANCE;
  }

  public SyncExecutor syncExecutor() {
    return syncExecutor;
  }

  public ScanFilter hiddenFilter() {
    return hiddenFilter;
  }

  public ScanFilter symlinkFilter() {
    return symlinkFilter;
  }

  public InvalidNameFilter invalidNameFilter() {
    return invalidNameFilter;
  }

  public BrokenFilter brokenFilter() {
    return brokenFilter;
  }

  public FileScanner fileScanner(List<ScanFilter> filters) {
    return new StandardStreamScanner(filters);
  }

  public SyncEngine syncEngine(ReportConsumer consumer, ErrorHandlingPolicy policy) {
    return new SyncEngine(consumer, policy);
  }
}