package org.freeone.k8s.web.knife.controller;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.freeone.k8s.web.knife.entity.TemporaryFile;
import org.freeone.k8s.web.knife.repository.TemporaryFileRepository;
import org.freeone.k8s.web.knife.utils.CommonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;

@RestController
public class DockerBuildFileDownloadController {


    @Autowired
    private TemporaryFileRepository temporaryFileRepository;

    @GetMapping("getWarOrJar")
    public ResponseEntity<byte[]> newdownload() throws IOException {

        String path = CommonUtils.getUploadPath() + "ROOT.war";
        File file = new File(path);
        System.out.println(file.getName());
        byte[] readFileToByteArray = FileUtils.readFileToByteArray(file);
        HttpHeaders headers = new HttpHeaders();
        //  "application/octet-stream"; //通知浏览器下载文件而不是打开
//        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        ContentDisposition contentDisposition = ContentDisposition.attachment().filename(java.net.URLEncoder.encode("ROOT.war", "UTF-8")).build();
        headers.setContentDisposition(contentDisposition);
        return new ResponseEntity<byte[]>(readFileToByteArray, headers, HttpStatus.OK);
    }

    @GetMapping("/getFile/{filename}/{id}/{ticket}")
    @Deprecated
    public ResponseEntity<byte[]> get(@PathVariable String filename,@PathVariable Long id,@PathVariable String ticket) throws IOException {
        if (id == null || id < 10_000 || StringUtils.isBlank(ticket)) {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            return new ResponseEntity<>(new byte[0], headers, HttpStatus.BAD_REQUEST);
        }

        TemporaryFile temporaryFile = temporaryFileRepository.findById(id).orElse(null);
        if (temporaryFile == null || !ticket.equals(temporaryFile.getTicket())) {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            return new ResponseEntity<>(new byte[0], headers, HttpStatus.BAD_REQUEST);
        }


        File file = new File(temporaryFile.getFilePath());
        String name = file.getName();
        if (!name.equals(filename)) {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            return new ResponseEntity<>(new byte[0], headers, HttpStatus.BAD_REQUEST);
        }


        byte[] readFileToByteArray = FileUtils.readFileToByteArray(file);
        HttpHeaders headers = new HttpHeaders();
        //  "application/octet-stream"; //通知浏览器下载文件而不是打开
//        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);

        ContentDisposition contentDisposition = ContentDisposition.attachment().filename(java.net.URLEncoder.encode(name, "UTF-8")).build();
        headers.setContentDisposition(contentDisposition);
        return new ResponseEntity<>(readFileToByteArray, headers, HttpStatus.OK);
    }
  @GetMapping("/download/{id}/{ticket}/{filename}")
    public ResponseEntity<byte[]> download(@PathVariable String filename,@PathVariable Long id,@PathVariable String ticket) throws IOException {
        if (id == null || id < 10_000 || StringUtils.isBlank(ticket)) {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            return new ResponseEntity<>(new byte[0], headers, HttpStatus.BAD_REQUEST);
        }

        TemporaryFile temporaryFile = temporaryFileRepository.findById(id).orElse(null);
        if (temporaryFile == null || !ticket.equals(temporaryFile.getTicket())) {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            return new ResponseEntity<>(new byte[0], headers, HttpStatus.BAD_REQUEST);
        }


        File file = new File(temporaryFile.getFilePath());
        String name = file.getName();
        if (!name.equals(filename)) {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            return new ResponseEntity<>(new byte[0], headers, HttpStatus.BAD_REQUEST);
        }


        byte[] readFileToByteArray = FileUtils.readFileToByteArray(file);
        HttpHeaders headers = new HttpHeaders();
        //  "application/octet-stream"; //通知浏览器下载文件而不是打开
//        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);

        ContentDisposition contentDisposition = ContentDisposition.attachment().filename(java.net.URLEncoder.encode(name, "UTF-8")).build();
        headers.setContentDisposition(contentDisposition);
        return new ResponseEntity<>(readFileToByteArray, headers, HttpStatus.OK);
    }


}
