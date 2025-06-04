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
    requires com.zaxxer.hikari;

    exports org.example.game_library.database.model;
    opens org.example.game_library.database.model;

    exports org.example.game_library.networking.enums;
    opens org.example.game_library.networking.enums to javafx.fxml;

    exports org.example.game_library.views.tictactoe;
    opens org.example.game_library.views.tictactoe to javafx.fxml;

    exports org.example.game_library.views.menu;
    opens org.example.game_library.views.menu to javafx.fxml;

    exports org.example.game_library.views.minesweeper;
    opens org.example.game_library.views.minesweeper to javafx.fxml;

    exports org.example.game_library.networking.server;
    opens org.example.game_library.networking.server to javafx.fxml;

    exports org.example.game_library.networking.client;
    opens org.example.game_library.networking.client to javafx.fxml;

    exports org.example.game_library.networking.server.tictactoe_game_logic;
    opens org.example.game_library.networking.server.tictactoe_game_logic to javafx.fxml;
}
