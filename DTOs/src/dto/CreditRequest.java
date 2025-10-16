package dto;

public final class CreditRequest {
    private final int amount;

    public CreditRequest(int charge) {
        this.amount = charge;
    }

    public int getAmount() {
        return amount;
    }
}