package com.hawk.fast_file_sync.app.session.manager;

import com.hawk.fast_file_sync.app.config.AppConfig;
import com.hawk.fast_file_sync.app.session.AppSession;

public class SessionManager {
  private final AppConfig config;

  public SessionManager(AppConfig config) {
    this.config = config;
  }

  public AppSession createSession() {
    return new AppSession(config);
  }

}