package com.hawk.fast_file_sync.app.session.listener;

import com.hawk.fast_file_sync.app.session.AppSession;

public interface SessionCreatedListener {
  void onSessionCreated(AppSession session);
}