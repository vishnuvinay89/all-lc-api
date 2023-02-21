package org.ekstep.language.measures;


import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@Slf4j
@SpringBootTest
public class WordTests {

    @Test
    void testWord() throws Exception {
        log.info("test word: {}", new ObjectMapper().writeValueAsString(WordMeasures.getWordComplexity("hi", "अच्छा")));
        log.info("test word: {}", new ObjectMapper().writeValueAsString(WordMeasures.getWordComplexity("hi", "मिलकर")));
        log.info("test word: {}", new ObjectMapper().writeValueAsString(WordMeasures.getWordComplexity("hi", "लगा")));
    }

    @Test
    void testParagraph() throws Exception {
        log.info("test para: {}", new ObjectMapper().writeValueAsString(ParagraphMeasures.getTextComplexity("hi", "आपसे मिलकर अच्छा लगा", null)));
    }
}
