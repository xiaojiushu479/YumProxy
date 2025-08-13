package yumProxy.server.timestamp;

import java.sql.Timestamp;


public class TimestampInfo {
    public String username;
    public Timestamp activatedAt;
    public Timestamp expiresAt;
    public boolean isActive;
    public boolean isExpired;
    
    public TimestampInfo() {}
    
    public TimestampInfo(String username, Timestamp activatedAt, Timestamp expiresAt, boolean isActive) {
        this.username = username;
        this.activatedAt = activatedAt;
        this.expiresAt = expiresAt;
        this.isActive = isActive;
        this.isExpired = false;
    }
    
    @Override
    public String toString() {
        return String.format("TimestampInfo{username='%s', activatedAt=%s, expiresAt=%s, isActive=%s, isExpired=%s}",
                username, activatedAt, expiresAt, isActive, isExpired);
    }
} 
