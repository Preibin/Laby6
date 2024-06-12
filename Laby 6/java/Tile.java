import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 * Tile represents a rectangular tile with a color and an active state.
 */
public class Tile extends Rectangle {
    protected volatile boolean active; // Indicates whether the tile is active

    /**
     * Constructs a Tile with the specified starting color.
     *
     * @param startingColor The initial color of the tile
     */
    public Tile(Color startingColor) {
        super();
        this.setFill(startingColor);
        this.active = true;
    }

    /**
     * Retrieves the current color of the tile.
     *
     * @return The current color of the tile
     */
    public synchronized Color getColor() {
        return (Color) this.getFill();
    }

    /**
     * Sets a new color for the tile.
     *
     * @param newColor The new color to be set
     */
    public synchronized void setColor(Color newColor) {
        this.setFill(newColor);
    }

    /**
     * Toggles the active state of the tile.
     */
    protected synchronized void changeState() {
        active = !active;
    }
}
