package com.glancy.backend.llm.parser;

import com.glancy.backend.dto.WordResponse;
import com.glancy.backend.entity.Language;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JacksonWordResponseParserTest {

    @Test
    void parseChineseJson() {
        String json = "{\n" +
                "  \"词条\": \"glow\",\n" +
                "  \"原始输入\": \"glow\",\n" +
                "  \"纠正\": false,\n" +
                "  \"变形\": [],\n" +
                "  \"发音\": {\"英音\": \"/gloʊ/\"},\n" +
                "  \"发音解释\": [\n" +
                "    {\n" +
                "      \"释义\": [\n" +
                "        {\n" +
                "          \"定义\": \"发出柔和的光\",\n" +
                "          \"类别\": \"动词\",\n" +
                "          \"例句\": []\n" +
                "        }\n" +
                "      ]\n" +
                "    }\n" +
                "  ],\n" +
                "  \"常见词组\": []\n" +
                "}";

        JacksonWordResponseParser parser = new JacksonWordResponseParser();
        WordResponse resp = parser.parse(json, "glow", Language.ENGLISH);
        assertEquals("glow", resp.getTerm());
        assertFalse(resp.getDefinitions().isEmpty());
        assertNotNull(resp.getPhonetic());
    }
}
