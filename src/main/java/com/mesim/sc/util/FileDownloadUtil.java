package com.mesim.sc.util;

import lombok.extern.slf4j.Slf4j;
import org.asynchttpclient.*;

@Slf4j
public class FileDownloadUtil {

    public static String downloadFile(String fileURL) {

        AsyncHttpClient client = Dsl.asyncHttpClient();

        String fileStr = null;
        try {
            Request getRequest = Dsl.get(fileURL).build();
            ListenableFuture<Response> responseFuture = client.executeRequest(getRequest);
            Response response = responseFuture.get();
            fileStr = response.getResponseBody();
        } catch(Exception e){
            log.error("파일 다운로드 실패" + "\t" + fileURL);
            fileStr = null;

        }

        return fileStr;
    }
}
