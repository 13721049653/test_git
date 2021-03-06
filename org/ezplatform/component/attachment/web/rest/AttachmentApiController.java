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
 * Restful????????????????????????????????????????????????
 */
@MetaData("????????????")
@Controller
@RequestMapping(value = "/api/cmp/attachment")
@ApiLog
@Api(value = "????????????????????????", description = "??????API", tags = "AttachmentApi")
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
     * ??????????????????????????????????????????????????????
     * 
     * @param request
     * @return
     */
    @ApiOperation(value = "????????????", notes = "??????????????????????????????")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "file", value = "????????????", required = true, paramType = "form", dataType = "CommonsMultipartFile"),
            @ApiImplicitParam(name = "dir", value = "????????????", required = true, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "fileDisplayName", value = "??????????????????", required = false, paramType = "query", dataType = "String") })
    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    @ResponseBody
    @JsonView(JsonViews.ApiView.class)
    public OperationResult upload(@RequestParam("file") CommonsMultipartFile file, @RequestParam("dir") String dir,
            HttpServletRequest request) {
        List<Attachment> result = null;
        String errorMessage = "????????????";
        WebUser webUser = org.ezplatform.core.web.util.WebUtils.getCurrentUser();

        //?????????????????????1?????????2?????????3?????????4???
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
            
            //?????????????????????????????????
            String fileDisplayName = request.getParameter("fileDisplayName");

            String absolutePath = AttachUtils.getUploadAbsolutePath(request);

            File tempFile = new File(absolutePath + dir + "/");
            // ???????????????????????????????????? ??????????????????????????????
            if (!tempFile.exists()) {
                //return OperationResult.buildFailureResult("??????????????????" + dir + "???????????????");
                LOGGER.debug("??????????????????"+dir+"?????????????????????????????????...");
                tempFile.mkdirs();
            }

            // ????????????
            String yearMonth = DateUtils.formatDate(new Date(), "yyyyMM");

            String relativePath = dir + "/" + yearMonth;

            absolutePath += relativePath + "/";

            LOGGER.debug("absolutePath: " + absolutePath);

            tempFile = new File(absolutePath);
            // ????????????????????????????????????????????????????????? ?????????????????????
            if (!tempFile.exists()) {
                tempFile.mkdirs();
            }

            MultipartFile fileDetail = file;
            if (fileDetail != null) {
                result = new ArrayList<Attachment>();
                String originalFilename = fileDetail.getOriginalFilename();
                // ?????????
                String fileExt = "";
                if (originalFilename.lastIndexOf(".") > -1) {
                    fileExt = originalFilename.substring(originalFilename.lastIndexOf("."));
                }

                // ------------------------------------------
                // ????????????????????????????????????
//                if (!StringUtils.containsIgnoreCase("," + AttachUtils.getAllowExts() + ",", "," + fileExt + ",")) {
//                    errorMessage = "?????????????????????????????????";
//                    return OperationResult.buildFailureResult(errorMessage);
//                }
//
//                if (StringUtils.containsIgnoreCase("," + AttachUtils.getNotAllowExts() + ",", "," + fileExt + ",")) {
//                    errorMessage = "?????????????????????????????????";
//                    return OperationResult.buildFailureResult(errorMessage);
//                }
                
                if(AttachUtils.checkFileExt(fileExt) == false) {
                    errorMessage = "?????????????????????????????????";
                    return OperationResult.buildFailureResult(errorMessage);
                }

                // ??????????????????
                // do nothing
                // ------------------------------------------

                String fileName = UuidUtils.UUID() + fileExt;

                Attachment attach = new Attachment();

                String path = absolutePath + fileName;
                File localFile = new File(path);
                
                String attachEnc = attachmentService.getSysAttachmentEncrypt("0");
                //?????????????????????  ??????????????????????????????  ?????????????????????  ????????????????????????  ???????????????????????????
                if(StringUtils.isNotBlank(attachEnc)&&"1".equals(attachEnc)&&AttachUtils.isInEncrypteExts(fileName)&& GlobalConstant.isFileEncAlgorithm()) {
                    try {
                        // ?????????????????????????????????????????????
                        FileSecUtils.encFile(fileDetail.getBytes(), localFile, GlobalConstant.getFileEncAlgorithm());
                        attach.setEncAlgorithm(GlobalConstant.getFileEncAlgorithm());
                    }catch(Exception e) {
                        // ???????????????????????????????????????
                        fileDetail.transferTo(localFile);
                    }
                } else {
                    // ???????????????????????????????????????
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
                attach.setCleanFlag(0);// 0-?????????1-?????????

                //???????????????????????????1?????????2?????????3?????????4???
                if(isUseSecretLevel) {
                    attach.setSecretLevel(secretLevel);
                }
                //attach.setPicDisplayHeight(picDisplayHeight);
                //????????????????????????????????????????????????
                attach.setPicDisplayHeight(100);
                attach.setPicDisplayWidth(100);
                // ??????
                Attachment newAttach = attachmentService.save(attach);

                String fid = newAttach.getId();

                attach.setDownloadUrl(AttachUtils.getDownloadUrl(fid, checksum));
                attach.setDeleteUrl(AttachUtils.getDeleteUrl(fid, MD5Utils.hash(checksum)));
                // ????????????????????????
                if (ImageUtils.isImage(fileExt) || ImageUtils.isMedia(fileExt)) {
                    attach.setImageUrl(AttachUtils.getImageUrl(fid, checksum));
                    attach.setImediaUrl(AttachUtils.getImageMediaUrl(relativePath, fileName));
                }
                // ??????????????????????????????
                if (ImageUtils.isImage(fileExt)) {
                    attach.setImageSmallerUrl(AttachUtils.getImageSmallerUrl(relativePath, fileName));
                    attach.setImageMiddleUrl(AttachUtils.getImageMiddleUrl(relativePath, fileName));
                }

                result.add(attach);
            }
        } catch (IOException e) {
            e.printStackTrace();
            LOGGER.error("??????????????????", e);
        }

        if (result.isEmpty() == false) {
            return OperationResult.buildSuccessResult("????????????", result);
        }

        return OperationResult.buildFailureResult(errorMessage);
    }

    /**
     * ????????????
     * 
     * @param fid
     *            ??????ID
     * @param chksum
     *            ?????????
     * @param request
     * @param response
     */
    @ApiOperation(value = "????????????", notes = "????????????ID????????????????????????")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "fid", value = "??????ID", required = true, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "chksum", value = "?????????", required = false, paramType = "query", dataType = "String") })
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

                // ??????????????????
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
                    if(StringUtils.isNotBlank(contentType)&&"pdf".equals(contentType)){//???????????????????????????????????????pdf  ?????????????????????
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
     * ????????????
     * 
     * @param fid
     *            ??????ID
     * @param chksum
     * @param request
     * @return
     */
    @ApiOperation(value = "????????????", notes = "????????????ID????????????????????????")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "fid", value = "??????ID", required = true, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "chksum", value = "?????????", required = true, paramType = "query", dataType = "String") })
    @RequestMapping(value = "/delete", method = RequestMethod.DELETE)
    @ResponseBody
    public OperationResult delete(@RequestParam("fid") String fid, @RequestParam("chksum") String chksum,
            HttpServletRequest request) {

        if (StringUtils.isBlank(fid) || StringUtils.isBlank(chksum)) {
            return OperationResult.buildFailureResult("????????????");
        }

        Attachment attachment = attachmentService.getAttachmentById(fid);
        if (attachment == null) {
            return OperationResult.buildFailureResult("????????????");
        }

        String fileName = attachment.getFileName();
        String checksum = MD5Utils.hash(MD5Utils.hash(fileName));
        // ????????????????????????????????????????????????????????????????????????????????????????????????????????????
        boolean isModify = MD5Utils.hash(fileName).equalsIgnoreCase(chksum);
        if (checksum.equalsIgnoreCase(chksum) || isModify) {
            String relativePath = attachment.getRelativePath();
            String filePath = AttachUtils.getUploadAbsolutePath(request) + "/" + relativePath + "/" + fileName;

            LOGGER.debug("filePath: " + filePath);

            File file = new File(filePath);
            if (file.exists()) {
                boolean isDeleted = false;
                // if (isModify == false && relativePath.indexOf("sysconf") == -1/*??????????????????*/)
                // {//?????????????????????????????????????????????????????????
                LOGGER.debug("now physical deleting...");
                isDeleted = file.delete();
                // }

                if (isDeleted) {// ????????????????????????????????????
                    attachmentService.deleteById(attachment);
                    AttachUtils.clearCache(attachment);
                    return OperationResult.buildSuccessResult("????????????");
                } /*
                   * else if(isModify) { attachment.setDelFlag("1");//?????????????????????????????????????????????????????????
                   * attachmentService.save(attachment); return
                   * OperationResult.buildSuccessResult("????????????"); }
                   */
            }
        }

        return OperationResult.buildFailureResult("????????????");
    }

    /**
     * ??????????????????
     * 
     * @param fid
     * @param chksum
     * @param request
     * @return
     */
    @ApiOperation(value = "??????????????????", notes = "????????????ID??????????????????????????????")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "fid", value = "??????ID", required = true, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "chksum", value = "?????????", required = true, paramType = "query", dataType = "String") })
    @RequestMapping(value = "/getAttachmentInfo", method = RequestMethod.GET)
    @ResponseBody
    @JsonView(JsonViews.ApiView.class)
    public OperationResult getAttachmentInfo(@RequestParam("fid") String fid, @RequestParam("chksum") String chksum,
            HttpServletRequest request) {
        if (StringUtils.isBlank(fid) || StringUtils.isBlank(chksum)) {
            return OperationResult.buildFailureResult("????????????????????????");
        }

        Attachment attachment = new AttachUtils().getAttachmentById(fid);
        if (attachment == null) {
            return OperationResult.buildFailureResult("????????????????????????");
        }

        String fileName = attachment.getFileName();
        String checksum = MD5Utils.hash(fileName);
        if (checksum.equalsIgnoreCase(chksum)) {
            List<Attachment> result = new ArrayList<Attachment>();
            result.add(attachment);

            return OperationResult.buildSuccessResult("????????????????????????", result);
        }

        return OperationResult.buildFailureResult("????????????????????????");
    }

    /**
     * ?????????????????????
     * 
     * @param fid
     *            ??????ID
     * @param chksum
     *            ?????????
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
     * ??????????????????
     * 
     * @param fid
     * @param chksum
     * @param request
     * @return
     */
    @ApiOperation(value = "????????????Id??????????????????", notes = "????????????Id??????????????????????????????|??????")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "fids", value = "??????ID", required = true, paramType = "query", dataType = "String") })
    @RequestMapping(value = "/getAttachmentInfos", method = RequestMethod.GET)
    @ResponseBody
    @JsonView(JsonViews.ApiView.class)
    public OperationResult getAttachmentInfos(@RequestParam("fids") String fids, HttpServletRequest request) {
        if (StringUtils.isBlank(fids)) {
            return OperationResult.buildFailureResult("????????????????????????");
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
        return OperationResult.buildSuccessResult("????????????????????????", result);
    }

    /**
     * ??????????????????
     * 
     * @param attachId
     * @return
     */
    @ApiOperation(value = "????????????Id??????????????????", notes = "????????????Id??????????????????????????????,??????")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "attachId", value = "??????ID", required = true, paramType = "query", dataType = "String"), })
    @RequestMapping(value = "/getAttachmentById", method = RequestMethod.GET)
    @ResponseBody
    @JsonView(JsonViews.ApiView.class)
    public OperationResult getAttachmentById(@RequestParam("attachId") String attachId, HttpServletRequest request) {
        if (StringUtils.isBlank(attachId)) {
            return OperationResult.buildFailureResult("????????????????????????");
        }
        String[] ids = attachId.split(",");
        List<Attachment> result = new ArrayList<Attachment>();
        for (int i = 0; i < ids.length; i++) {
            Attachment attach = new AttachUtils().getAttachmentById(ids[i]);
            if (attach != null) {
                result.add(attach);
            }
        }
        return OperationResult.buildSuccessResult("????????????????????????", result);
    }

    /**
     * ??????????????????
     * 
     * @param attachId
     * @return
     */
    @ApiOperation(value = "????????????????????????????????????", notes = "????????????????????????????????????")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "fileName", value = "????????????", required = true, paramType = "query", dataType = "String"), })
    @RequestMapping(value = "/getAttachmentByFileName", method = RequestMethod.GET)
    @ResponseBody
    @JsonView(JsonViews.ApiView.class)
    public OperationResult getAttachmentByFileName(@RequestParam("fileName") String fileName,
            HttpServletRequest request) {
        if (StringUtils.isBlank(fileName)) {
            return OperationResult.buildFailureResult("????????????????????????");
        }
        Attachment attach = new AttachUtils().getAttachmentByFileName(fileName);
        return OperationResult.buildSuccessResult("????????????????????????", attach);
    }

    @ApiOperation(value = "??????????????????", notes = "??????????????????")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "fileName", value = "????????????", required = true, dataType = "String"),
            @ApiImplicitParam(name = "fileSize", value = "??????", required = true, dataType = "String"),
            @ApiImplicitParam(name = "fileDisplayName", value = "????????????", required = true, dataType = "String"),
            @ApiImplicitParam(name = "contentType", value = "????????????", required = true, dataType = "String"),
            @ApiImplicitParam(name = "fileExt", value = "?????????(??????.)", required = true, dataType = "String"),
            @ApiImplicitParam(name = "clientIp", value = "?????????IP", required = false, dataType = "String"),
            @ApiImplicitParam(name = "relativePath", value = "????????????", required = true, dataType = "String")
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
        // ??????
        attachmentService.save(newAttach);
        return OperationResult.buildSuccessResult("??????????????????", newAttach);
    }

}
