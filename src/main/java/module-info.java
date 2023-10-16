module com.heap {
    requires transitive javafx.graphics;
    requires transitive javafx.controls;
    requires javafx.fxml;

    opens com.heap to javafx.fxml;
    exports com.heap;
}
