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

import com.alibaba.cloud.ai.example.rag.service.PdfStoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

@Controller
@RequiredArgsConstructor
public class MainController {

    @Autowired
    private final PdfStoreService pdfStoreService;

    @GetMapping("/")
    public String index() {
        return "index.html";
    }


    @PostMapping("/uploadimg")
    public String upload(@RequestParam("file") MultipartFile file) throws Exception {

        System.out.println("/uploadimg,,,,,,");

        // 设置上传至项目文件夹下的uploadFile文件夹中，没有文件夹则创建
        File dir = new File("uploadFile");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        pdfStoreService.saveSource(file);
        System.out.println("上传完成！,,,,,,");
        return "上传完成！文件名：" + file.getName();
    }


}