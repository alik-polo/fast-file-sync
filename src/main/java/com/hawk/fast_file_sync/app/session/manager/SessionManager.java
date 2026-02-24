package com.hawk.fast_file_sync.app.session.manager;

import com.hawk.fast_file_sync.app.config.AppConfig;
import com.hawk.fast_file_sync.app.session.AppSession;
import com.hawk.fast_file_sync.cunsumer.ReportConsumer;
import com.hawk.fast_file_sync.sync.policy.ErrorHandlingPolicy;

public class SessionManager {
  private final AppConfig config;
  private final ReportConsumer reportConsumer;
  private final ErrorHandlingPolicy policy;

  public SessionManager(AppConfig config,
                        ReportConsumer reportConsumer,
                        ErrorHandlingPolicy policy) {
    this.config = config;
    this.reportConsumer = reportConsumer;
    this.policy = policy;
  }

  public AppSession createSession() {
    return new AppSession(config, reportConsumer, policy);
  }

}