package cn.e3mall.controller;

import cn.e3mall.common.utils.FastDFSClient;
import cn.e3mall.common.utils.JsonUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

/**
 * @author quan
 * @create 2020-05-06 10:12
 */
@RestController
public class PictureController {

    @Value("${IMAGE_SERVER_URL}")
    private String urlPath;

            @RequestMapping(value = "/pic/upload", produces = MediaType.TEXT_PLAIN_VALUE + ";charset=utf-8")
            public String uploadFile(MultipartFile uploadFile) {
                Map map = new HashMap();
                try {
                    FastDFSClient fastDFSClient = new FastDFSClient("classpath:conf\\fastdfs_client.conf");

                    String originalFilename = uploadFile.getOriginalFilename();
                    String extName = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);

                    String path = fastDFSClient.uploadFile(uploadFile.getBytes(), extName);

                    String url = urlPath + path;

                    map.put("error", 0);
                    map.put("url", url);
        } catch (Exception e) {
            e.printStackTrace();
            map.put("error", 1);
            map.put("message", "图片上传失败");
        }
        return JsonUtils.objectToJson(map);
    }
}
