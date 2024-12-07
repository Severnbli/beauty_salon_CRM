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
    requires java.sql;
    requires gson.javatime.serialisers;
    requires javafx.base;
    requires kernel;
    requires layout;
    requires org.apache.poi.ooxml;
    requires io;
    requires org.bouncycastle.pkix;

    opens by.bsuir.client.controllers to javafx.fxml;
    opens by.bsuir.client.models to com.google.gson, javafx.base;
    exports by.bsuir.client;
    opens by.bsuir.client.utils to javafx.fxml;
}