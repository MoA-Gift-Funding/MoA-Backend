package moa.pay;
import java.time.OffsetDateTime;

public record TossPaymentResponse(
        String mId,
        String version,
        String lastTransactionKey,
        String paymentKey,
        String orderId,
        String orderName,
        String currency,
        String method,
        String status,
        OffsetDateTime requestedAt,
        OffsetDateTime approvedAt,
        boolean useEscrow,
        boolean cultureExpense,
        Card card,
        Receipt receipt,
        Checkout checkout,
        String type,
        String country,
        Failure failure,
        int totalAmount,
        int balanceAmount,
        int suppliedAmount,
        int vat,
        int taxFreeAmount,
        int taxExemptionAmount
) {
    public record Card(
            int amount,
            String issuerCode,
            String acquirerCode,
            String number,
            int installmentPlanMonths,
            boolean isInterestFree,
            String interestPayer,
            String approveNo,
            boolean useCardPoint,
            String cardType,
            String ownerType,
            String acquireStatus
    ) {
    }

    public record Receipt(String url) {
    }

    public record Checkout(String url) {
    }

    public record Failure(
            String code,
            String message
    ) {
    }
}
