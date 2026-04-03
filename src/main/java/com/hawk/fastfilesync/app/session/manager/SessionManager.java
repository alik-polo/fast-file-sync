package com.hawk.fastfilesync.app.session.manager;

import com.hawk.fastfilesync.app.config.AppConfig;
import com.hawk.fastfilesync.app.session.AppSession;
import com.hawk.fastfilesync.app.session.listener.SessionCreatedListener;
import com.hawk.fastfilesync.cunsumer.ReportConsumer;
import java.util.Optional;

/**
 * Manages application sessions, providing creation, cancellation, and access
 * to the current active session.
 */
public class SessionManager {

  private final AppConfig config;
  private final ReportConsumer reportConsumer;

  private AppSession currentSession;
  private SessionCreatedListener listener;

  /**
   * Creates a new {@code SessionManager} with the given configuration and report consumer.
   *
   * @param config the application configuration
   * @param reportConsumer the consumer to receive session reports
   */
  public SessionManager(AppConfig config,
                        ReportConsumer reportConsumer) {
    this.config = config;
    this.reportConsumer = reportConsumer;
  }

  /**
   * Sets a listener to be notified when a new session is created.
   *
   * @param listener the session created listener
   */
  public void setSessionCreatedListener(SessionCreatedListener listener) {
    this.listener = listener;
  }

  /**
   * Creates a new {@link AppSession}, closes any existing session,
   * and notifies the listener if set.
   *
   * @return the newly created AppSession
   */
  public synchronized AppSession createSession() {
    closeCurrentSession();

    AppSession session = new AppSession(config, reportConsumer);
    currentSession = session;

    if (listener != null) {
      listener.onSessionCreated(session);
    }

    return session;
  }

  /**
   * Returns the current active session, if any.
   *
   * @return an {@link Optional} containing the current session, or empty if none
   */
  public Optional<AppSession> getCurrentSession() {
    return Optional.ofNullable(currentSession);
  }

  /**
   * Cancels the current session and releases its resources.
   */
  public synchronized void cancelCurrentSession() {
    closeCurrentSession();
    currentSession = null;
  }

  private void closeCurrentSession() {
    if (currentSession != null) {
      try {
        currentSession.close();
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }
  }
}