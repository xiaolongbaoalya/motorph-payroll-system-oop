package com.compprog1282025.service; // Only keep THIS one

import com.compprog1282025.model.user.Session;

// This class acts as the "Global Memory" for the app.
// It uses the Singleton Pattern to ensure only one session exists at a time.
public class SessionContext {
    private static SessionContext instance;
    private Session currentSession;

    private SessionContext() {} // Private constructor

    public static SessionContext getInstance() {
        if (instance == null) {
            instance = new SessionContext();
        }
        return instance;
    }

    public void setCurrentSession(Session session) {
        this.currentSession = session;
    }

    public Session getCurrentSession() {
        return currentSession;
    }

    public void clear() {
        this.currentSession = null;
    }
}