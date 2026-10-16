package com.citypulse.audit.dto;

import com.citypulse.audit.entity.AuditLog;

import java.time.Instant;
import java.util.UUID;

public class AuditLogResponse {

    private UUID id;
    private UUID actorId;
    private String actorEmail;
    private String action;
    private String entityType;
    private UUID entityId;
    private String beforeState;
    private String afterState;
    private String reason;
    private String requestId;
    private String ipAddress;
    private Instant createdAt;

    public AuditLogResponse() {
    }

    public static AuditLogResponse fromEntity(AuditLog entity) {
        AuditLogResponse dto = new AuditLogResponse();
        dto.setId(entity.getId());
        dto.setActorId(entity.getActorId());
        dto.setActorEmail(entity.getActorEmail());
        dto.setAction(entity.getAction());
        dto.setEntityType(entity.getEntityType());
        dto.setEntityId(entity.getEntityId());
        dto.setBeforeState(entity.getBeforeState());
        dto.setAfterState(entity.getAfterState());
        dto.setReason(entity.getReason());
        dto.setRequestId(entity.getRequestId());
        dto.setIpAddress(entity.getIpAddress());
        dto.setCreatedAt(entity.getCreatedAt());
        return dto;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getActorId() {
        return actorId;
    }

    public void setActorId(UUID actorId) {
        this.actorId = actorId;
    }

    public String getActorEmail() {
        return actorEmail;
    }

    public void setActorEmail(String actorEmail) {
        this.actorEmail = actorEmail;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getEntityType() {
        return entityType;
    }

    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }

    public UUID getEntityId() {
        return entityId;
    }

    public void setEntityId(UUID entityId) {
        this.entityId = entityId;
    }

    public String getBeforeState() {
        return beforeState;
    }

    public void setBeforeState(String beforeState) {
        this.beforeState = beforeState;
    }

    public String getAfterState() {
        return afterState;
    }

    public void setAfterState(String afterState) {
        this.afterState = afterState;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
