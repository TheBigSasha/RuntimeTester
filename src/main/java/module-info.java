module MultiCOM.main {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires com.google.common;
    opens RuntimeTester to javafx.fxml;
    exports RuntimeTester;
}