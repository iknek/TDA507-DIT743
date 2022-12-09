package task5;

import javax.swing.*;
import java.awt.*;

public class MatrixVisualizer2000 extends JFrame {

    private double[][] matrix;
    private JLabel rowLabel;
    private JLabel colLabel;
    GridBagConstraints gbc;

    /**
     * Simple Java Swing class to draw out the transitional matrix, mapping each number to a colour, to create a heatmap.
     */
    public MatrixVisualizer2000(double[][] mat){
        setTitle("Matrix Visualizer");
        matrix = mat;
        setSize(1000, 1000);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        // Create labels for the rows and columns.
        String[] rLabel = "X axis".split("");
        String[] cLabel = "Y axis".split("");

        rowLabel = new JLabel();
        rowLabel.setText("<html>" + String.join("<br>", rLabel) + "</html>");
        colLabel = new JLabel();
        colLabel.setText("<html>" + String.join("<br>", cLabel) + "</html>");

        add(rowLabel, gbc);
        add(colLabel, gbc);

        // Add the labels to the panel using GridBagLayout.
        gbc = new GridBagConstraints();
        gbc.gridheight = 1000;
        gbc.gridwidth = 1000;
        gbc.gridx = 1000;
        gbc.gridy = 1000;
        gbc.fill = GridBagConstraints.BOTH;
    }

    /** Gives the color for a heatmap value in the range 0 to 1. I.e, maps a number to a colour. */
    private Color getHeatmapColor(double value) {
        float hue = (float)(1.0 - value) * 280 / 360;
        return Color.getHSBColor(hue, 1.0f, 0.6f);
    }

    /** Draws each element in the matrix */
    @Override
    public void paint(Graphics g) {
        super.paint(g);
        int x;
        int y = 0;

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
