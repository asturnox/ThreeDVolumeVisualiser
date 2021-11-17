package threedobjectvisualiser;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.paint.Color;

import java.net.URL;
import java.util.ResourceBundle;

public class ViewerController implements Initializable {
    private GraphicsContext gc;
    private float shift3d = 1.0f;
    private float shift2d = 5.0f;
    private int originX = 1024/4, originY = 768/2;
    private int xSquareSize = 5, ySquareSize = 5;
    private final int xGridSize = 50, yGridSize = 50;
    private int[][] map = new int[xGridSize][yGridSize];
    private UserFunction userFunction;
    public static String userFunctionString;
    private int viewAngle = 1;
    private final int sens = 50;

    public void setUserFunction() {
        userFunction = new UserFunction(userFunctionString);
    }

    public int getZ(int x, int y) {
        return map[x][y];
    }

    public void setZ(int x, int y, int z) {
        map[x][y] = z;
    }

    public void generateZCoordinates() {
        double magnitude = findMaxValue(userFunction);

        for (int y = yGridSize / 4; y < yGridSize - 1; y++) {
            for (double r = 0; r < Math.PI; r += 0.01) {
                double res = userFunction.apply(y - yGridSize/4.0);

                int x = (int) Math.round((xGridSize * (res * Math.cos(r))/magnitude) + xGridSize / 2.0 - 1.0) ;

                if (x >= 0 && x < xGridSize) {
                    int z = (int) Math.round(((xGridSize *((res * Math.sin(r)))) + xGridSize / 2.0 - 1)/magnitude);
                    setZ(x, y, z);
                }
            }
        }
    }

    public void setElevationColor(int y1, int y2) {
        int yAvg = (y1 + y2) / 2;
        if ((yAvg > -100) && (yAvg < -10)) gc.setStroke(Color.DARKBLUE);
        if ((yAvg > -10) && (yAvg < -2)) gc.setStroke(Color.BLUE);
        if ((yAvg > -2) && (yAvg < 0)) gc.setStroke(Color.AQUAMARINE);
        if ((yAvg > 0) && (yAvg < 10)) gc.setStroke(Color.GREEN);
        if ((yAvg > 10) && (yAvg < 15)) gc.setStroke(Color.GRAY);
        if ((yAvg > 15) && (yAvg < 20)) gc.setStroke(Color.WHITE);
    }


    public double findMaxValue(UserFunction function) {
        double max = Double.MIN_VALUE;
        for (int i = 0; i < yGridSize - 1; i++) {
            double result = function.apply(i);
            if (result > max)
                max = result;
        }

        return max;
    }

    public Point2D transform3D(Point3D point3D) {
        switch (viewAngle) {
            case 0:
                return new Point2D(point3D.x + point3D.z, (int) (((-point3D.y*shift2d) + point3D.z) * shift3d));
            case 1:
                return new Point2D(point3D.x + point3D.z, (int) (((-point3D.y*shift2d) + point3D.z - point3D.x) * shift3d));
            case 2:
                return new Point2D(point3D.x - point3D.z, (int) (((-point3D.y*shift2d) + point3D.z) * shift3d));
            default:
                return new Point2D(1,2);
        }
    }

    public void map3Dto2D() {
        clear();
        drawPart(1);
        drawPart(-1);
    }

    public void drawPart(int direction) {
        int xs = xSquareSize;
        int ys = ySquareSize;

        for (int y = 0; y < yGridSize - 1; y++) {
            for (int x = 0; x < xGridSize - 1; x++) {
                try {
                    if (getZ(x, y) == 0 || getZ(x + 1, y) == 0)
                        continue;
                    Point3D point3d1 = new Point3D(x * xs, (int) (direction * getZ(x, y) * shift3d), y * ys);
                    Point3D point3d2 = new Point3D((x + 1) * xs, (int) (direction * getZ(x + 1, y) * shift3d), y * ys);

                    Point2D point1 = transform3D(point3d1);
                    Point2D point2 = transform3D(point3d2);
                    setElevationColor(point3d1.y, point3d2.y);
                    gc.strokeLine(point1.x + originX, point1.y + originY, point2.x + originX, point2.y + originY);

                    point3d1 = new Point3D(x * xs, (int) (direction * getZ(x, y) * shift3d), y * ys);
                    point3d2 = new Point3D(x * xs, (int) (direction * getZ(x, y + 1) * shift3d), (y + 1) * ys);

                    point1 = transform3D(point3d1);
                    point2 = transform3D(point3d2);
                    setElevationColor(point3d1.y, point3d2.y);
                    gc.strokeLine(point1.x + originX, point1.y + originY, point2.x + originX, point2.y + originY);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }


    public void clear() {
        gc.setFill(Color.BLACK);
        gc.fillRect(0,0, canvas.getWidth(), canvas.getHeight());
    }

    @FXML
    Canvas canvas;

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

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        gc = canvas.getGraphicsContext2D();
        clear();
        setUserFunction();
        generateZCoordinates();
        map3Dto2D();

    }

    public void zoomDown(ActionEvent actionEvent) {
        xSquareSize--;
        ySquareSize--;
        map3Dto2D();
    }

    public void zoomUp(ActionEvent actionEvent) {
        xSquareSize++;
        ySquareSize++;
        map3Dto2D();
    }


    public void goRight(ActionEvent actionEvent) {
        originX += sens;
        map3Dto2D();
    }

    public void goLeft(ActionEvent actionEvent) {
        originX -= sens;
        map3Dto2D();
    }

    public void goUp(ActionEvent actionEvent) {
        originY += sens;
        map3Dto2D();
    }

    public void goDown(ActionEvent actionEvent) {
        originY -= sens;
        map3Dto2D();
    }

    public void increaseShift(ActionEvent actionEvent) {
        shift3d += 0.05f;
        increaseShift3d(null);
        map3Dto2D();
    }

    public void decreaseShift(ActionEvent actionEvent) {
        shift3d -= 0.05f;
        increaseShift3d(null);
        map3Dto2D();
    }

    public void increaseShift3d(ActionEvent actionEvent) {
        shift2d += 0.1f;
        map3Dto2D();
    }

    public void setLeftView(ActionEvent actionEvent) {
        viewAngle = 0;
        map3Dto2D();
    }

    public void setCenterView(ActionEvent actionEvent) {
        viewAngle = 1;
        map3Dto2D();
    }

    public void setRightView(ActionEvent actionEvent) {
        viewAngle = 2;
        map3Dto2D();
    }
}
