package moa.sms;

import org.springframework.stereotype.Component;

@Component
public class SmsMessageFactory {

    private static final String PREFIX = "[모아] ";

    public String generatePhoneVerificationMessage(String verificationNumber) {
        return PREFIX + "인증번호는 [" + verificationNumber + "] 입니다.";
    }

    public String generateFundingFinishMessage(
            String receiverName,
            String productName,
            String expirationPeriod,
            String productDescription
    ) {
        return PREFIX + """
                %s님의 선물 펀딩이 달성되어 주문 상품이 도착했어요🎁
                앞으로도 모아를 통해 모두가 행복한 새로운 선물 문화를 누려보세요🤗
                행복한 하루 보내세요🍀
                                
                - 상품 이름: %s
                - 유효 기간: ~ %s
                - 유의 사항: %s
                """.formatted(receiverName, productName, expirationPeriod, productDescription);
    }
}
