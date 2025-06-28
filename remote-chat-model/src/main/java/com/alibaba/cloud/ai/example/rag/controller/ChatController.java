/**
 * @Author  千企AI
 *
 * 千企AI是集大模型能力接入、使用、管理于一体的一站式AI应用开发平台， 集成业界主流的对话模型、嵌入模型以及多模态模型，
 *
 * 为用户提供智能知识库、客服、聊天助手、智能体、工作流、以及文生图等AI能力。 平台致力于降低AI能力使用门槛，
 *
 * 让更多的中小企业可以低成本甚至0成本落地AI能力，满足公司各种业务场景的需要。
 *
 * 体验网址： http://ai.cnaider.cn/
 *
 * @Description
 *
 *  平台源码已开源，版本持续更新中
 *  https://gitee.com/aiqianqi/thousand-enterprises-ai/
 *
 * @Contact
 *
 * 如需试用或者技术支持，欢迎联系微信： qianqimodel 或 kemodel
 *
 */

package com.alibaba.cloud.ai.example.rag.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.QuestionAnswerAdvisor;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.milvus.MilvusVectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY;
import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_RETRIEVE_SIZE_KEY;


@RestController
@Slf4j
@RequestMapping("/ai")
@RequiredArgsConstructor
public class ChatController {

    @Autowired
    private ChatClient chatClient;

    @Autowired
    private MilvusVectorStore vectorStore;

    private final ChatModel dashScopeChatModel;

    // 历史消息列表
    static List<Message> historyMessage = new ArrayList<>();
    // 历史消息列表的最大长度
    static int maxLen = 10;

    @GetMapping(value = "/chat", produces = "text/plain; charset=UTF-8")
    public String generation(String userInput) {
        // 发起聊天请求并处理响应

        System.out.println("userInput==" + userInput);

        String output = chatClient.prompt()
                .messages(historyMessage)
                .user(userInput)
                .system(promptSystemSpec -> promptSystemSpec.param("current_date", LocalDate.now().toString()))
                .advisors(new QuestionAnswerAdvisor(vectorStore, SearchRequest.builder().build()))
                .call()
                .content();
        // 用户输入的文本是UserMessage
        historyMessage.add(new UserMessage(userInput));
        // 发给AI前对历史消息对列的长度进行检查
        if (historyMessage.size() > maxLen) {
            historyMessage = historyMessage.subList(historyMessage.size() - maxLen - 1, historyMessage.size());
        }
        return output;
    }


    @PostMapping(value = "/knowledge", produces = "text/plain; charset=UTF-8")
    public String chatWithDb(String userInput) {
        // 发起聊天请求并处理响应
        String output = chatClient.prompt()
                .messages(historyMessage)
                .user(userInput)
                .system(promptSystemSpec -> promptSystemSpec.param("current_date", LocalDate.now().toString()))
                .advisors(advisorSpec -> advisorSpec.param(AbstractChatMemoryAdvisor.CHAT_MEMORY_RETRIEVE_SIZE_KEY,100))
                .advisors(new QuestionAnswerAdvisor(vectorStore, SearchRequest.builder().build()))
                .call()
                .content();
        return output;
    }

    /**
     * 流式返回
     * @param message
     * @return
     */
    @GetMapping(value="/stream",produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> generateStream(
            @RequestParam(value = "chatId", defaultValue = "10086") String chatId,
            @RequestParam(value = "message", defaultValue = "讲个笑话") String message

    ) throws InterruptedException {
        System.out.println("chatId==" + chatId);
        System.out.println("message==" + message);
        Flux<String> content = chatClient.prompt()
                .user(message)
                //给提示词中传递参数
                .system(promptSystemSpec -> promptSystemSpec.param("current_date", LocalDate.now().toString()))
                //设置上下文中对话记录的数量
                .advisors(advisorSpec -> advisorSpec.param(AbstractChatMemoryAdvisor.CHAT_MEMORY_RETRIEVE_SIZE_KEY,100))
                .advisors(a -> a.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId).param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 100))
                .advisors(new QuestionAnswerAdvisor(vectorStore, SearchRequest.builder().build()))
                .stream()
                .content();

        return  content.concatWith(Flux.just("[complete]"));
    }

}