package com.glancy.backend.llm.service;

import com.glancy.backend.dto.WordResponse;
import com.glancy.backend.entity.Language;
import com.glancy.backend.llm.config.LLMConfig;
import com.glancy.backend.llm.llm.LLMClient;
import com.glancy.backend.llm.llm.LLMClientFactory;
import com.glancy.backend.llm.model.ChatMessage;
import com.glancy.backend.llm.parser.WordResponseParser;
import com.glancy.backend.llm.prompt.PromptManager;
import com.glancy.backend.llm.search.SearchContentManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class WordSearcherImplTest {
    private LLMClientFactory factory;
    private LLMClient client;
    private PromptManager promptManager;
    private SearchContentManager searchContentManager;
    private WordResponseParser parser;
    private LLMConfig config;
    private WordSearcherImpl searcher;

    @BeforeEach
    void setUp() {
        factory = mock(LLMClientFactory.class);
        client = mock(LLMClient.class);
        promptManager = mock(PromptManager.class);
        searchContentManager = mock(SearchContentManager.class);
        parser = mock(WordResponseParser.class);

        config = new LLMConfig();
        config.setDefaultClient("openai");
        config.setTemperature(0.3);
        config.setPromptPath("prompts/english_to_chinese.txt");

        searcher = new WordSearcherImpl(factory, config, promptManager, searchContentManager, parser);
    }

    @Test
    void searchDelegatesToClientAndParser() {
        when(searchContentManager.normalize(" Hello ")).thenReturn("hello");
        when(promptManager.loadPrompt("prompts/english_to_chinese.txt")).thenReturn("sys");
        when(factory.get("openai")).thenReturn(client);
        when(client.chat(anyList(), eq(0.3))).thenReturn("{\"term\":\"hello\"}");

        WordResponse expected = new WordResponse(null, "hello", List.of(), Language.ENGLISH, null, null);
        when(parser.parse("{\"term\":\"hello\"}", " Hello ", Language.ENGLISH)).thenReturn(expected);

        WordResponse result = searcher.search(" Hello ", Language.ENGLISH, null);

        assertEquals(expected, result);

        verify(searchContentManager).normalize(" Hello ");
        verify(promptManager).loadPrompt("prompts/english_to_chinese.txt");
        verify(factory).get("openai");
        verify(client).chat(
                eq(List.of(
                        new ChatMessage("system", "sys"),
                        new ChatMessage("user", "hello")
                )),
                eq(0.3)
        );
        verify(parser).parse("{\"term\":\"hello\"}", " Hello ", Language.ENGLISH);
    }
}
