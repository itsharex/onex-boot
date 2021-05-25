package com.nb6868.onexboot.api.modules.sys.oss;

import com.nb6868.onexboot.common.pojo.Kv;
import com.nb6868.onexboot.common.util.DateUtils;
import com.nb6868.onexboot.common.util.IdUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.multipart.MultipartFile;

/**
 * 云存储(阿里云、本地)
 *
 * @author Charles zhangchaoxu@gmail.com
 */
public abstract class AbstractOssService {

    /**
     * 云存储配置信息
     */
    OssProp config;

    /**
     * 文件路径
     *
     * @param pathPrefix 路径前缀
     * @param fileName 文件扩展名
     * @param keepFileName 是否保留原文件名
     * @param appendTimestamp 文件名追加时间戳
     * @return 返回上传路径
     */
    public String buildUploadPath(String pathPrefix, String fileName, boolean keepFileName, boolean appendTimestamp) {
        // 路径：文件路径,按日分割
        String path = DateUtils.format(DateUtils.now(), "yyyyMMdd") + "/";
        if (StringUtils.isNotBlank(pathPrefix)) {
            path = pathPrefix + "/" + path;
        }
        // 文件
        String newFileName;
        if (keepFileName) {
            // 保留原文件名
            if (appendTimestamp) {
                String fileExtensionName = FilenameUtils.getExtension(fileName);
                if (StringUtils.isNotBlank(fileExtensionName)) {
                    newFileName = FilenameUtils.removeExtension(fileName) + "-" + DateUtils.format(DateUtils.now(), "HHmmssSSS") + "." + fileExtensionName;
                } else {
                    newFileName = FilenameUtils.getName(fileName) + "-" + DateUtils.format(DateUtils.now(), "HHmmssSSS");
                }
            } else {
                newFileName = fileName;
            }
        } else {
            // 生成uuid
            String uuid = IdUtils.simpleUUID();
            String fileExtensionName = FilenameUtils.getExtension(fileName);
            if (StringUtils.isNotBlank(fileExtensionName)) {
                if (appendTimestamp) {
                    newFileName = uuid + "-" + DateUtils.format(DateUtils.now(), "HHmmssSSS") + "." + fileExtensionName;
                } else {
                    newFileName = uuid + "." + fileExtensionName;
                }
            } else {
                newFileName = uuid;
            }
        }
        return path + newFileName;
    }

    /**
     * 文件上传
     *
     * @param file 文件
     * @return 返回http地址
     */
    public abstract String upload(MultipartFile file);

    /**
     * 生成访问时间
     */
    public abstract String generatePresignedUrl(String objectName, long expiration);

    /**
     * 获得sts
     */
    public abstract Kv getSts();

}
