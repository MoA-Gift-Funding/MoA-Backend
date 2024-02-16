package moa.sms;

import org.springframework.stereotype.Component;

@Component
public class SmsMessageFactory {

    private static final String PREFIX = "[모아] ";

    public String generatePhoneVerificationMessage(String verificationNumber) {
        return PREFIX + "인증번호는 [" + verificationNumber + "] 입니다.";
    }

    public String generateFundingFinishMessage(
            String title,
            String productName,
            String link
    ) {
        return PREFIX + """
                등록하신 펀딩 [%s]이 달성 완료됐어요!
                다음 링크를 통해 정보를 입력하고 [%s]을(를) 수령해주세요 🥰
                                
                수령하러 가기 🎁
                %s
                """.formatted(title, productName, link);
    }
}
