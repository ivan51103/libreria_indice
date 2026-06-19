module com.biblioteca {
    requires javafx.controls;
    requires java.sql;
    requires org.xerial.sqlitejdbc;

    exports com.biblioteca.app;
    exports com.biblioteca.ui.view;
    exports com.biblioteca.util;
}
