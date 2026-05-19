package tn.pharmaconnect.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class CommandeDTO {
    private Integer id;
    private String code;
    private LocalDate date;
    private String status;
    private BigDecimal total;
    private String items;

    public CommandeDTO() {
    }

    public CommandeDTO(Integer id, String code, LocalDate date, String status, BigDecimal total, String items) {
        this.id = id;
        this.code = code;
        this.date = date;
        this.status = status;
        this.total = total;
        this.items = items;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public String getItems() {
        return items;
    }

    public void setItems(String items) {
        this.items = items;
    }
}
