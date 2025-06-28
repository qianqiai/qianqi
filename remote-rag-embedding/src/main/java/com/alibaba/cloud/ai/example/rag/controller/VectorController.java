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
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.milvus.MilvusVectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
@RestController
@RequestMapping("/ai/vector")
public class VectorController {

    @Autowired
    private MilvusVectorStore vectorStore;

    @Autowired
    private EmbeddingModel embeddingModel;

    @PostMapping("/insert")
    public String insert() {

        List<Document> documents = List.of(
                new Document("14. 准备SpringAIAlibaba需要的Java 17及以上的开发环境。"),
                new Document("15. 使用IDEA进行SpringAIAlibaba的Java开发和HBuilder X进行前端开发。"),
                new Document("16. 在SpringAIAlibaba的Spring Boot项目中集成多种AI模型和向量数据库。"),
                new Document("17. SpringAIAlibaba支持自然语言处理、计算机视觉、语音处理和数据分析与预测功能。"),
                new Document("18. 通过SpringAIAlibaba的配置中心和注册中心实现动态扩展。")
        );
        vectorStore.add(documents);
        return "success";
    }

}