module MultiCOM.main {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    opens RuntimeTester to javafx.fxml;
    exports RuntimeTester;
}