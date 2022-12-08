package task5;

import javax.swing.*;
import java.awt.*;

public class MatrixVisualizer2000 extends JFrame {

    private double[][] matrix;

    public MatrixVisualizer2000(double[][] mat){
        setTitle("Matrix Visualizer");
        matrix = mat;
        setSize(1000, 1000);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    // get the color for a heatmap value in the range [0, 1]
    private Color getHeatmapColor(double value) {
        float hue = (float)(1.0 - value) * 280 / 360;
        return Color.getHSBColor(hue, 1.0f, 0.6f);
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        int x;
        int y = 0;

        // Draws each element in the matrix
        for (double[] doubles : matrix) {
            x = 0;
            for (double aDouble : doubles) {
                g.setColor(getHeatmapColor(aDouble));
                x += 10;
                g.fillRect(x, y, 10, 10);
            }
            y += 10;
        }
    }
}
