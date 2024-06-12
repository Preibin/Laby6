import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Main application class for setting up and launching the JavaFX application.
 */
public class Application extends javafx.application.Application {

    public static int m; // Number of rows
    public static int n; // Number of columns
    private static int k; // Time multiplier for sleep duration
    private static double p; // Probability threshold for random color change
    Controller controller; // Controller for managing the grid and threads

    /**
     * Starts the JavaFX application by setting up the stage and scene.
     *
     * @param stage The primary stage for this application
     * @throws IOException If the FXML file cannot be loaded
     */
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/app-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 320, 240);

        controller = fxmlLoader.getController();
        controller.setParameters(m, n, k, p);

        stage.setTitle("Disco!");
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Main method to launch the application.
     *
     * @param args Command-line arguments specifying the grid parameters
     */
    public static void main(String[] args) {
        m = Integer.parseInt(args[0]);
        n = Integer.parseInt(args[1]);
        k = Integer.parseInt(args[2]);
        p = Double.parseDouble(args[3]);
        launch();
    }

    /**
     * Stops the application and shuts down the executor service.
     */
    @Override
    public void stop() {
        controller.executor.shutdownNow();
    }
}