package dk.lyngby;

import dk.lyngby.config.ApplicationConfig;
import io.javalin.Javalin;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {

        int PORT = Integer.parseInt(ApplicationConfig.getProperty("javalin.port"));
        try(var app = Javalin.create()) {
            ApplicationConfig.startServer(app, PORT);
        }
    }
}