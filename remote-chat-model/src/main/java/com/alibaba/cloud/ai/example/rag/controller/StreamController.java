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

import com.alibaba.cloud.ai.example.rag.service.StreamService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;


@RequestMapping("/api/assistant")
@RestController
public class StreamController {

    private final StreamService agent;

    public StreamController(StreamService agent) {
        this.agent = agent;
    }

    @RequestMapping(path="/chat", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
        public Flux<String> chat(String chatId, String userMessage) {
        return agent.chat(chatId, userMessage);
    }

}