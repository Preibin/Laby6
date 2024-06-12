import javafx.application.Platform;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CountDownLatch;

/**
 * ThreadTask represents a runnable task that updates the color of a tile based on certain conditions.
 */
public class ThreadTask implements Runnable {
    private final int m; // Row index of the tile
    private final int n; // Column index of the tile
    protected final Tile tile; // The tile to be updated
    private final int k; // Time multiplier for sleep duration
    private final double p; // Probability threshold for random color change
    private final Random rand; // Random number generator
    private final Controller controller; // Controller managing the application
    private final CountDownLatch latch; // Latch to synchronize thread start
    private final Object lock; // Lock for synchronizing tile updates

    /**
     * Constructs a ThreadTask with specified parameters.
     *
     * @param m         Row index of the tile
     * @param n         Column index of the tile
     * @param tile      Tile to be updated
     * @param k         Time multiplier for sleep duration
     * @param p         Probability threshold for random color change
     * @param controller Controller managing the application
     * @param lock      Lock for synchronizing tile updates
     */
    public ThreadTask(int m, int n, Tile tile, int k, double p, Controller controller, Object lock) {
        this.m = m;
        this.n = n;
        this.tile = tile;
        this.k = k;
        this.p = p;
        this.controller = controller;
        this.rand = controller.rand;
        this.latch = controller.latch;
        this.lock = lock;
    }

 
    @Override
    public void run() {
        try {
            latch.await(); // Wait for all threads to be ready
            while (true) {
                int time = (int) Math.round((rand.nextDouble(1.51) + 0.5) * k); // Calculate sleep time
                Thread.sleep(time);
                synchronized (lock) {
                    System.out.println("Start " + m + " " + n);

                    if (!tile.active) continue; // Skip inactive tiles

                    double r_value = rand.nextDouble();
                    if (r_value < p) {
                        Color newColor = Color.rgb(rand.nextInt(256), rand.nextInt(256), rand.nextInt(256)); // Random color
                        setColor(newColor);
                    } else {
                        List<Color> nColors = getNeighborColors();
                        if (!nColors.isEmpty()) {
                            Color averageColor = averageColor(nColors); // Average neighbor colors
                            setColor(averageColor);
                        } else {
                            Color newColor = Color.rgb(0, 0, 0); // Default color
                            setColor(newColor);
                        }
                    }
                    System.out.println("End " + m + " " + n);
                }
            }
        } catch (Exception e) {
            // Handle exceptions
        }
    }

    /**
     * Retrieves the colors of neighboring tiles.
     *
     * @return List of neighboring tile colors
     */
    private List<Color> getNeighborColors() {
        List<Color> nColors = new ArrayList<>();

        if (m < Application.m - 1) {
            addColorIfActive(nColors, m + 1, n);
        } else {
            addColorIfActive(nColors, 0, n);
        }

        if (m > 0) {
            addColorIfActive(nColors, m - 1, n);
        } else {
            addColorIfActive(nColors, Application.m - 1, n);
        }

        if (n < Application.n - 1) {
            addColorIfActive(nColors, m, n + 1);
        } else {
            addColorIfActive(nColors, m, 0);
        }

        if (n > 0) {
            addColorIfActive(nColors, m, n - 1);
        } else {
            addColorIfActive(nColors, m, Application.n - 1);
        }

        return nColors;
    }

    /**
     * Adds the color of a neighboring tile to the list if the tile is active.
     *
     * @param nColors List of neighboring tile colors
     * @param m       Row index of the neighboring tile
     * @param n       Column index of the neighboring tile
     */
    private void addColorIfActive(List<Color> nColors, int m, int n) {
        if (controller.thread_list[m][n].tile.active) {
            nColors.add(controller.thread_list[m][n].tile.getColor());
        }
    }

    /**
     * Calculates the average color from a list of colors.
     *
     * @param colors List of colors
     * @return Average color
     */
    private Color averageColor(List<Color> colors) {
        double r = 0, g = 0, b = 0;
        for (Color color : colors) {
            r += color.getRed();
            g += color.getGreen();
            b += color.getBlue();
        }
        int size = colors.size();
        return Color.color(r / size, g / size, b / size);
    }

    /**
     * Sets the color of the tile on the JavaFX application thread.
     *
     * @param color New color for the tile
     */
    private void setColor(Color color) {
        Platform.runLater(() -> {
            tile.setColor(color);
        });
    }
}