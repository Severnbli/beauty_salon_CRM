module by.bsuir.client {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires static lombok;
    requires io.github.cdimascio.dotenv.java;
    requires com.google.gson;
    requires java.logging;
    requires java.sql;
    requires java.desktop;

    opens by.bsuir.client.controllers to javafx.fxml;
    opens by.bsuir.client.models to com.google.gson;
    exports by.bsuir.client;
    opens by.bsuir.client.utils to javafx.fxml;
}