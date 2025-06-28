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


import com.alibaba.cloud.ai.dashscope.api.DashScopeApi;
import com.alibaba.cloud.ai.dashscope.embedding.DashScopeEmbeddingModel;
import com.alibaba.cloud.ai.dashscope.embedding.DashScopeEmbeddingOptions;
import io.milvus.client.MilvusServiceClient;
import io.milvus.param.ConnectParam;
import io.milvus.param.IndexType;
import io.milvus.param.MetricType;
import org.springframework.ai.document.MetadataMode;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.embedding.TokenCountBatchingStrategy;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.milvus.MilvusVectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class VectorConfig {

    @Value("${zilliz.username}")
    private String username;

    @Value("${zilliz.password}")
    private String password;

    @Value("${zilliz.endpoint}")
    private String endpoint;

    @Value("${spring.ai.dashscope.api-key}")
    private String apiKey;

    @Bean
   public MilvusServiceClient  milvusServiceClient(){
       return new MilvusServiceClient(ConnectParam.newBuilder()
               .withAuthorization(username,password)
               .withUri(endpoint)
               .build());
   }


    /**
     * 有下面这个Bean的时候，就用自定义的向量数据库，没有Bean的时候，就用默认的default/vector_store
     * @param milvusClient
     * @param embeddingModel
     * @return
     */
    @Bean
    public MilvusVectorStore vectorStore(MilvusServiceClient milvusClient, EmbeddingModel embeddingModel) {
        return MilvusVectorStore.builder(milvusClient, embeddingModel)
                .databaseName("您的向量数据库名称")
                .collectionName("该数据库下的Collection名称")   //  alibaba
                .embeddingDimension(1536) //1536
                .indexType(IndexType.IVF_FLAT)
                .metricType(MetricType.COSINE)
//                .batchingStrategy(new TokenCountBatchingStrategy())
                .initializeSchema(true)
                .build();
    }


}
