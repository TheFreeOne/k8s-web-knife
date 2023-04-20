//package org.freeone.k8s.web.knife.config.websocket;
//
//
//import io.kubernetes.client.Exec;
//import io.kubernetes.client.openapi.models.V1Pod;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.web.socket.TextMessage;
//import org.springframework.web.socket.WebSocketSession;
//
//import java.io.*;
//import java.util.Arrays;
//import java.util.List;
//import java.util.Map;
//
//
///**
// * https://github.com/ica10888/k8s-terminal
// */
//public class WsConnection extends  Thread {
//
//    private InputStream inputStream ;
//    private OutputStream outputStream;
//    private Exec exec;
//    private WebSocketSession session;
//    private V1Pod pod;
//    private ConsoleSize consoleSize;
//    private Process proc;
//
//    private static Logger logger = LoggerFactory.getLogger(WsConnection.class);
//
//    public WsConnection(Map<String, String> stringStringMap, WebSocketSession session) {
//        this.setSession(session);
//        this.setExec( new Exec());
//        this.setPod(new Pod(stringStringMap.get("name"),stringStringMap.get("namespace"),stringStringMap.get("container")));
//        this.setConsoleSize(new ConsoleSize(stringStringMap.get("cols"),stringStringMap.get("rows")));
//    }
//
//    @Override
//    public void run() {
//        List<String> cmds = Arrays.asList( new String[] {"/bin/bash","/bin/sh"});
//        cmds.forEach((s) -> startProcess(s));
//
//    }
//
//    private void startProcess(String shellPath){
//        String namespace = this.getPod().getNamespace();
//        String name = this.getPod().getName();
//        String container = this.getPod().getContainer();
//        Boolean tty = true;
//        Boolean initValid = true;
//        try {
//            proc = exec.exec(namespace,name,new String[]{shellPath},container,true,tty);
//            outputStream = proc.getOutputStream();
//            inputStream = proc.getInputStream();
//            try {
//                while (true){
//                    byte data[] = new byte[1024];
//                    if (inputStream.read(data) != -1) {
//                        TextMessage textMessage = new TextMessage(data);
//                        if (initValid && isValidBash(textMessage,shellPath)){
//                            break;
//                        } else {
//                            initValid = false;
//                        }
//                        session.sendMessage(textMessage);
//                    }
//                }
//            } catch ( IOException e) {
//                logger.warn("Pipe closed");
//            } finally {
//                proc.destroy();
//                logger.info("session closed... exit thread");
//            }
//
//
//        } catch (ApiException | IOException e) {
//            e.printStackTrace();
//            try {
//                logger.info("ApiException or IOException... close session");
//                session.close();
//            } catch (IOException e1) {
//                e1.printStackTrace();
//            }
//        }
//
//
//    }
//
//    private boolean isValidBash(TextMessage textMessage,String shellPath){
//        String failMessage = "OCI runtime exec failed";
//        if (textMessage.getPayload().trim().indexOf(failMessage) != -1){
//            logger.warn("OCI runtime exec failed: " + shellPath);
//            return true;
//        }else {
//            return  false;
//        }
//    }
//
//    protected void finalize() {
//        try {
//            outputStream.close();
//            inputStream.close();
//            outputStream = null;
//            inputStream = null;
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    public void exit() {
//        proc.destroyForcibly();
//        try {
//            outputStream.close();
//            inputStream.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//    }
//
//
//}
