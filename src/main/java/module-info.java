module lee.aspect.dev {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.google.gson;
    requires java.desktop;
    requires json;
    requires slf4j.api;
    requires java.management;
    requires junixsocket.common;


    opens lee.aspect.dev.application to javafx.controls,javafx.fxml,javafx.graphics;
    opens lee.aspect.dev.application.Gui.Settings to javafx.controls,javafx.fxml,javafx.graphics;
    opens lee.aspect.dev.application.Gui.callbackscreen to javafx.controls,javafx.fxml,javafx.graphics;
    opens lee.aspect.dev.application.Gui.config.ready to javafx.controls,javafx.fxml,javafx.graphics;
    opens lee.aspect.dev.application.Gui.config to javafx.controls,javafx.fxml,javafx.graphics;
    opens lee.aspect.dev.application.Gui.LoadingScreen to javafx.controls,javafx.fxml,javafx.graphics;

    exports lee.aspect.dev.application to javafx.controls,javafx.fxml,javafx.graphics;
    exports lee.aspect.dev.application.Gui.Settings to javafx.controls,javafx.fxml,javafx.graphics;
    exports lee.aspect.dev.application.Gui.callbackscreen to javafx.controls,javafx.fxml,javafx.graphics;
    exports lee.aspect.dev.application.Gui.config.ready to javafx.controls,javafx.fxml,javafx.graphics;
    exports lee.aspect.dev.application.Gui.config to javafx.controls,javafx.fxml,javafx.graphics;
    exports lee.aspect.dev.application.Gui.LoadingScreen to javafx.controls,javafx.fxml,javafx.graphics;
}
