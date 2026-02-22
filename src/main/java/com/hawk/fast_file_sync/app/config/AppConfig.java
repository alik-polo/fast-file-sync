package com.hawk.fast_file_sync.app.config;

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
import com.hawk.fast_file_sync.sync.policy.impl.FastFailPolicy;
import java.util.List;

public class AppConfig {
  private final SyncExecutor syncExecutor = new SafeSyncExecutor();
  private final SyncEngine syncEngine = new SyncEngine(new FastFailPolicy());
  private final HiddenFilter hiddenFilter = new HiddenFilter();
  private final SymlinkFilter symlinkFilter = new SymlinkFilter();
  private final InvalidNameFilter invalidNameFilter = new InvalidNameFilter();
  private final BrokenFilter brokenFilter = new BrokenFilter();

  public FileScanner fileScanner(List<ScanFilter> filters) {
    return new StandardStreamScanner(filters);
  }

  public SyncExecutor syncExecutor() {
    return syncExecutor;
  }

  public SyncEngine syncEngine() {
    return syncEngine;
  }

  public ScanFilter hiddenFiler() {
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

}
