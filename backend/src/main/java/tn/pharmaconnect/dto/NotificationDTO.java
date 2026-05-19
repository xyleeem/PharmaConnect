package tn.pharmaconnect.dto;

import java.time.LocalDateTime;

public class NotificationDTO {
    private Integer id;
    private String message;
    private String type;
    private LocalDateTime dateEnvoi;
    private Boolean isRead;

    public NotificationDTO() {
    }

    public NotificationDTO(Integer id, String message, String type, LocalDateTime dateEnvoi, Boolean isRead) {
        this.id = id;
        this.message = message;
        this.type = type;
        this.dateEnvoi = dateEnvoi;
        this.isRead = isRead;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public LocalDateTime getDateEnvoi() {
        return dateEnvoi;
    }

    public void setDateEnvoi(LocalDateTime dateEnvoi) {
        this.dateEnvoi = dateEnvoi;
    }

    public Boolean getIsRead() {
        return isRead;
    }

    public void setIsRead(Boolean isRead) {
        this.isRead = isRead;
    }
}
