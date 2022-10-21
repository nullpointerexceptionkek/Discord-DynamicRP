module lee.aspect.dev {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.google.gson;
    requires java.desktop;
    requires org.json;
    requires org.slf4j;
    requires java.management;
    requires com.kohlschutter.junixsocket.nativecommon;
    requires org.newsclub.net.unix;


    opens lee.aspect.dev.application to javafx.controls,javafx.fxml,javafx.graphics;
    opens lee.aspect.dev.application.Gui to javafx.controls, javafx.fxml, javafx.graphics;

    exports lee.aspect.dev.application to javafx.controls,javafx.fxml,javafx.graphics;
    exports lee.aspect.dev.application.Gui to javafx.controls,javafx.fxml,javafx.graphics;
}
