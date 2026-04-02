package com.hawk.fast_file_sync.app.session.manager;

import com.hawk.fast_file_sync.app.config.AppConfig;
import com.hawk.fast_file_sync.app.session.AppSession;
import com.hawk.fast_file_sync.app.session.listener.SessionCreatedListener;
import com.hawk.fast_file_sync.cunsumer.ReportConsumer;
import com.hawk.fast_file_sync.sync.policy.ErrorHandlingPolicy;
import java.util.Optional;

public class SessionManager {

  private final AppConfig config;
  private final ReportConsumer reportConsumer;
  private final ErrorHandlingPolicy policy;

  private AppSession currentSession;
  private SessionCreatedListener listener;

  public SessionManager(AppConfig config,
                        ReportConsumer reportConsumer,
                        ErrorHandlingPolicy policy) {
    this.config = config;
    this.reportConsumer = reportConsumer;
    this.policy = policy;
  }

  public void setSessionCreatedListener(SessionCreatedListener listener) {
    this.listener = listener;
  }

  public synchronized AppSession createSession() throws Exception {
    closeCurrentSession();

    AppSession session = new AppSession(config, reportConsumer, policy);
    currentSession = session;

    if (listener != null) {
      listener.onSessionCreated(session);
    }

    return session;
  }

  public Optional<AppSession> getCurrentSession() {
    return Optional.ofNullable(currentSession);
  }

  public synchronized void cancelCurrentSession() {
    closeCurrentSession();
    currentSession = null;
  }

  private void closeCurrentSession() {
    if (currentSession != null) {
      try {
        currentSession.close();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

}