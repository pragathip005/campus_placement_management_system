package com.crms.placement.dto;

public class OADispatchResponseDTO {

    private int total;
    private int eligible;
    private int dispatched;
    private int skipped;

    public OADispatchResponseDTO(int total, int eligible, int dispatched, int skipped) {
        this.total = total;
        this.eligible = eligible;
        this.dispatched = dispatched;
        this.skipped = skipped;
    }

    public int getTotal() { return total; }
    public int getEligible() { return eligible; }
    public int getDispatched() { return dispatched; }
    public int getSkipped() { return skipped; }
}