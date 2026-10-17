package com.project;

import java.util.function.Consumer;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.Stop;

// Predefined colors drawn as circles on their own Canvas
public class ColorPalette {

    private static final Color[] COLORS = {
        Color.BLACK,
        Color.web("#9B1C1C"),
        Color.web("#A85D00"),
        Color.YELLOW,
        Color.LIMEGREEN,
        Color.GREEN,
        Color.TEAL,
        Color.CYAN,
        Color.BLUE,
        Color.INDIGO,
        Color.MAGENTA,
        Color.DEEPPINK
    };


    private final Canvas canvas;
    private final GraphicsContext gc;
    private final Consumer<Color> onColorSelected;

    private Color selectedColor = COLORS[0];

    public ColorPalette(Canvas canvas) {
        this(canvas, null);
    }

    public ColorPalette(Canvas canvas, Consumer<Color> onColorSelected) {
        this.canvas = canvas;
        this.gc = canvas.getGraphicsContext2D();
        this.onColorSelected = onColorSelected;

        canvas.setOnMouseClicked(this::onMouseClicked);

        draw();
    }

    public Color getSelectedColor() {
        return selectedColor;
    }

    public void setSelectedColor(Color color) {
        selectedColor = null;

        for (Color paletteColor : COLORS) {
            if (paletteColor.equals(color)) {
                selectedColor = paletteColor;
                break;
            }
        }

        draw();
    }

    private void onMouseClicked(MouseEvent event) {

        double slotWidth = canvas.getWidth() / COLORS.length;

        int index = (int) (event.getX() / slotWidth);

        if (index < 0 || index >= COLORS.length) {
            return;
        }

        selectedColor = COLORS[index];

        if (onColorSelected != null) {
            onColorSelected.accept(selectedColor);
        }

        draw();
    }

    private void draw() {
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        double slotWidth = canvas.getWidth() / COLORS.length;

        for (int i = 0; i < COLORS.length; i++) {
            double pencilWidth = slotWidth * 0.78;
            double x = i * slotWidth + (slotWidth - pencilWidth) / 2;

            boolean selected = COLORS[i].equals(selectedColor);

            double topY = selected ? 0 : 7;
            double bottomY = canvas.getHeight() - 1 - (selected ? 7 : 0);

            double tipHeight = Math.min(12,(bottomY - topY) * 0.22);
            
            double centerX = x + pencilWidth / 2;
            double bodyY = topY + tipHeight;
            double bodyHeight = bottomY - bodyY;


            gc.setFill(Color.web("#C8A878"));
            double[] woodX = {
                centerX,
                x,
                x + pencilWidth
            };

            double[] woodY = {
                topY,
                bodyY,
                bodyY
            };

            gc.fillPolygon(woodX, woodY, 3);

            gc.setFill(Color.DARKSLATEGRAY);

            double[] leadX = {
                centerX,
                centerX - pencilWidth * 0.2,
                centerX + pencilWidth * 0.2
            };

            double[] leadY = {
                topY,
                topY + tipHeight * 0.48,
                topY + tipHeight * 0.48
            };

            gc.fillPolygon(leadX, leadY, 3);

            Color color = COLORS[i];

            LinearGradient gradient = new LinearGradient(
                0, 0, 1, 0,
                true,
                CycleMethod.NO_CYCLE,
                new Stop(0, color.darker()),
                new Stop(0.35, color.brighter()),
                new Stop(0.7, color),
                new Stop(1, color.darker())
            );

            gc.setFill(gradient);

            gc.fillRect(
                x,
                bodyY,
                pencilWidth,
                bodyHeight
            );

            gc.setStroke(Color.GRAY);
            gc.setLineWidth(0.7);

            gc.strokeRect(
                x,
                bodyY,
                pencilWidth,
                bodyHeight
            );

            gc.setStroke(Color.color(1, 1, 1, 0.55));
            gc.setLineWidth(0.6);

            gc.strokeLine(
                x + pencilWidth * 0.3,
                bodyY + 2,
                x + pencilWidth * 0.3,
                bottomY - 2
            );
        }
    }
}
