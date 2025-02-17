package com.nb6868.onex.common.oss;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.io.file.FileNameUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.RadixUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.nb6868.onex.common.pojo.ApiResult;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

/**
 * 存储服务
 *
 * @author Charles zhangchaoxu@gmail.com
 */
public abstract class AbstractOssService {

    public OssPropsConfig config;

    /**
     * 通过自定义前缀和文件名构建object key
     *
     * @param customPrefix 自定义前缀，可以是a/b
     * @param fileName 文件名
     * @return 构建后的objectKey
     */
    public String buildObjectKey(String customPrefix, String fileName) {
        // 构建前缀,前后没有斜杠
        String path = StrUtil.emptyIfNull(config.getPrefix());
        if (StrUtil.isNotBlank(path)) {
            path += StrUtil.SLASH;
        }
        if (StrUtil.isNotBlank(customPrefix)) {
            path += customPrefix + StrUtil.SLASH;
        }
        // 构建路径
        String policyPath = buildPolicyPath(config.getPathPolicy());
        if (StrUtil.isNotBlank(policyPath)) {
            path += policyPath;
        }
        if (!StrUtil.endWith(path, StrUtil.SLASH)) {
            path += StrUtil.SLASH;
        }
        // 构建文件名
        String newFileName = buildFileName(path, fileName, config.getPathPolicy());
        return path + newFileName;
    }

    /**
     * 构建策略path
     */
    public String buildPolicyPath(String pathPolicy) {
        if ("uuid".equalsIgnoreCase(pathPolicy)) {
            return IdUtil.fastSimpleUUID();
        } else if ("day".equalsIgnoreCase(pathPolicy)) {
            return  DateUtil.format(DateUtil.date(), DatePattern.PURE_DATE_PATTERN);
        } else if ("month".equalsIgnoreCase(pathPolicy)) {
            return DateUtil.format(DateUtil.date(), DatePattern.SIMPLE_MONTH_PATTERN);
        } else if ("year".equalsIgnoreCase(pathPolicy)) {
            return DateUtil.format(DateUtil.date(), DatePattern.NORM_YEAR_PATTERN);
        } else if ("dayReverse".equalsIgnoreCase(pathPolicy)) {
            return StrUtil.reverse(DateUtil.format(DateUtil.date(), DatePattern.PURE_DATE_PATTERN));
        } else if ("monthReverse".equalsIgnoreCase(pathPolicy)) {
            return StrUtil.reverse(DateUtil.format(DateUtil.date(), DatePattern.SIMPLE_MONTH_PATTERN));
        } else if ("yearReverse".equalsIgnoreCase(pathPolicy)) {
            return StrUtil.reverse(DateUtil.format(DateUtil.date(), DatePattern.NORM_YEAR_PATTERN));
        } else if ("radixs34".equalsIgnoreCase(pathPolicy)) {
            return RadixUtil.encode(RadixUtil.RADIXS_34, DateUtil.current());
        } else if ("radixsShuffle34".equalsIgnoreCase(pathPolicy)) {
            return RadixUtil.encode(RadixUtil.RADIXS_SHUFFLE_34, DateUtil.current());
        } else if ("radixs59".equalsIgnoreCase(pathPolicy)) {
            return RadixUtil.encode(RadixUtil.RADIXS_59, DateUtil.current());
        } else if ("radixsShuffle59".equalsIgnoreCase(pathPolicy)) {
            return RadixUtil.encode(RadixUtil.RADIXS_SHUFFLE_59, DateUtil.current());
        }
        return "";
    }

