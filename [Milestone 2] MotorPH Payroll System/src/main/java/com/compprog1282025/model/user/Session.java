package com.compprog1282025.model.user;

import java.time.LocalDateTime;
import com.compprog1282025.model.employee.Attendance;

// This class represents a user session, containing the user information, session status, and timestamps for session start and end.

public class Session {
    private User user;
    private boolean active;
    private LocalDateTime sessionStart;
    private LocalDateTime sessionEnd;
    private Attendance attendance;

    //NEW CONSTRUCTOR: Matches AuthService call
    public Session(User user) {
        this.user = user;
        this.active = true;
        this.sessionStart = LocalDateTime.now();
        this.sessionEnd = null;
        this.attendance = null;
    }


    // Constructor - initializes the session with the user, active status, session start and end times.
    public Session(User user, boolean active, LocalDateTime sessionStart, LocalDateTime sessionEnd, Attendance attendance) {
        this.user = user;
        this.active = active;
        this.sessionStart = sessionStart;
        this.sessionEnd = sessionEnd;
        this.attendance = attendance;
    }

    // Constructor used during log in - sets active to true and session end to null.
    public Session(User user, LocalDateTime sessionStart) {
        this.user = user;
        this.active = true;
        this.sessionStart = sessionStart;
        this.sessionEnd = null;
        this.attendance = null;
    }

    // constructor to create and return an empty session
    public Session(boolean active) {
    	this.user = null;
    	this.active = active;
    	this.sessionStart = null;
    	this.sessionEnd = null;
    }

    // Getters and setters
    public User getUser() {
        return user;
    }
    public void setUser(User user) {
    	this.user = user;
    }

    public boolean isActive() {
        return active;
    }
    public void setActive(boolean active) {
    	this.active = active;
    }

    public LocalDateTime getSessionStart() {
        return sessionStart;
    }
    public void setSessionStart(LocalDateTime sessionStart) {
    	this.sessionStart = sessionStart;
    }

    public LocalDateTime getSessionEnd() {
        return sessionEnd;
    }
    public void setSessionEnd(LocalDateTime sessionEnd) {
    	this.sessionEnd = sessionEnd;
    }

    public Attendance getAttendance() {
    	return attendance;
    }
    public void setAttendance(Attendance attendance) {
    	this.attendance = attendance;
    }




// Invalidate Session - sets active to false and records the session end time.
    public void invalidateSession() {
        active = false;
        sessionEnd = LocalDateTime.now();
    }

}
