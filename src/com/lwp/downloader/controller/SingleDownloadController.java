package com.lwp.downloader.controller;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import jddl.DirectDownloader;
import jddl.DownloadListener;
import jddl.DownloadTask;
import org.jsoup.Jsoup;
import org.jsoup.helper.StringUtil;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;

import com.lwp.downloader.Constants;

/**
 * Author: liuwuping
 * Date: 17/9/20
 * Description:
 */
public class SingleDownloadController {

    @FXML
    private ComboBox typeCombo;

    @FXML
    private TextField urlText;

    @FXML
    private Label msgLabel;

    @FXML
    private Button checkBtn;

    @FXML
    private Button downBtn;

    private boolean checkFlag = false;

    private String fileUrl;

    private String outDir = "C:\\video\\";


    @FXML
    public void initialize() {
        typeCombo.getItems().addAll(Constants.SOURCE_KUAISHOU, Constants.SOURCE_HUOSHAN);
        typeCombo.getSelectionModel().selectFirst();
        File outDir = new File("C:\\video\\");
        if (!outDir.exists()) {
            outDir.mkdir();
        }
    }

    @FXML
    public void onCheckUrl(ActionEvent actionEvent) {

        Task<String> urlParseTask = new Task<String>() {
            @Override
            protected String call() throws Exception {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        checkBtn.setText("正在解析");
                        checkBtn.setDisable(true);
                    }
                });
                String url = urlText.getText();
                if (StringUtil.isBlank(url)) {
                    return null;
                }
                try {
                    String selectedItem = typeCombo.getValue().toString();
                    System.out.print(selectedItem);
                    Document doc = null;
                    if (selectedItem.equals(Constants.SOURCE_KUAISHOU)) {
                        doc = Jsoup.connect(url).get();
                        Element video = doc.select("video").first();
                        return video.attr("src");
                    } else if (selectedItem.equals(Constants.SOURCE_HUOSHAN)) {
                        doc = Jsoup.connect(url).get();
                        Element script = doc.select("script").get(4);
                        String scriptText = script.html();
                        String videoUrl = scriptText.substring(scriptText.indexOf(".create(") + 8, scriptText.indexOf("}}});") + 3);

                        return videoUrl;
                    }
                    return null;
                } catch (Exception e) {
                    return null;
                }
            }
        };

        urlParseTask.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent t) {
                checkBtn.setText("解析");
                checkBtn.setDisable(false);
                fileUrl = urlParseTask.getValue();
                if (StringUtil.isBlank(fileUrl)) {
                    showError("解析错误，请检查地址是否正确！");
                } else
                    showSuc("解析成功，请点击下载按钮开始下载！");
            }
        });

        new Thread(urlParseTask).start();
    }

    @FXML
    public void onDownload(ActionEvent actionEvent) {
        if (StringUtil.isBlank(fileUrl)) {
            showDownError("请先输入url地址进行解析！");
            return;
        }
        DirectDownloader dd = new DirectDownloader();
        String out = outDir + System.currentTimeMillis() + ".mp4";
        try {
            dd.download(new DownloadTask(new URL(fileUrl), new FileOutputStream(out), new DownloadListener() {
                String fname;
                int totalSize;

                public void onUpdate(int bytes, int totalDownloaded) {
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            downBtn.setDisable(true);
                            downBtn.setText("正在下载 ");
                        }
                    });
                }

                public void onStart(String fname, int size) {
                    this.fname = fname;
                    this.totalSize = size;
                }

                public void onComplete() {
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            downBtn.setDisable(false);
                            msgLabel.setText("");
                            downBtn.setText("下载");
                            showDownSuc("下载成功！");
                        }
                    });

                }

                public void onCancel() {

                }
            }));
            // Start downloading
            Thread t = new Thread(dd);
            t.start();
            t.join();
        } catch (Exception e) {
            showDownError("下载失败！");
        }
    }


    private void showError(String message) {
        msgLabel.setText(message);
        msgLabel.setTextFill(Color.RED);
    }

    private void showSuc(String message) {
        msgLabel.setText(message);
        msgLabel.setTextFill(Color.GREEN);
    }

    private void showDownError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("信息提示");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.setWidth(200);
        alert.setHeight(100);
        alert.showAndWait();
    }

    private void showDownSuc(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("信息提示");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.setWidth(200);
        alert.setHeight(100);
        alert.showAndWait();
    }


}
