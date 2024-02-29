package com.lemonzuo.license.mobaxterm.generator;

import com.wanna.keygen.core.License;
import com.wanna.keygen.core.LicenseType;
import com.wanna.keygen.util.Generator;
import com.wanna.keygen.util.impl.JavaGenerator;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.concurrent.*;
import java.util.regex.Pattern;

/**
 * @author wanna
 * @since 2019-01-03
 */
public class MainController {

    private static final Logger logger = LoggerFactory.getLogger(MainController.class);

    private static final Pattern VERSION_MATCHER = Pattern.compile("\\d+\\.\\d+$");

    @FXML
    private TextField userName;

    @FXML
    private ComboBox<LicenseType> licenseType;

    @FXML
    private TextField targetVersion;

    @FXML
    private CheckBox gameBox;

    @FXML
    private CheckBox pluginBox;

    public static ExecutorService executor;

    /**
     * Generator
     */
    private Generator generator;

    static {
        ThreadFactory threadFactory = Executors.defaultThreadFactory();
        executor = new ThreadPoolExecutor(1, 1, 0, TimeUnit.MILLISECONDS,
                new ArrayBlockingQueue<>(1), threadFactory, new ThreadPoolExecutor.CallerRunsPolicy());
    }

    public void initialize() {
        // 初始化 许可 类型
        initLicenseType();
        // 初始化 版本监听器
        initVersionListener();
        // 初始化实现方式
        generator = new JavaGenerator();
    }

    /**
     * 初始化 版本监听器
     */
    private void initVersionListener() {
        targetVersion.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal) {
                checkVersionFormat();
            }
        });
    }

    /**
     * 初始化许可类型
     */
    private void initLicenseType() {
        licenseType.setItems(FXCollections.observableArrayList(LicenseType.values()));
        licenseType.setValue(LicenseType.Professional);
    }


    /**
     * 检查版本是否合法
     */
    private void checkVersionFormat() {
        String version = targetVersion.getText();
        if (!VERSION_MATCHER.matcher(version).find()) {
            Alert alert = createAlert("版本号格式为 [主版本号.次版本号], 如10.9");
            alert.showAndWait();
            targetVersion.requestFocus();
        }
    }

    /**
     * 生成注册文件
     */
    public void register() {
        String name = userName.getText();
        String version = targetVersion.getText();
        if (Objects.equals("", name.trim()) || Objects.equals("", version.trim())) {
            Alert alert = createAlert("请确定用户名和版本已填写");
            alert.showAndWait();
            return;
        }

        // 封装成 license
        License license = pakLicense(name, version);

        String licenseKey = license.getLicenseKey();
        String encryptBytes = EncryptUtil.encryptBytes(0x787, licenseKey.getBytes(StandardCharsets.UTF_8));
        String content = VariantBase64.variantBase64Encode(encryptBytes.getBytes(StandardCharsets.UTF_8));

        executor.submit(() -> {
            try {
                generator.generate(content);
            } catch (Exception e) {
                logger.error("生成注册文件出错,{}....", e);
                Alert alert = createAlert("保存key出错,稍后请重试");
                alert.showAndWait();
            }
        });
    }

    /**
     * package license
     *
     * @param userName userName
     * @param version  version
     * @return license
     */
    private License pakLicense(String userName, String version) {
        LicenseType value = licenseType.getValue();
        boolean openGame = gameBox.isSelected();
        boolean openPlugin = pluginBox.isSelected();

        License license = new License(userName, version);
        license.setLicenseType(value);
        license.setOpenGames(openGame);
        license.setOpenPlugins(openPlugin);
        return license;
    }

    /**
     * 创建弹窗警告
     *
     * @param content 弹窗内容
     * @return alert
     */
    private Alert createAlert(String content) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("警告");
        alert.setHeaderText(null);
        alert.setContentText(content);
        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image(("/static/logo.png")));
        return alert;
    }

    /**
     * 打开超链接
     *
     * @param actionEvent event
     */
    public void openItemLink(ActionEvent actionEvent) {
        Object source = actionEvent.getSource();
        MenuItem item = (MenuItem) source;
        openHyperlink(item.getUserData().toString());
    }

    /**
     * 打开超链接
     *
     * @param actionEvent event
     */
    public void openHyperlink(ActionEvent actionEvent) {
        Object source = actionEvent.getSource();
        Hyperlink hyperlink = (Hyperlink) source;
        openHyperlink(hyperlink.getText());
    }

    /**
     * 打开超链接
     *
     * @param url url
     */
    public void openHyperlink(String url) {
/*        HostServicesDelegate hostServices = HostServicesFactory.getInstance(App.getInstance());
        hostServices.showDocument(url);*/
    }

    /**
     * 打开生成路径
     */
    public void openPath() {
        if (Desktop.isDesktopSupported()) {
            executor.submit(() -> {
                try {
                    String file = new File("").getAbsolutePath();
                    Desktop.getDesktop().browse(new File(file).toURI());
                } catch (IOException e) {
                    logger.error("打开生成文件夹出错,{}.....", e);
                }
            });
        }
    }

}