    /**
     * 创建文件名
     */
    public String buildFileName(String path, String fileName, String pathPolicy) {
        // 文件
        String newFileName;
        if (config.getKeepFileName()) {
            String fileExtName = FileNameUtil.extName(fileName);
            String fileMainName = FileNameUtil.mainName(fileName);
            // 去除urlencode不支持字符,去除容易出问题的逗号
            String fileMainNameNoSpecChar = StrUtil.removeAll(fileMainName, ' ', '+', '=', '&', '#', '/', '?', '%', '*', ',', '，');
            // 新的文件名
            newFileName = fileMainNameNoSpecChar + (StrUtil.isNotBlank(fileExtName) ? ("." + fileExtName) : "");
            // 若路径策略是radixs进制转换，则无需判断重复，通过radixs自身已经做了重复处理，认为毫秒时间内不会有重名文件请求
            if (!StrUtil.startWith(pathPolicy, "radixs") && isObjectKeyExisted(config.getBucketName(), path + newFileName).getData()) {
                // 若objectKey已存在,补一个后缀,默认补上后缀后不会再重复
                newFileName = fileMainNameNoSpecChar + "-" + DateUtil.format(DateUtil.date(), DatePattern.PURE_DATETIME_MS_PATTERN) + (StrUtil.isNotBlank(fileExtName) ? ("." + fileExtName) : "");
            }
        } else {
            // 文件扩展名
            String fileExtName = FileNameUtil.extName(fileName);
            // 生成{uuid}.{extName}
            newFileName = IdUtil.simpleUUID() + (StrUtil.isNotBlank(fileExtName) ? ("." + fileExtName) : "");
        }
        return newFileName;
    }

    /**
     * 文件上传
     *
     * @param objectKey      路径前缀+文件名
     * @param inputStream    文件流
     * @param objectMetadata 自定义的objectMetadata
     * @return 返回objectKey
     */
    public abstract ApiResult<JSONObject> upload(String objectKey, InputStream inputStream, Map<String, Object> objectMetadata);

    public ApiResult<JSONObject> upload(String objectKey, InputStream inputStream) {
        return this.upload(objectKey, inputStream, null);
    }

    public ApiResult<JSONObject> upload(String objectKey, MultipartFile file) {
        InputStream inputStream;
        try {
            inputStream = file.getInputStream();
        } catch (IOException e) {
            return new ApiResult<JSONObject>().error(ApiResult.ERROR_CODE_EXCEPTION, "文件处理异常:" + e.getMessage());
        }
        return upload(objectKey, inputStream, null);
    }

    /**
     * 文件上传
     *
     * @param objectKey 文件路径前缀
     * @param file      文件
     * @return 返回http地址
     */
    public ApiResult<JSONObject> upload(String objectKey, File file) {
        BufferedInputStream inputStream = FileUtil.getInputStream(file);
        return upload(objectKey, inputStream);
    }

    /**
     * base64 上传文件
     *
     * @param objectKey 前缀
     * @param base64    文件base64
     * @return 上传结果
     */
    public ApiResult<JSONObject> uploadBase64(String objectKey, String base64) {
        InputStream inputStream;
        try {
            if (base64.split(",").length > 1) {
                base64 = base64.split(",")[1];
            }
            inputStream = IoUtil.toStream(Base64.decode(base64));
        } catch (Exception e) {
            return new ApiResult<JSONObject>().error(ApiResult.ERROR_CODE_EXCEPTION, "文件base64处理异常:" + e.getMessage());
        }
        return upload(objectKey, inputStream);
    }

    /**
     * 文件下载
     *
     * @param objectKey 文件名
     */
    public abstract ApiResult<InputStream> download(String objectKey);

    /**
     * 生成预签名链接
     *
     * @param expire 过期时间(单位秒)
     */
    public abstract ApiResult<String> getPreSignedUrl(String objectKey, String method, int expire);

    /**
     * 生成已签名的表单
     */
    public ApiResult<JSONObject> getSignedPostForm(JSONArray conditions, int expire, String objectKey) {
        return new ApiResult<JSONObject>().error("暂未实现该方法");
    }

    /**
     * object key是否存在
     * 默认存在
     */
    public abstract ApiResult<Boolean> isObjectKeyExisted(String bucketName, String objectKey);

}
