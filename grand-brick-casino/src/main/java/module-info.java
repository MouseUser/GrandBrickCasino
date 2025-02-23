module casino {
    requires javafx.controls;
    requires javafx.fxml;

    opens casino to javafx.fxml;
    exports casino;
}
