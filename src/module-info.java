module javelin
{
    requires transitive javafx.fxml;
    requires transitive javafx.base;
    requires transitive javafx.graphics;
    requires transitive javafx.controls;
    requires com.google.gson;
    exports org.db to javafx.graphics, javafx.fxml;
    opens org.db to javafx.fxml, javafx.base, com.google.gson;
}
