import javafx.fxml.FXML;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.paint.Color;

import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Controller class for managing the grid of tiles and the associated threads.
 */
public class Controller {
    @FXML
    private GridPane gridPane; // The GridPane layout for displaying tiles

    private int m; // Number of rows
    private int n; // Number of columns
    private int k; // Time multiplier for sleep duration
    private double p; // Probability threshold for random color change

    protected Random rand; // Random number generator
    protected ThreadTask[][] thread_list; // Array of thread tasks for each tile
    protected CountDownLatch latch; // Latch to synchronize thread start
    protected ExecutorService executor; // Executor service to manage threads

    final Object lock = new Object(); // Lock for synchronizing tile updates

    /**
     * Sets the parameters for the grid and initializes it.
     *
     * @param m Number of rows
     * @param n Number of columns
     * @param k Time multiplier for sleep duration
     * @param p Probability threshold for random color change
     */
    public void setParameters(int m, int n, int k, double p) {
        this.m = m;
        this.n = n;
        this.k = k;
        this.p = p;
        initializeGrid();
    }

    /**
     * Initializes the grid with tiles and starts the associated threads.
     */
    private void initializeGrid() {
        thread_list = new ThreadTask[m][n];
        executor = Executors.newFixedThreadPool(m * n);
        latch = new CountDownLatch(1);
        rand = new Random();

        gridPane.getRowConstraints().clear();
        gridPane.getColumnConstraints().clear();
        gridPane.getChildren().clear();

        // Create column constraints
        for (int i = 0; i < m; i++) {
            ColumnConstraints col = new ColumnConstraints();
            col.setPercentWidth(100.0 / m);
            gridPane.getColumnConstraints().add(col);
        }

        // Create row constraints
        for (int i = 0; i < n; i++) {
            RowConstraints row = new RowConstraints();
            row.setPercentHeight(100.0 / n);
            gridPane.getRowConstraints().add(row);
        }

        // Initialize tiles and threads
        for (int col = 0; col < m; col++) {
            for (int row = 0; row < n; row++) {
                Color startingColor = Color.rgb(rand.nextInt(255), rand.nextInt(255), rand.nextInt(255));
                Tile tile = new Tile(startingColor);
                tile.setOnMouseClicked(event -> {
                    tile.changeState();
                });
                tile.widthProperty().bind(gridPane.widthProperty().divide(m));
                tile.heightProperty().bind(gridPane.heightProperty().divide(n));
                gridPane.add(tile, col, row);

                ThreadTask task = new ThreadTask(col, row, tile, k, p, this, lock);
                thread_list[col][row] = task;
                executor.submit(task);
            }
        }
        latch.countDown(); // Start all threads
    }
}