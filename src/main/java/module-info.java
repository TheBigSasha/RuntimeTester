module RuntimeEfficiency.main {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.xml;
    requires java.desktop;

    exports RuntimeTester;

    opens RuntimeTester to javafx.fxml;
}