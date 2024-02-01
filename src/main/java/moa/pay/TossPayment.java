package moa.pay;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import java.util.Date;

public record TossPayment(
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
        Object virtualAccount,
        Object transfer,
        Object mobilePhone,
        Object giftCertificate,
        Object cashReceipt,
        Object cashReceipts,
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
        int taxExemptionAmount,

        @Embedded
        Card card,

        @Embedded
        Receipt receipt,

        @Embedded
        Checkout checkout
) {

    public record Card(
            @Column(name = "card_amount")
            int amount,

            @Column(name = "card_issuer_code")
            String issuerCode,

            @Column(name = "card_acquirer_code")
            String acquirerCode,

            @Column(name = "card_number")
            String number,

            @Column(name = "card_installment_plan_months")
            int installmentPlanMonths,

            @Column(name = "card_is_interest_free")
            boolean isInterestFree,

            @Column(name = "card_interest_payer")
            Object interestPayer,

            @Column(name = "card_approve_no")
            String approveNo,

            @Column(name = "card_use_card_point")
            boolean useCardPoint,

            @Column(name = "card_card_type")
            String cardType,

            @Column(name = "card_owner_type")
            String ownerType,

            @Column(name = "card_acquire_status")
            String acquireStatus
    ) {
    }

    public record Checkout(
            @Column(name = "checkout_url")
            String url
    ) {
    }

    public record Receipt(
            @Column(name = "receipt_url")
            String url
    ) {
    }
}
