package tn.pharmaconnect.dto;

public class DashboardStatsDTO {
    private long totalMedicines;
    private long lowStock;
    private long pendingRx;
    private long todayOrders;

    public DashboardStatsDTO(long totalMedicines, long lowStock, long pendingRx, long todayOrders) {
        this.totalMedicines = totalMedicines;
        this.lowStock = lowStock;
        this.pendingRx = pendingRx;
        this.todayOrders = todayOrders;
    }

    public long getTotalMedicines() {
        return totalMedicines;
    }

    public long getLowStock() {
        return lowStock;
    }

    public long getPendingRx() {
        return pendingRx;
    }

    public long getTodayOrders() {
        return todayOrders;
    }
}
