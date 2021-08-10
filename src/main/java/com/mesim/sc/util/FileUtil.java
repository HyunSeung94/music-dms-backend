package com.mesim.sc.util;

import com.mesim.sc.exception.BackendException;
import lombok.extern.slf4j.Slf4j;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.PNGTranscoder;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

@Slf4j
@Component
public class FileUtil {

    public static String DEFAULT_FILE_PATH;
    public static String RESOURCE_FILE_PATH;
    public static String DATA_FILE_PATH;
    public static String INFRALAYER_FILE_PATH;
    public static String EVENTALYER_FILE_PATH;
    public static int BUF_SIZE = 1024 * 1024 * 100;


    @Value("${file.data.base.path}")
    public void setDefaultFilePath(String defaultFilePath) {
        FileUtil.DEFAULT_FILE_PATH = defaultFilePath;
    }

    @Value("${file.resource.base.path}")
    public void setResourceFilePath(String resourceFilePath) {
        FileUtil.RESOURCE_FILE_PATH = resourceFilePath;
    }

    @Value("${file.data.base.path}")
    public void setDataFilePath(String dataFilePath) {
        FileUtil.DATA_FILE_PATH = dataFilePath;
    }

    @Value("${file.data.infralayer.path}")
    public void setInfralayerFilePath(String path) {
        FileUtil.INFRALAYER_FILE_PATH = path;
    }

    @Value("${file.data.eventlayer.path}")
    public void setEventalyerFilePath(String path) {
        FileUtil.EVENTALYER_FILE_PATH = path;
    }


    public static boolean upload(String path, String name, MultipartFile file) throws IOException {
        File folder = new File(path);

        if (!folder.exists()) {
            folder.mkdirs();
        }

        File saveFile = new File(path + System.getProperty("file.separator") + name);
        file.transferTo(saveFile);

        return true;
    }

    public static boolean moveFile(String path, String name,String fileTempPath) throws IOException {
        File folder = new File(path);

            if (!folder.exists()) {
                folder.mkdirs();
            }

            File tempFile = new File (fileTempPath+ System.getProperty("file.separator") + name);
            File saveFile = new File(path + System.getProperty("file.separator") + name);
            tempFile.renameTo(saveFile);

        return true;
    }

    public static byte[] download(String filePath) throws BackendException {
        byte[] fileByteArray;

        FileInputStream fis = null;
        ByteArrayOutputStream baos = null;

        try {
            fis = new FileInputStream(filePath);
            baos = new ByteArrayOutputStream();
            int bytesRead = -1;
            int size = fis.available();
            if (size == 0) {
                return null;
            }
            byte[] buf = new byte[size];
            while ((bytesRead = fis.read(buf)) != -1) {
                baos.write(buf, 0, bytesRead);
            }

            fileByteArray = baos.toByteArray();

        } catch (UnsupportedEncodingException e) {
            throw new BackendException("지원하지 않는 인코딩입니다.", e);
        } catch (FileNotFoundException e) {
            throw new BackendException("파일을 찾을 수 없습니다.", e);
        } catch(IOException e) {
            throw new BackendException("파일 읽는 중 오류가 발생했습니다.", e);
        } finally {
            try {
                if (baos != null) {
                    baos.close();
                }
                if (fis != null) {
                    fis.close();
                }
            } catch (IOException e) {
                throw new BackendException("자원 해제 중 오류가 발생했습니다.", e);
            }
        }

        return fileByteArray;
    }

    public static File svgToPng(File imgFile) throws BackendException {
        try {
            TranscoderInput svgTi = new TranscoderInput("file:" + imgFile.getAbsolutePath());

            String pngFilePath = imgFile.getAbsolutePath().replace(".svg", ".png");
            OutputStream pngOs = new FileOutputStream(pngFilePath);
            TranscoderOutput pngTo = new TranscoderOutput(pngOs);

            PNGTranscoder pngTranscoder = new PNGTranscoder();
            pngTranscoder.transcode(svgTi, pngTo);

            pngOs.flush();
            pngOs.close();

            return new File(pngFilePath);
        } catch (Exception e) {
            throw new BackendException("이미지 png 변환 중 오류가 발생했습니다.", e);
        }
    }

    public static String makePath(String... paths) {
        log.info(String.join(System.getProperty("file.separator"), paths));
        return String.join(System.getProperty("file.separator"), paths);

    }

    public static String getExt(String name) {
        int index = name.lastIndexOf(".");
        if (index >= 0) {
            return name.substring(index + 1);
        } else {
            return "";
        }
    }

