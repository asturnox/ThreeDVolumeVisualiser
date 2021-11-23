package threedobjectvisualiser;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.paint.Color;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Main controller for the Viewer window.
 * Renders object, controls how object is render, provides control GUI.
 */
public class ViewerController implements Initializable {
    private GraphicsContext gc;
    /**
     * Viewing parameters
     */
    private float shift3d = 1.0f;
    private float shift2d = 5.0f;
    private int originX = 1024 / 4, originY = 768 / 2;
    private int xSquareSize = 10, ySquareSize = 10;
    private int viewAngle = 1;
    private final int sens = 50;

    /**
     * Function graph parameters
     */
    private final int xGridSize = 50, yGridSize = 50;
    private final int[][] map = new int[xGridSize][yGridSize];

    /**
     * User function associated represented by viewer
     */
    private UserFunction userFunction;
    public static String userFunctionString;


    /**
     * Calculates the height of volume of revolution for each point (x, y) in the plane.
     * Adds the resulting points (x, y, z) to map.
     */
    private void generateZCoordinates() {
        double magnitude = findMaxValue(userFunction);

        for (int y = yGridSize / 4; y < yGridSize - 1; y++) {
            for (double r = 0; r < Math.PI; r += 0.01) {
                double res = userFunction.apply(y - yGridSize / 4.0);

                int x = (int) Math.round((xGridSize * (res * Math.cos(r)) / magnitude) + xGridSize / 2.0 - 1.0);

                if (x >= 0 && x < xGridSize) {
                    int z = (int) Math.round(((xGridSize * ((res * Math.sin(r)))) + xGridSize / 2.0 - 1) / magnitude);
                    setZ(x, y, z);
                }
            }
        }
    }

    /**
     * Draws one half of the solid of revolution.
     *
     * @param direction 1 specifies to draw the top part, -1 specifies the bottom part
     */
    private void drawPart(int direction) {
        int xs = xSquareSize;
        int ys = ySquareSize;

        for (int y = 0; y < yGridSize - 1; y++) {
            for (int x = 0; x < xGridSize - 1; x++) {
                try {
                    if (getZ(x, y) == 0 || getZ(x + 1, y) == 0) // skip drawing if point depth == 0
                        continue;
                    // draw lines in the x-direction
                    Point3D point3d1 = new Point3D(x * xs, (int) (direction * getZ(x, y) * shift3d), y * ys);
                    Point3D point3d2 = new Point3D((x + 1) * xs, (int) (direction * getZ(x + 1, y) * shift3d), y * ys);

                    Point2D point1 = transform3D(point3d1);
                    Point2D point2 = transform3D(point3d2);
                    setElevationColour(point3d1.y, point3d2.y);
                    gc.strokeLine(point1.x + originX, point1.y + originY, point2.x + originX, point2.y + originY);

                    // draw lines in the y-direction
                    point3d2 = new Point3D(x * xs, (int) (direction * getZ(x, y + 1) * shift3d), (y + 1) * ys);
                    point2 = transform3D(point3d2);
                    gc.strokeLine(point1.x + originX, point1.y + originY, point2.x + originX, point2.y + originY);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }


    /**
     * Takes in a point in 3D space, represents this as a 2D point.
     *
     * @param point3D point to transform
     * @return 2D point transformed
     */
    private Point2D transform3D(Point3D point3D) {
        switch (viewAngle) {
            case 0:
                return new Point2D(point3D.x + point3D.z, (int) (((-point3D.y * shift2d) + point3D.z) * shift3d));
            case 1:
                return new Point2D(point3D.x + point3D.z, (int) (((-point3D.y * shift2d) + point3D.z - point3D.x) * shift3d));
            case 2:
                return new Point2D(point3D.x - point3D.z, (int) (((-point3D.y * shift2d) + point3D.z) * shift3d));
            default:
                throw new IllegalArgumentException();
        }
    }


    /**
     * Finds the maximum value of a given function for the grid.
     * Used for scaling height.
     *
     * @param function function used
     * @return maximum value found for grid
     */
    private double findMaxValue(UserFunction function) {
        double max = Double.MIN_VALUE;
        for (int i = 0; i < yGridSize - 1; i++) {
            double result = function.apply((double) i);
            if (result > max)
                max = result;
        }

        return max;
    }


    /**
     * Sets the colour of line used when drawing a line between two points
     *
     * @param y1 height of first point
     * @param y2 height of second point
     */
    private void setElevationColour(int y1, int y2) {
        int yAvg = (y1 + y2) / 2;
        if ((yAvg > -100) && (yAvg < -10)) gc.setStroke(Color.DARKBLUE);
        if ((yAvg > -10) && (yAvg < -2)) gc.setStroke(Color.BLUE);
        if (yAvg == -1) gc.setStroke(Color.AQUAMARINE);
        if ((yAvg > 0) && (yAvg < 10)) gc.setStroke(Color.GREEN);
        if ((yAvg > 10) && (yAvg < 15)) gc.setStroke(Color.GRAY);
        if ((yAvg > 15) && (yAvg < 20)) gc.setStroke(Color.WHITE);
    }


    /**
     * Clears the canvas, draws both top and bottom of volume.
     */
    private void map3Dto2D() {
        clear();
        drawPart(1);
        drawPart(-1);
    }

    public void clear() {
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }

    private void setUserFunction() {
        userFunction = new UserFunction(userFunctionString);
    }

    private int getZ(int x, int y) {
        return map[x][y];
    }

    private void setZ(int x, int y, int z) {
        map[x][y] = z;
    }

    @FXML
    Canvas canvas;

    /**
     * Initializes Viewer window, gets UserFunction from UserFunction class, draws 3D Object from UserFunction.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        gc = canvas.getGraphicsContext2D();
        clear();
        setUserFunction();
        generateZCoordinates();
        map3Dto2D();

    }

    @FXML
    Button up;

    @FXML
    Button down;

    @FXML
    Button left;

    @FXML
    Button right;

    @FXML
    Button zoomUp;

    @FXML
    Button zoomDown;

    @FXML
    Button rightView;

    @FXML
    Button centerView;

    @FXML
    Button leftView;

    @FXML
    Button increaseShift;

    @FXML
    Button decreaseShift;

    @FXML
    Button increaseShift3d;

    public void zoomDown() {
        xSquareSize--;
        ySquareSize--;
        map3Dto2D();
    }

    public void zoomUp() {
        xSquareSize++;
        ySquareSize++;
        map3Dto2D();
    }


    public void goRight() {
        originX += sens;
        map3Dto2D();
    }

    public void goLeft() {
        originX -= sens;
        map3Dto2D();
    }

    public void goUp() {
        originY += sens;
        map3Dto2D();
    }

    public void goDown() {
        originY -= sens;
        map3Dto2D();
    }

    /**
     * Increases angle of view in the y-direction.
     */
    public void increaseShift() {
        shift3d += 0.05f;
        increaseShift3d();
        map3Dto2D();
    }

    /**
     * Decreases angle of view in the y-direction.
     */
    public void decreaseShift() {
        shift3d -= 0.05f;
        increaseShift3d();
        map3Dto2D();
    }

    /**
     * Increases depth of view.
     */
    public void increaseShift3d() {
        shift2d += 0.1f;
        map3Dto2D();
    }

    public void setLeftView() {
        viewAngle = 0;
        map3Dto2D();
    }

    public void setCenterView() {
        viewAngle = 1;
        map3Dto2D();
    }

    public void setRightView() {
        viewAngle = 2;
        map3Dto2D();
    }
}
