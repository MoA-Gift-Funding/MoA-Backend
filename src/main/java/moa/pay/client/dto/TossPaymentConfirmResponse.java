package moa.pay.client.dto;

import java.util.List;
import moa.pay.domain.TossPayment;

public record TossPaymentConfirmResponse(
        String version,
        String paymentKey,
        String type,
        String orderId,
        String orderName,
        String mId,
        String currency,
        String method,
        String totalAmount,
        String balanceAmount,
        String status,
        String requestedAt,
        String approvedAt,
        boolean useEscrow,
        String lastTransactionKey,
        String suppliedAmount,
        String vat,
        boolean cultureExpense,
        String taxFreeAmount,
        String taxExemptionAmount,
        List<Cancel> cancels,
        boolean isPartialCancelable,
        Card card,
        VirtualAccount virtualAccount,
        String secret,
        MobilePhone mobilePhone,
        GiftCertificate giftCertificate,
        Transfer transfer,
        Receipt receipt,
        Checkout checkout,
        EasyPay easyPay,
        String country,
        Failure failure,
        CashReceipt cashReceipt,
        List<CashReceipts> cashReceipts,
        Discount discount
) {
    public TossPayment toPayment(Long memberId) {
        return new TossPayment(
                paymentKey,
                orderId,
                orderName,
                totalAmount,
                memberId
        );
    }

    public record Cancel(
            String cancelAmount,
            String cancelReason,
            String taxFreeAmount,
            String taxExemptionAmount,
            String refundableAmount,
            String easyPayDiscountAmount,
            String canceledAt,
            String transactionKey,
            String receiptKey
    ) {
    }

    public record Card(
            String amount,
            String issuerCode,
            String acquirerCode,
            String number,
            String installmentPlanMonths,
            String approveNo,
            boolean useCardPoint,
            String cardType,
            String ownerType,
            String acquireStatus,
            boolean isInterestFree,
            String interestPayer
    ) {
    }

    public record VirtualAccount(
            String accountType,
            String accountNumber,
            String bankCode,
            String customerName,
            String dueDate,
            String refundStatus,
            boolean expired,
            String settlementStatus,
            RefundReceiveAccount refundReceiveAccount
    ) {
        public record RefundReceiveAccount(
                String bankCode,
                String accountNumber,
                String holderName
        ) {
        }
    }

    public record MobilePhone(
            String customerMobilePhone,
            String settlementStatus,
            String receiptUrl
    ) {
    }

    public record GiftCertificate(
            String approveNo,
            String settlementStatus
    ) {
    }

    public record Transfer(
            String bankCode,
            String settlementStatus
    ) {
    }

    public record Receipt(String url) {
    }

    public record Checkout(String url) {
    }

    public record EasyPay(
            String provider,
            String amount,
            String discountAmount
    ) {
    }

    public record Failure(
            String code,
            String message
    ) {
    }

    public record CashReceipt(
            String type,
            String receiptKey,
            String issueNumber,
            String receiptUrl,
            String amount,
            String taxFreeAmount
    ) {
    }

    public record CashReceipts(
            String receiptKey,
            String orderId,
            String orderName,
            String type,
            String issueNumber,
            String receiptUrl,
            String businessNumber,
            String transactionType,
            String amount,
            String taxFreeAmount,
            String issueStatus,
            Failure failure,
            String customerIdentityNumber,
            String requestedAt
    ) {
    }

    public record Discount(String amount) {
    }
}