    public static String setExt(String originFileNm, String targetFileNm) {
        String originExt = FileUtil.getExt(originFileNm);
        String targetExt = FileUtil.getExt(targetFileNm);

        if (targetExt.equals("")) {
            return originFileNm;
        } else if (originExt.equals("")) {
            return originFileNm + "." + targetExt;
        } else {
            return originFileNm.replace("." + originExt, "." + targetExt);
        }
    }

    public static String getImgBase64Str(String imgSrcPath) throws BackendException {
        String filePath = FileUtil.makePath(imgSrcPath);
        File file = new File(filePath);
        if (file.exists()) {
            try {
                String sourceStr;
                if (imgSrcPath.endsWith("SVG") || imgSrcPath.endsWith("svg")) {
                    sourceStr = "svg+xml";
                } else {
                    sourceStr = imgSrcPath.substring(imgSrcPath.lastIndexOf(".") + 1);
                }
                String base64Str = "data:image/" + sourceStr + ";base64," + Base64.getEncoder().encodeToString(FileUtils.readFileToByteArray(file));
                return base64Str;
            } catch (IOException e) {
                throw new BackendException("이미지 인코딩 중 오류가 발생했습니다.", e);
            }
        }
        return null;
    }

    public static List<String> fileList(String filePath){
        List<String> fileNameList = new ArrayList<>();
        File rw = new File(filePath);

        /*파일 경로에 있는 파일 리스트 fileList[] 에 넣기*/
        File[] fileList = rw.listFiles();
        if(fileList != null){
            /*fileList에 있는거 for 문 돌려서 출력*/
            for(File file : fileList) {
                if(file.isFile()) {
                    String fileName =  file.getName();
                    fileNameList.add(fileName);

                }
            }
        }
        return fileNameList;
    }

    public static void deleteFile(String path){
        File deleteFolder = new File(path);

        if(deleteFolder.exists()){
            File[] deleteFolderList = deleteFolder.listFiles();

            for (int i = 0; i < deleteFolderList.length; i++) {
                if(deleteFolderList[i].isFile()) {
                    deleteFolderList[i].delete();
                }else {
                    deleteFile(deleteFolderList[i].getPath());
                }
                deleteFolderList[i].delete();
            }
            deleteFolder.delete();
        }
    }

    public static File compress(String outPath, String filePath, List<String> fileList) throws BackendException {
        byte[] buf = new byte[BUF_SIZE];


        File zipFile = null;
        FileInputStream fis = null;
        ZipArchiveOutputStream zos = null;
        BufferedInputStream bis = null;

        try {
            if (!(new File(outPath).exists())) {
                FileUtils.forceMkdir(new File(outPath));
            }

            zipFile = new File(outPath, UUID.randomUUID().toString() + ".zip");

            zos = new ZipArchiveOutputStream(new BufferedOutputStream(new FileOutputStream(zipFile)));

            for (String file : fileList) {

                String fileFullPath = FileUtil.makePath(filePath, file);

                // 디렉토리 안의 다른 디렉토리는 무시
                if (new File(fileFullPath).isDirectory()) {
                    continue;
                }

                zos.setEncoding("UTF-8");

                fis = new FileInputStream(fileFullPath);
                bis = new BufferedInputStream(fis, BUF_SIZE);
                zos.putArchiveEntry(new ZipArchiveEntry(file));

                int len;
                while((len = bis.read(buf, 0, BUF_SIZE)) != -1){
                    zos.write(buf, 0, len);
                }

                bis.close();
                fis.close();
                zos.closeArchiveEntry();

            }
            zos.close();

        } catch (FileNotFoundException e) {
            throw new BackendException("파일을 찾을 수 없습니다.", e);
        } catch(IOException e) {
            throw new BackendException("파일 읽는 중 오류가 발생했습니다.", e);
        } finally {
            try {
                if (zos != null) {
                    zos.close();
                }
                if (fis != null) {
                    fis.close();
                }
                if (bis != null) {
                    bis.close();
                }
            } catch (IOException e) {
                throw new BackendException("자원 해제 중 오류가 발생했습니다.", e);
            }
        }

        return zipFile;
    }

    public static String getAudioBase64Str(String imgSrcPath) {
        String filePath = FileUtil.makePath(imgSrcPath);
        log.info(filePath);
        File file = new File(filePath);
        if (file.exists()) {
            try {
                String sourceStr;
                sourceStr = imgSrcPath.substring(imgSrcPath.lastIndexOf(".") + 1);
                String base64Str = "data:audio/" + sourceStr + ";base64," + Base64.getEncoder().encodeToString(FileUtils.readFileToByteArray(file));
                return base64Str;
            } catch(IOException e) {
                log.error(e.getMessage());
            }
        }
        return null;
    }

}

