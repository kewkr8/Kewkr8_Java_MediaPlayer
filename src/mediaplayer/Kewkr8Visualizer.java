/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mediaplayer;

import static java.lang.Integer.min;
import java.util.Random;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.*;


/**
 *
 * @author KEW
 */
public class Kewkr8Visualizer implements Visualizer {
    private final String name = "Kewkr8Visualizer";
    private Integer numBands;
    private AnchorPane pane;
    private String initalPaneSize = "";
    
    private final Double bandHeightPercent = 1.3;
    private final Double minEllipseRadius = 10.0; 
    private final Double rotatePhaseMultiplier = 300.0;
    
    private Double width = 0.0;
    private Double height = 0.0;
    
    private int count = 0;
    private int selectShape = 0;
    
    private Double bandWidth = 0.0;
    private Double bandHeight = 0.0;
    private Double halfBandHeight = 0.0;
    private Boolean change = false;
    private final Double startHue = 260.0;
    
    private Cylinder[] cylinders;
    private Box[] boxes;
    private Sphere[] spheres;

    @Override
    public void start(Integer numBands, AnchorPane vizPane) {
        end();
        initalPaneSize = vizPane.getStyle();
        this.numBands = numBands;
        this.pane = vizPane;
        
        height = vizPane.getHeight();
        width = vizPane.getWidth();
        
        //Rectangle clip = new Rectangle(width, height);
        //clip.setLayoutX(0);
        //clip.setLayoutY(0);
        
        /*clip.onMouseClickedProperty().set((EventHandler<MouseEvent>) (MouseEvent event) -> {
            Random num = new Random();
            selectShape = num.nextInt(3);
        });*/
           vizPane.setClip(null);
        
        bandWidth = width / numBands;
        bandHeight = height * bandHeightPercent;
        halfBandHeight = bandHeight / 2;
        
        spheres = new Sphere[numBands];
        boxes = new Box[numBands];
        cylinders = new Cylinder[numBands];
        
        for (int i = 0; i < numBands; i++) {
            Box box = new Box();
            Sphere sphere = new Sphere();
            Cylinder cylinder = new Cylinder();
            
            cylinder.setLayoutX(bandWidth / 2 + bandWidth * i);
            cylinder.setLayoutY((height/2));
            cylinder.setRadius(bandWidth / 2);
            cylinder.setHeight(bandWidth/2);
            cylinder.setMaterial(new PhongMaterial(Color.hsb(startHue, 1.0, 1.0, 1.0)));
            cylinder.setDrawMode(DrawMode.LINE);
            
            sphere.setLayoutX(bandWidth / 2 + bandWidth * i);
            sphere.setLayoutY((height / 2));
            sphere.setRadius(bandWidth / 2);
            sphere.setMaterial(new PhongMaterial(Color.hsb(startHue, 1.0, 1.0, 1.0)));
            sphere.setDrawMode(DrawMode.LINE);
            
            box.setLayoutX(bandWidth/2+bandWidth *i);
            box.setLayoutY((height / 2));
            box.setWidth(bandWidth/2);
            box.setHeight(bandWidth/2);
            box.setMaterial(new PhongMaterial(Color.hsb(startHue, 1.0, 1.0, 1.0)));
            box.setDrawMode(DrawMode.LINE);
            
            switch (selectShape) {
                case 0:
                  vizPane.getChildren().add(box);  
                    break;
                case 1:
                    vizPane.getChildren().add(sphere);
                    break;
                case 2:
                    vizPane.getChildren().add(cylinder);
                    break;
                default:
                    vizPane.getChildren().add(box);
                    break;
            }
            
            spheres[i] = sphere;
            boxes[i] = box;
            cylinders[i] = cylinder;
        }
    }
     public void changeShape(){
        if (spheres != null)
             for(Sphere s : spheres){
                 pane.getChildren().remove(s);
             }
        if (boxes != null)
             for(Box b : boxes){
                 pane.getChildren().remove(b);
             }
        if (cylinders != null)
             for(Cylinder c : cylinders){
                 pane.getChildren().remove(c);
             }
        pane.setClip(null);
        pane.setStyle(initalPaneSize);
        
         switch (selectShape) {
             case 0:
                 for (int i = 0; i < numBands; i++){
                    pane.getChildren().add(boxes[i]);
                }
                change = false;
            break;
                
             case 1:
                 for (int i = 0; i < numBands; i++){
                    pane.getChildren().add(spheres[i]);
                 }
                 change = false;
            break;
            
             case 2:
                 for (int i = 0; i < numBands; i++){
                    pane.getChildren().add(cylinders[i]);
                }
                change = false;
            break;
             default:
                 for (int i = 0; i < numBands; i++){
                    pane.getChildren().add(boxes[i]);
                 }
                change = false;
            break;
        }  
     }
        
    
    @Override
    public void end() {
        if(spheres != null){
             for(Sphere sphere : spheres){
                 pane.getChildren().remove(sphere);
             }
        if (boxes != null)
            for(Box box : boxes){
                pane.getChildren().remove(box);
             }
        if (cylinders != null)
            for(Cylinder c : cylinders){
                pane.getChildren().remove(c);
             }
                cylinders = null;
                boxes = null;
                spheres = null;
                pane.setClip(null);
                pane.setStyle(initalPaneSize);
         }
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void update(double timestamp, double duration, float[] magnitudes, float[] phases) {
        
        if(spheres == null && boxes == null && cylinders == null){
            return;
        }
        
        if(count == 100){
            Random num = new Random();
            change = true;
            selectShape = num.nextInt(3);
            count = 0;
        }
        Integer num;
        switch (selectShape) {
            case 0:
                changeShape();
                num = min(boxes.length, magnitudes.length);
                
                for (int i = 0; i < num; i++) {
                    boxes[i].setHeight(((60.0 + magnitudes[i])/60.0) * halfBandHeight + minEllipseRadius);
                    boxes[i].setWidth(((60.0 + magnitudes[i])/60.0) * halfBandHeight + minEllipseRadius);
                    boxes[i].setMaterial(new PhongMaterial(Color.hsb(startHue - (magnitudes[i] * -6.0), 1.0, 1.0, 1.0)));
                    boxes[i].setRotate(phases[i] * rotatePhaseMultiplier);
                }
            break;
            
            case 1:
                changeShape();
                num = min(spheres.length, magnitudes.length);
            
                for (int i = 0; i < num; i++) {
                    spheres[i].setRadius( ((60.0 + magnitudes[i])/60.0) * halfBandHeight + minEllipseRadius);
                    spheres[i].setMaterial(new PhongMaterial(Color.hsb(startHue  - (magnitudes[i] * -6.0), 1.0, 1.0, 1.0)));
                    spheres[i].setRotate(phases[i] * rotatePhaseMultiplier);
                }
            break;
            
            case 2:
                changeShape();
                num = min(cylinders.length, magnitudes.length);
            
                for (int i = 0; i < num; i++) {
                    cylinders[i].setRadius( ((60.0 + magnitudes[i])/60.0) * halfBandHeight + minEllipseRadius);
                    cylinders[i].setMaterial(new PhongMaterial(Color.hsb(startHue  - (magnitudes[i] * -6.0), 1.0, 1.0, 1.0)));
                    cylinders[i].setRotate(phases[i] * rotatePhaseMultiplier);
                }
            break;
            
            default:
                changeShape();
                num = min(boxes.length, magnitudes.length);
                
                for (int i = 0; i < num; i++) {
                    boxes[i].setHeight(((60.0 + magnitudes[i])/60.0) * halfBandHeight + minEllipseRadius);
                    boxes[i].setWidth(((60.0 + magnitudes[i])/60.0) * halfBandHeight + minEllipseRadius);
                    boxes[i].setMaterial(new PhongMaterial(Color.hsb(startHue - (magnitudes[i] * -6.0), 1.0, 1.0, 1.0)));
                    boxes[i].setRotate(phases[i] * rotatePhaseMultiplier);
                }
           break;
        }
        
        count++;
        
    }
    
}
