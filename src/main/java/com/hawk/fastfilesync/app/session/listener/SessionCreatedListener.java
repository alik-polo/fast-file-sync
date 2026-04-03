package com.hawk.fastfilesync.app.session.listener;

import com.hawk.fastfilesync.app.session.AppSession;

/**
 * Listener interface for receiving notifications when a new session is created.
 */
public interface SessionCreatedListener {
  /**
   * Invoked when a new {@link AppSession} is created.
   *
   * @param session the newly created session
   */
  void onSessionCreated(AppSession session);
}