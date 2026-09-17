package net.rationalminds.massmailer.ui;

/**
 * Entry point that does not extend javafx.application.Application, so the
 * java launcher's fat-jar / classpath-mode JavaFX detection does not trigger
 * "Error: JavaFX runtime components are missing" when running the shaded jar.
 */
public class Launcher {

    public static void main(String[] args) {
        MassSenderUI.main(args);
    }
}
