package org.ezplatform.component.attachment.web.rest;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.ezplatform.component.attachment.entity.Attachment;
import org.ezplatform.component.attachment.entity.AttachmentDownload;
import org.ezplatform.component.attachment.service.AttachmentService;
import org.ezplatform.component.attachment.util.AttachUtils;
import org.ezplatform.core.annotation.ApiLog;
import org.ezplatform.core.annotation.MetaData;
import org.ezplatform.core.common.WebUser;
import org.ezplatform.core.service.BaseService;
import org.ezplatform.core.web.controller.BaseController;
import org.ezplatform.core.web.json.JsonViews;
import org.ezplatform.core.web.view.OperationResult;
import org.ezplatform.util.DateUtils;
import org.ezplatform.util.GlobalConstant;
import org.ezplatform.util.IPAddrFetcher;
import org.ezplatform.util.ImageUtils;
import org.ezplatform.util.MD5Utils;
import org.ezplatform.util.UuidUtils;
import org.ezplatform.util.security.FileSecUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import com.fasterxml.jackson.annotation.JsonView;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import springfox.documentation.annotations.ApiIgnore;

/**
 * Restful附件上传（包括图片等）处理控制器
 */
@MetaData("附件上传")
@Controller
@RequestMapping(value = "/api/cmp/attachment")
@ApiLog
@Api(value = "附件上传下载接口", description = "附件API", tags = "AttachmentApi")
public class AttachmentApiController extends BaseController<Attachment, String> {

    private static final Logger LOGGER = LoggerFactory.getLogger(AttachmentApiController.class);

    @Autowired
    private AttachmentService attachmentService;

    @Override
    protected BaseService<Attachment, String> getEntityService() {
        return attachmentService;
    }

    @RequestMapping(value = "/uploadfile", method = RequestMethod.POST)
    @ResponseBody
    @JsonView(JsonViews.ApiView.class)
    public OperationResult uploadfile(@RequestParam("file") CommonsMultipartFile file, @RequestParam("dir") String dir,
                                  HttpServletRequest request) {
        return upload(file, dir, request);
    }

