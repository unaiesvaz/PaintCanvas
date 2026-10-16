package com.project;

import java.util.ArrayList;
import java.util.List;

import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

// A freehand stroke: an ordered list of points with its own style
public class Stroke extends DrawingObject {

    private final List<Point2D> points = new ArrayList<>();

    public Stroke(int id, javafx.scene.paint.Color lineColor, double lineWidth) {
        super(id, lineColor, javafx.scene.paint.Color.TRANSPARENT, lineWidth);
    }

    public void addPoint(double x, double y) {
        points.add(new Point2D(x, y));
    }

    @Override
    public String getTypeName() {
        return "Traç lliure";
    }

    public void draw(GraphicsContext gc) {
        if (points.isEmpty()) {
            return;
        }

        gc.setStroke(getLineColor());
        gc.setLineWidth(getLineWidth());

        // Join each point with the next one
        gc.beginPath();
        gc.moveTo(points.get(0).getX(), points.get(0).getY());

        for (Point2D p : points) {
            gc.lineTo(p.getX(), p.getY());
        }
        gc.stroke();
    }

    @Override
    public Rectangle2D getBounds() { // Calcula los limites para pintar 
        if (points.isEmpty()) {
            return new Rectangle2D(0, 0, 0, 0);
        }

        double minX = points.get(0).getX();
        double minY = points.get(0).getY();
        double maxX = minX;
        double maxY = minY;

        for (Point2D point : points) {
            minX = Math.min(minX, point.getX());
            minY = Math.min(minY, point.getY());

            maxX = Math.max(maxX, point.getX());
            maxY = Math.max(maxY, point.getY());
        }

        return createBounds(minX, minY, maxX, maxY);
    }

    @Override
    public void move(double dx, double dy) { //Desplaza el trazo al pintar 
        for (int i = 0; i < points.size(); i++) {
            Point2D point = points.get(i);

            points.set(i, new Point2D(point.getX() + dx,point.getY() + dy));
        }
    }
}   
