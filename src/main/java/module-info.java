module org.db.javelin {
    requires transitive javafx.fxml;
    requires transitive javafx.graphics;
    requires transitive javafx.controls;
    requires transitive javafx.base;
    requires com.google.gson;
    requires org.slf4j;

    exports org.db to javafx.graphics, javafx.fxml;
    opens org.db to javafx.fxml, javafx.base, com.google.gson;
}