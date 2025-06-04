module org.example.game_library {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.hibernate.orm.core;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires java.desktop;
    requires java.logging;
    requires jakarta.persistence;
    requires static lombok;
    requires jbcrypt;

    exports org.example.game_library.database.model;
    opens org.example.game_library.database.model;

    exports org.example.game_library.networking;
    opens org.example.game_library.networking to javafx.fxml;

    exports org.example.game_library.views;
    opens org.example.game_library.views to javafx.fxml;
}