    /**
     * 处理上传附件，并存储相关信息到数据库
     * 
     * @param request
     * @return
     */
    @ApiOperation(value = "上传附件", notes = "将文件上传到指定目录")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "file", value = "文件内容", required = true, paramType = "form", dataType = "CommonsMultipartFile"),
            @ApiImplicitParam(name = "dir", value = "附件目录", required = true, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "fileDisplayName", value = "文件显示名称", required = false, paramType = "query", dataType = "String") })
    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    @ResponseBody
    @JsonView(JsonViews.ApiView.class)
    public OperationResult upload(@RequestParam("file") CommonsMultipartFile file, @RequestParam("dir") String dir,
            HttpServletRequest request) {
        List<Attachment> result = null;
        String errorMessage = "上传失败";
        WebUser webUser = org.ezplatform.core.web.util.WebUtils.getCurrentUser();

        //附件密级（绝密1、秘密2、一般3、非密4）
        int secretLevel = 4;
        boolean isUseSecretLevel = GlobalConstant.isUseSecretLevel();
        if(isUseSecretLevel) {
            String fileSecretLevel = request.getParameter("secretLevel");
            try{
                secretLevel = Integer.parseInt(fileSecretLevel);
            } catch (Exception e) {
                secretLevel = 4;
            }
        }

        try {
            
            //支持自定义文件显示名称
            String fileDisplayName = request.getParameter("fileDisplayName");

            String absolutePath = AttachUtils.getUploadAbsolutePath(request);

            File tempFile = new File(absolutePath + dir + "/");
            // 判断目标文件夹是否存在， 不存在则提示并返回。
            if (!tempFile.exists()) {
                //return OperationResult.buildFailureResult("目标文件夹【" + dir + "】不存在！");
                LOGGER.debug("目标文件夹【"+dir+"】不存在！开始自动创建...");
                tempFile.mkdirs();
            }

            // 系统日期
            String yearMonth = DateUtils.formatDate(new Date(), "yyyyMM");

            String relativePath = dir + "/" + yearMonth;

            absolutePath += relativePath + "/";

            LOGGER.debug("absolutePath: " + absolutePath);

            tempFile = new File(absolutePath);
            // 判断目标文件夹中的年月文件夹是否存在， 不存在则创建。
            if (!tempFile.exists()) {
                tempFile.mkdirs();
            }

            MultipartFile fileDetail = file;
            if (fileDetail != null) {
                result = new ArrayList<Attachment>();
                String originalFilename = fileDetail.getOriginalFilename();
                // 扩展名
                String fileExt = "";
                if (originalFilename.lastIndexOf(".") > -1) {
                    fileExt = originalFilename.substring(originalFilename.lastIndexOf("."));
                }

                // ------------------------------------------
                // 扩展名校验，考虑读取配置
//                if (!StringUtils.containsIgnoreCase("," + AttachUtils.getAllowExts() + ",", "," + fileExt + ",")) {
//                    errorMessage = "不合法文件，禁止上传！";
//                    return OperationResult.buildFailureResult(errorMessage);
//                }
//
//                if (StringUtils.containsIgnoreCase("," + AttachUtils.getNotAllowExts() + ",", "," + fileExt + ",")) {
//                    errorMessage = "不合法文件，禁止上传！";
//                    return OperationResult.buildFailureResult(errorMessage);
//                }
                
                if(AttachUtils.checkFileExt(fileExt) == false) {
                    errorMessage = "不合法文件，禁止上传！";
                    return OperationResult.buildFailureResult(errorMessage);
                }

                // 文件内容验证
                // do nothing
                // ------------------------------------------

                String fileName = UuidUtils.UUID() + fileExt;

                Attachment attach = new Attachment();

                String path = absolutePath + fileName;
                File localFile = new File(path);
                
                String attachEnc = attachmentService.getSysAttachmentEncrypt("0");
                //系统设置为加密  文件在加密文件范围内  有配置加密算法  满足这三个条件时  对上传文件进行加密
                if(StringUtils.isNotBlank(attachEnc)&&"1".equals(attachEnc)&&AttachUtils.isInEncrypteExts(fileName)&& GlobalConstant.isFileEncAlgorithm()) {
                    try {
                        // 将上传文件加密写入到指定文件出
                        FileSecUtils.encFile(fileDetail.getBytes(), localFile, GlobalConstant.getFileEncAlgorithm());
                        attach.setEncAlgorithm(GlobalConstant.getFileEncAlgorithm());
                    }catch(Exception e) {
                        // 将上传文件写入到指定文件出
                        fileDetail.transferTo(localFile);
                    }
                } else {
                    // 将上传文件写入到指定文件出
                    fileDetail.transferTo(localFile);
                }
                
                attach.setFileName(fileName);
                attach.setContentType(fileDetail.getContentType());
                if(StringUtils.isNotBlank(fileDisplayName)) {
                    originalFilename = fileDisplayName;
                }
                attach.setFileDisplayName(originalFilename);
                attach.setFileExt(fileExt);
                attach.setFileSize(fileDetail.getSize());
                attach.setRelativePath(relativePath);
                attach.setClientIp(IPAddrFetcher.getRemoteIpAddress(request));
                attach.setFileSource(Attachment.FileSource.PC);

                attach.setDelFlag("0");

                String checksum = MD5Utils.hash(fileName);
                attach.setChecksum(checksum);
                attach.setCleanFlag(0);// 0-正常；1-可清理

                //增加附件密级（绝密1、秘密2、一般3、非密4）
                if(isUseSecretLevel) {
                    attach.setSecretLevel(secretLevel);
                }
                //attach.setPicDisplayHeight(picDisplayHeight);
                //移动端上传时，默认增加宽度和高度
                attach.setPicDisplayHeight(100);
                attach.setPicDisplayWidth(100);
                // 保存
                Attachment newAttach = attachmentService.save(attach);

                String fid = newAttach.getId();

                attach.setDownloadUrl(AttachUtils.getDownloadUrl(fid, checksum));
                attach.setDeleteUrl(AttachUtils.getDeleteUrl(fid, MD5Utils.hash(checksum)));
                // 设置图片媒体地址
                if (ImageUtils.isImage(fileExt) || ImageUtils.isMedia(fileExt)) {
                    attach.setImageUrl(AttachUtils.getImageUrl(fid, checksum));
                    attach.setImediaUrl(AttachUtils.getImageMediaUrl(relativePath, fileName));
                }
                // 获取较小较大图片地址
                if (ImageUtils.isImage(fileExt)) {
                    attach.setImageSmallerUrl(AttachUtils.getImageSmallerUrl(relativePath, fileName));
                    attach.setImageMiddleUrl(AttachUtils.getImageMiddleUrl(relativePath, fileName));
                }

                result.add(attach);
            }
        } catch (IOException e) {
            e.printStackTrace();
            LOGGER.error("上传文件异常", e);
        }

        if (result.isEmpty() == false) {
            return OperationResult.buildSuccessResult("上传成功", result);
        }

        return OperationResult.buildFailureResult(errorMessage);
    }

    /**
     * 下载附件
     * 
     * @param fid
     *            文件ID
     * @param chksum
     *            校验码
     * @param request
     * @param response
     */
    @ApiOperation(value = "下载附件", notes = "根据文件ID和校验码下载附件")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "fid", value = "文件ID", required = true, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "chksum", value = "校验码", required = false, paramType = "query", dataType = "String") })
    @RequestMapping(value = "/download", method = RequestMethod.GET)
    public void download(@RequestParam("fid") String fid, @RequestParam("chksum") String chksum,
            HttpServletRequest request, HttpServletResponse response) {

    	 String contentType = request.getParameter("contentType") ;
        if (StringUtils.isBlank(fid) || StringUtils.isBlank(chksum)) {
            return;
        }

        Attachment attachment = attachmentService.getAttachmentById(fid);
        if (attachment == null) {
            return;
        }

        String fileName = attachment.getFileName();
        String checksum = MD5Utils.hash(fileName);
        if (checksum.equalsIgnoreCase(chksum)) {
            String relativePath = attachment.getRelativePath();
            String downloadPath = AttachUtils.getUploadAbsolutePath(request) + relativePath + "/" + fileName;

            LOGGER.debug("downloadPath: " + downloadPath);

            if (downloadPath.toUpperCase().contains(AttachUtils.DENY_DIR)) {
                return;
            }

            File file = new File(downloadPath);
            if (file.exists()) {
                LOGGER.debug("downloading...");

                // 保存下载记录
                AttachmentDownload ad = new AttachmentDownload();
                ad.setFileName(fileName);
                ad.setFileDisplayName(attachment.getFileDisplayName());
                ad.setClientIp(IPAddrFetcher.getRemoteIpAddress(request));
                attachmentService.saveAttachmentDownload(ad);

                InputStream in = null;
                OutputStream out = null;
                try {
                    String downloadFileName = AttachUtils.encodingFileName(attachment.getFileDisplayName(), request);

                    response.reset();
                    response.setHeader("Content-Disposition", "attachment; filename=\"" + downloadFileName + "\"");
                    response.setHeader("Connection", "Keep-Alive");
                    response.setContentType("application/octet-stream; charset=utf-8");
                    if(StringUtils.isNotBlank(contentType)&&"pdf".equals(contentType)){//如果指定了内容类型并且值为pdf  则直接进行预览
//                    	headerDispositionDefault = "inline";
//                    	contentTypeDefault = "application/pdf;charset=utf-8";
                    	
                    	  response.setHeader("Content-Disposition", "inline"+"; filename=\"" + downloadFileName+"\"");
                          response.setHeader("Connection", "Keep-Alive");
                          response.setContentType( "application/pdf;charset=utf-8");
                    }
//                    response.setContentLength(Integer.parseInt(file.length()+""));//.setContentLengthLong(file.length());//attachment.getFileSize());
                    out = response.getOutputStream();
                    in = FileUtils.openInputStream(file);
                    byte[] buffer = new byte[4096];
                    int len = -1;

                    if(AttachUtils.isInEncrypteExts(fileName) && StringUtils.isNotBlank(attachment.getEncAlgorithm()) && !"none".equalsIgnoreCase(attachment.getEncAlgorithm())) {
                        try {
                            out.write(FileSecUtils.decFile(file, attachment.getEncAlgorithm()));
                        }catch(Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        while ((len = in.read(buffer)) != -1) {  
                            out.write(buffer, 0, len);  
                        }
                    }
                    
                    out.flush();
                    return;
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (out != null) {
                        try {
                            out.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if (in != null) {
                        try {
                            in.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    /**
     * 删除附件
     * 
     * @param fid
     *            文件ID
     * @param chksum
     * @param request
     * @return
     */
    @ApiOperation(value = "删除附件", notes = "根据文件ID和校验码删除附件")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "fid", value = "文件ID", required = true, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "chksum", value = "校验码", required = true, paramType = "query", dataType = "String") })
    @RequestMapping(value = "/delete", method = RequestMethod.DELETE)
    @ResponseBody
    public OperationResult delete(@RequestParam("fid") String fid, @RequestParam("chksum") String chksum,
            HttpServletRequest request) {

        if (StringUtils.isBlank(fid) || StringUtils.isBlank(chksum)) {
            return OperationResult.buildFailureResult("删除失败");
        }

        Attachment attachment = attachmentService.getAttachmentById(fid);
        if (attachment == null) {
            return OperationResult.buildFailureResult("删除失败");
        }

        String fileName = attachment.getFileName();
        String checksum = MD5Utils.hash(MD5Utils.hash(fileName));
        // 是否修改状态，此状态下不物理删除附件，避免表单未保存，附件丢失现象！！！
        boolean isModify = MD5Utils.hash(fileName).equalsIgnoreCase(chksum);
        if (checksum.equalsIgnoreCase(chksum) || isModify) {
            String relativePath = attachment.getRelativePath();
            String filePath = AttachUtils.getUploadAbsolutePath(request) + "/" + relativePath + "/" + fileName;

            LOGGER.debug("filePath: " + filePath);

            File file = new File(filePath);
            if (file.exists()) {
                boolean isDeleted = false;
                // if (isModify == false && relativePath.indexOf("sysconf") == -1/*系统设置除外*/)
                // {//新增状态，物理删除，避免产生垃圾！！！
                LOGGER.debug("now physical deleting...");
                isDeleted = file.delete();
                // }

                if (isDeleted) {// 接口调用实际彻底删除文件
                    attachmentService.deleteById(attachment);
                    AttachUtils.clearCache(attachment);
                    return OperationResult.buildSuccessResult("删除成功");
                } /*
                   * else if(isModify) { attachment.setDelFlag("1");//修改状态，避免附件丢失，逻辑删除！！！
                   * attachmentService.save(attachment); return
                   * OperationResult.buildSuccessResult("删除成功"); }
                   */
            }
        }

        return OperationResult.buildFailureResult("删除失败");
    }

    /**
     * 获取文件信息
     * 
     * @param fid
     * @param chksum
     * @param request
     * @return
     */
    @ApiOperation(value = "获取附件信息", notes = "根据文件ID和校验码获取附件信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "fid", value = "文件ID", required = true, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "chksum", value = "校验码", required = true, paramType = "query", dataType = "String") })
    @RequestMapping(value = "/getAttachmentInfo", method = RequestMethod.GET)
    @ResponseBody
    @JsonView(JsonViews.ApiView.class)
    public OperationResult getAttachmentInfo(@RequestParam("fid") String fid, @RequestParam("chksum") String chksum,
            HttpServletRequest request) {
        if (StringUtils.isBlank(fid) || StringUtils.isBlank(chksum)) {
            return OperationResult.buildFailureResult("获取附件信息失败");
        }

        Attachment attachment = new AttachUtils().getAttachmentById(fid);
        if (attachment == null) {
            return OperationResult.buildFailureResult("获取附件信息失败");
        }

        String fileName = attachment.getFileName();
        String checksum = MD5Utils.hash(fileName);
        if (checksum.equalsIgnoreCase(chksum)) {
            List<Attachment> result = new ArrayList<Attachment>();
            result.add(attachment);

            return OperationResult.buildSuccessResult("获取附件信息成功", result);
        }

        return OperationResult.buildFailureResult("获取附件信息失败");
    }

    /**
     * 免登陆认证下载
     * 
     * @param fid
     *            文件ID
     * @param chksum
     *            校验码
     * @param request
     * @param response
     */
    @ApiIgnore
    @RequestMapping(value = "/dl", method = RequestMethod.GET)
    public void dl(@RequestParam("fid") String fid, @RequestParam("chksum") String chksum, HttpServletRequest request,
            HttpServletResponse response) {
        String hash = request.getParameter("hash");
        if (StringUtils.isBlank(hash)) {
            return;
        }

        String newHash = MD5Utils.hash(fid + "|" + chksum);
        if (!hash.equalsIgnoreCase(newHash)) {
            return;
        }

        download(fid, chksum, request, response);
    }

    /**
     * 获取文件信息
     * 
     * @param fid
     * @param chksum
     * @param request
     * @return
     */
    @ApiOperation(value = "根据文件Id获取附件信息", notes = "根据文件Id获取附件信息，多个以|分隔")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "fids", value = "文件ID", required = true, paramType = "query", dataType = "String") })
    @RequestMapping(value = "/getAttachmentInfos", method = RequestMethod.GET)
    @ResponseBody
    @JsonView(JsonViews.ApiView.class)
    public OperationResult getAttachmentInfos(@RequestParam("fids") String fids, HttpServletRequest request) {
        if (StringUtils.isBlank(fids)) {
            return OperationResult.buildFailureResult("获取附件信息失败");
        }
        String[] attachId = fids.split("\\|");
        List<Attachment> result = new ArrayList<Attachment>();
        for (int i = 0; i < attachId.length; i++) {
            Attachment attach = new AttachUtils().getAttachmentById(attachId[i]);
            if (attach != null) {
//                String downloadUrl = attach.getDownloadUrl();
//                attach.setDownloadUrl(downloadUrl);
                result.add(attach);
            }
        }
        return OperationResult.buildSuccessResult("获取附件信息成功", result);
    }

    /**
     * 获取文件信息
     * 
     * @param attachId
     * @return
     */
    @ApiOperation(value = "根据文件Id获取文件信息", notes = "根据文件Id获取附件信息，多个以,分隔")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "attachId", value = "文件ID", required = true, paramType = "query", dataType = "String"), })
    @RequestMapping(value = "/getAttachmentById", method = RequestMethod.GET)
    @ResponseBody
    @JsonView(JsonViews.ApiView.class)
    public OperationResult getAttachmentById(@RequestParam("attachId") String attachId, HttpServletRequest request) {
        if (StringUtils.isBlank(attachId)) {
            return OperationResult.buildFailureResult("获取附件信息失败");
        }
        String[] ids = attachId.split(",");
        List<Attachment> result = new ArrayList<Attachment>();
        for (int i = 0; i < ids.length; i++) {
            Attachment attach = new AttachUtils().getAttachmentById(ids[i]);
            if (attach != null) {
                result.add(attach);
            }
        }
        return OperationResult.buildSuccessResult("获取附件信息成功", result);
    }

    /**
     * 获取文件信息
     * 
     * @param attachId
     * @return
     */
    @ApiOperation(value = "根据文件名称获取附件信息", notes = "根据文件名称获取附件信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "fileName", value = "文件名称", required = true, paramType = "query", dataType = "String"), })
    @RequestMapping(value = "/getAttachmentByFileName", method = RequestMethod.GET)
    @ResponseBody
    @JsonView(JsonViews.ApiView.class)
    public OperationResult getAttachmentByFileName(@RequestParam("fileName") String fileName,
            HttpServletRequest request) {
        if (StringUtils.isBlank(fileName)) {
            return OperationResult.buildFailureResult("获取附件信息失败");
        }
        Attachment attach = new AttachUtils().getAttachmentByFileName(fileName);
        return OperationResult.buildSuccessResult("获取附件信息成功", attach);
    }

    @ApiOperation(value = "创建附件信息", notes = "创建附件信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "fileName", value = "存储名称", required = true, dataType = "String"),
            @ApiImplicitParam(name = "fileSize", value = "大小", required = true, dataType = "String"),
            @ApiImplicitParam(name = "fileDisplayName", value = "显示名称", required = true, dataType = "String"),
            @ApiImplicitParam(name = "contentType", value = "文件类型", required = true, dataType = "String"),
            @ApiImplicitParam(name = "fileExt", value = "扩展名(包含.)", required = true, dataType = "String"),
            @ApiImplicitParam(name = "clientIp", value = "客户端IP", required = false, dataType = "String"),
            @ApiImplicitParam(name = "relativePath", value = "相对路径", required = true, dataType = "String")
            })
    @RequestMapping(value = "/createAttachmentInfo", method = RequestMethod.POST, produces = {
            "application/json; charset=UTF-8" })
    @ResponseBody
    public OperationResult createAttachmentInfo(HttpServletRequest request) {
        String dingSpaceId = request.getParameter("dingSpaceId");
        String dingFileId = request.getParameter("dingFileId");
        String fileName = request.getParameter("fileName");
        String fileSize = request.getParameter("fileSize");
        String fileDisplayName = request.getParameter("fileDisplayName");
        String contentType = request.getParameter("contentType");
        String fileExt = request.getParameter("fileExt");
        String clientIp = request.getParameter("clientIp");
        String relativePath = request.getParameter("relativePath");
        if(StringUtils.isBlank(fileDisplayName)){
        	fileDisplayName = fileName;
        }
        Attachment newAttach = new Attachment();
        String id = UuidUtils.UUID();
        newAttach.setId(id);
        newAttach.setFileName(fileName);
        newAttach.setContentType(contentType);
        newAttach.setFileDisplayName(fileDisplayName);
        newAttach.setFileExt(fileExt);
        newAttach.setFileSize(Long.valueOf(fileSize));
        newAttach.setRelativePath(relativePath);
        newAttach.setClientIp(clientIp);
        newAttach.setDelFlag("0");
        newAttach.setDingSpaceId(dingSpaceId);
        newAttach.setDingFileId(dingFileId);
        String checksum = MD5Utils.hash(fileName);
        newAttach.setChecksum(checksum);
        // 保存
        attachmentService.save(newAttach);
        return OperationResult.buildSuccessResult("创建附件成功", newAttach);
    }

}
