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

package com.alibaba.cloud.ai.example.rag.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.PromptChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.QuestionAnswerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.ai.vectorstore.milvus.MilvusVectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PromptConfiguration {

    @Autowired
    private MilvusVectorStore vectorStore;

    @Bean
    ChatClient chatClient(ChatClient.Builder builder,ChatMemory chatMemory) {
        return builder
                .defaultSystem(
                 """
                 你是我的企业知识库AI客服助手，请根据企业知识库内容帮我解答我提出的相关问题
                 
                 请说中文
                 今天的日期是{current_date}
                 
                 """

                ).defaultAdvisors(
                        //每次对话带上上下文
                        new PromptChatMemoryAdvisor(chatMemory)
                        //从向量数据库中查找
                        , new QuestionAnswerAdvisor(vectorStore)
                )
                .build();
    }

    /*每次对话带上下文*/
    @Bean
    public ChatMemory chatMemory() {
        return new InMemoryChatMemory();
    }

}