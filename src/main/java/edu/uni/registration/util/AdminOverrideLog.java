package edu.uni.registration.util;

import java.time.LocalDateTime;

public class AdminOverrideLog {
    private final String adminId;
    private final String action;
    private final String targetId;
    private final String reason;
    private final LocalDateTime timestamp;

    public AdminOverrideLog(String adminId, String action, String targetId, String reason) {
        this.adminId = adminId;
        this.action = action;
        this.targetId = targetId;
        this.reason = reason;
        this.timestamp = LocalDateTime.now();
    }

    public String getAdminId() {
        return adminId;
    }

    public String getAction() {
        return action;
    }

    public String getTargetId() {
        return targetId;
    }

    public String getReason() {
        return reason;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return String.format("[%s] Admin %s: %s on %s - Reason: %s",
                timestamp, adminId, action, targetId, reason);
    }
}

