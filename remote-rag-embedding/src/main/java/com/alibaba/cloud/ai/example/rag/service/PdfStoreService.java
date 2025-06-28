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


package com.alibaba.cloud.ai.example.rag.service;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.reader.ExtractedTextFormatter;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.reader.pdf.ParagraphPdfDocumentReader;
import org.springframework.ai.reader.pdf.config.PdfDocumentReaderConfig;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.milvus.MilvusVectorStore;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;


@Service
@RequiredArgsConstructor
public class PdfStoreService {

    private final DefaultResourceLoader resourceLoader;
    private final MilvusVectorStore vectorStore;
    private final TokenTextSplitter tokenTextSplitter;

    /**
     * 根据PDF的页数进行分割
     * @param url
     */
    public void saveSourceByPage(String url){
        // 加载资源，需要本地路径的信息
        Resource resource = resourceLoader.getResource(url);
        // 加载PDF文件时的配置对象
        PdfDocumentReaderConfig loadConfig = PdfDocumentReaderConfig.builder()
                .withPageExtractedTextFormatter(
                        new ExtractedTextFormatter
                                .Builder()
                                .withNumberOfBottomTextLinesToDelete(3)
                                .withNumberOfTopPagesToSkipBeforeDelete(1)
                                .build()
                )
                .withPagesPerDocument(1)
                .build();

        PagePdfDocumentReader pagePdfDocumentReader = new PagePdfDocumentReader(resource, loadConfig);
        // 存储到向量数据库中
        vectorStore.accept(tokenTextSplitter.apply(pagePdfDocumentReader.get()));
    }

    /**
     * 根据PDF的目录（段落）进行划分
     * @param url
     */
    public void saveSourceByParagraph(String url){
        Resource resource = resourceLoader.getResource(url);

        PdfDocumentReaderConfig loadConfig = PdfDocumentReaderConfig.builder()
                .withPageExtractedTextFormatter(
                        new ExtractedTextFormatter
                                .Builder()
                                .withNumberOfBottomTextLinesToDelete(3)
                                .withNumberOfTopPagesToSkipBeforeDelete(1)
                                .build()
                )
                .withPagesPerDocument(1)
                .build();

        ParagraphPdfDocumentReader pdfReader = new ParagraphPdfDocumentReader(
                resource,
                loadConfig
        );
        vectorStore.accept(tokenTextSplitter.apply(pdfReader.get()));
    }

    /**
     * MultipartFile对象存储，采用PagePdfDocumentReader
     * https://docs.spring.io/spring-ai/docs/current/api/org/springframework/ai/reader/pdf/config/PdfDocumentReaderConfig.Builder.html#withPagesPerDocument(int)
     * @param file
     */
    public void saveSource(MultipartFile file){
        try {
            // 获取文件名
            String fileName = file.getOriginalFilename();

            System.out.println("finename==" +fileName);

            // 获取文件内容类型
            String contentType = file.getContentType();
            // 获取文件字节数组
            byte[] bytes = file.getBytes();
            // 创建一个临时文件
            Path tempFile = Files.createTempFile("temp-", fileName);
            // 将文件字节数组保存到临时文件
            Files.write(tempFile, bytes);
            // 创建一个 FileSystemResource 对象
            Resource fileResource = new FileSystemResource(tempFile.toFile());
            PdfDocumentReaderConfig loadConfig = PdfDocumentReaderConfig.builder()
                    .withPageExtractedTextFormatter(
                            new ExtractedTextFormatter
                                    .Builder()
                                    .withNumberOfBottomTextLinesToDelete(3)
                                    .withNumberOfTopPagesToSkipBeforeDelete(1)
                                    .build()
                    )

                    .withPagesPerDocument(1)//How many pages to put in a single Document instance. 0 stands for all pages. Defaults to 1.
                    .build();
            PagePdfDocumentReader pagePdfDocumentReader = new PagePdfDocumentReader(fileResource, loadConfig);
            vectorStore.accept(tokenTextSplitter.apply(pagePdfDocumentReader.get()));

            System.out.println("success");

        }catch (IOException e){
            e.printStackTrace();
        }

    }
}