package moa.pay;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import java.util.Date;

@Entity
public record TossPaymentObject(
        @Id
        String mId,
        String version,
        String lastTransactionKey,
        String paymentKey,
        String orderId,
        String orderName,
        String currency,
        String method,
        String status,
        Date requestedAt,
        Date approvedAt,
        boolean useEscrow,
        boolean cultureExpense,
        Card card,
        Object virtualAccount,
        Object transfer,
        Object mobilePhone,
        Object giftCertificate,
        Object cashReceipt,
        Object cashReceipts,
        Receipt receipt,
        Checkout checkout,
        Object discount,
        Object cancels,
        Object secret,
        String type,
        Object easyPay,
        String country,
        Object failure,
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
            Object interestPayer,
            String approveNo,
            boolean useCardPoint,
            String cardType,
            String ownerType,
            String acquireStatus
    ) {
    }

    public record Checkout(
            String url
    ) {
    }

    public record Receipt(
            String url
    ) {
    }
}
