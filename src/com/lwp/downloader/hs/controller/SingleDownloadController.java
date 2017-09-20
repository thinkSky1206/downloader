package com.lwp.downloader.hs.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Author: liuwuping
 * Date: 17/9/20
 * Description:
 */
public class SingleDownloadController {

    @FXML
    public static Button checkBtn;

    @FXML
    public static Button downBtn;




    public void onCheckUrl(ActionEvent actionEvent) {
        System.out.println("check");
    }

    public void onDownload(ActionEvent actionEvent) {
        System.out.println("down");
    }
}
