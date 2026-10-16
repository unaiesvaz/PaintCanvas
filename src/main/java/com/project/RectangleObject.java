package com.project;

import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class RectangleObject extends DrawingObject { // La clase para dibujar rectangulos

    private double x;
    private double y;
    private double width;
    private double height;

    public RectangleObject(int id,
                           double startX, double startY,
                           double endX, double endY,
                           Color lineColor, Color fillColor,
                           double lineWidth) {

        super(id, lineColor, fillColor, lineWidth);

        updateFromPoints(startX, startY, endX, endY);
    }

    public void updateFromPoints(double startX, double startY,
                                 double endX, double endY) {

        x = Math.min(startX, endX);
        y = Math.min(startY, endY);

        width = Math.abs(endX - startX);
        height = Math.abs(endY - startY);
    }

    @Override
    public String getTypeName() {
        return "Rectangle";
    }

    @Override
    public void draw(GraphicsContext gc) {
        gc.setLineWidth(getLineWidth());

        gc.setFill(getFillColor());
        gc.fillRect(x, y, width, height);

        gc.setStroke(getLineColor());
        gc.strokeRect(x, y, width, height);
    }

    @Override
    public Rectangle2D getBounds() {
        return createBounds(x, y, x + width, y + height);
    }

    @Override
    public void move(double dx, double dy) {
        x += dx;
        y += dy;
    }
}