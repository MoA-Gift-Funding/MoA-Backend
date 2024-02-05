package study;

import java.time.LocalDate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;

@DisplayName("LocalData 학습 테스트")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
public class LocalDataStudyTest {

    @Test
    @DisplayName("1월 31일에서 한달을 더하면 2월 29일이 된다.")
    void plusMonthTest() {
        // when
        LocalDate result = LocalDate.of(2024, 1, 31).plusMonths(1);

        // then
        System.out.println(result);
    }
}
