package moa;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import moa.config.TestInfraConfig;
import moa.support.DataClearExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@Target(TYPE)
@Retention(RUNTIME)
@SpringBootTest
@SpringBatchTest
@ExtendWith(DataClearExtension.class)
@Import({TestInfraConfig.class})
public @interface BatchTest {
}
