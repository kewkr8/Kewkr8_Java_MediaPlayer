/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mediaplayer;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Slider;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaView;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 *
 * @author KEW
 */
public class PlayerController extends Switchable implements Initializable {
    
    @FXML
    private AnchorPane vizPane;
    
    @FXML
    private MediaView mediaView;
    
    @FXML
    private Text volumeText;
    
    @FXML
    private Text filePathText;
    
    @FXML
    private Text lengthText;
    
    @FXML
    private Text currentText;
    
    @FXML
    private Text bandsText;
    
    @FXML
    private Text errorText;
   
    @FXML
    private Menu bandsMenu;
    
    @FXML
    private Slider timeSlider;
    
    @FXML
    private Slider volumeSlider;
    
    private Media media;
    private javafx.scene.media.MediaPlayer mediaPlayer;
    private Boolean paused = false;
    private Boolean mp3 = false;
    private double mediaViewHeight;
    private double mediaViewWidth;
    private double volume;
    private Integer volumeDisplay;
    
    
    private Integer numBands = 40;
    private final Double updateInterval = 0.05;
    
    
    private Visualizer currentVisualizer;
    private final Integer[] bandsList = {1, 2, 4, 8, 16, 20, 40, 60, 100, 120, 140};

    private Stage stage;
    
    @Override
    public void initialize(URL location, ResourceBundle rb) {
          bandsText.setText(Integer.toString(numBands));
          currentVisualizer = new Kewkr8Visualizer();
        
       for (Integer bands : bandsList) {
            MenuItem menuItem1 = new MenuItem(Integer.toString(bands));
            menuItem1.setUserData(bands);
            menuItem1.setOnAction((ActionEvent event) -> {
                selectBands(event);
            });
            bandsMenu.getItems().add(menuItem1);
        }
    }
    
    @Override
    public void setStage(Stage stage) {
        this.stage = stage;
    }
    
    @FXML
    private void selectVisualizer(ActionEvent event) {
        MenuItem menuItem = (MenuItem)event.getSource();
        
        if(menuItem.getText().equals("Off")){
            currentVisualizer.end();
            currentVisualizer = null;
           
        }else{
            currentVisualizer = new Kewkr8Visualizer();
            currentVisualizer.start(numBands, vizPane);
        }
        
    }
    
    private void selectBands(ActionEvent event) {
        MenuItem menuItem = (MenuItem)event.getSource();
        numBands = (Integer)menuItem.getUserData();
        if (currentVisualizer != null) {
            currentVisualizer.start(numBands, vizPane);
        }
        if (mediaPlayer != null) {
            mediaPlayer.setAudioSpectrumNumBands(numBands);
        }
        bandsText.setText(Integer.toString(numBands));
    }
    
   
    
    private void openMedia(File file) {
        filePathText.setText("");
        errorText.setText("");
        
        if (mediaPlayer != null) {
            mediaPlayer.dispose();
        }
        
        try {
            if(file.toURI().getPath().endsWith(".mp4")|| file.toURI().getPath().endsWith(".mp3") || file.toURI().getPath().endsWith(".wav")) {
                media = new Media(file.toURI().toString());
                mediaPlayer = new javafx.scene.media.MediaPlayer(media);
                mediaView.setMediaPlayer(mediaPlayer);
            
            mediaPlayer.setOnReady(() -> {
                handleReady();
            });
            mediaPlayer.setOnEndOfMedia(() -> {
                handleEndOfMedia();
            });
            mediaPlayer.setAudioSpectrumNumBands(numBands);
            mediaPlayer.setAudioSpectrumInterval(updateInterval);
            mediaPlayer.setAudioSpectrumListener((double timestamp, double duration, float[] magnitudes, float[] phases) -> {
                handleUpdate(timestamp, duration, magnitudes, phases);
            });
            mediaPlayer.setAutoPlay(true);
            filePathText.setText(file.getPath());
            volume = mediaPlayer.getVolume();
            volumeDisplay = 10;   
            volumeText.setText(volumeDisplay.toString());
            
                if(file.toURI().getPath().endsWith(".mp3")) {
                    vizPane.relocate(230, 90);
                    vizPane.setMinSize(750, 600);
                    mp3 = true;
                
                }
                if(!file.toURI().getPath().endsWith(".mp3")) {
                    vizPane.relocate(225, 500);
                    vizPane.setMinSize(750, 200);
                    mp3 = false;
                }
            
            } else {
                filePathText.setText("Error unsupported file!");
                Alert alert = new Alert(AlertType.ERROR,"Media not supported. Please pick a different format");
                alert.setTitle("Unsupported Format");
                alert.showAndWait();
                return;
            }
            
        } catch (Exception ex) {
            errorText.setText(ex.toString());
        }
    }
    
    private void handleReady() {
        Duration duration = mediaPlayer.getTotalDuration();
        lengthText.setText(String.valueOf(duration.toSeconds())+"sec");
        Duration ct = mediaPlayer.getCurrentTime();
        currentText.setText(String.valueOf(ct.toSeconds()));
        currentVisualizer.start(numBands, vizPane);
        timeSlider.setMin(0);
        timeSlider.setMax(duration.toMillis());
        
        this.stage.addEventFilter(KeyEvent.KEY_PRESSED, (KeyEvent event) -> {
            
            if ("SHIFT".equals(event.getCode().toString())) {
                
                handlePausePress();          
            
            } else if ("ENTER".equals(event.getCode().toString())) {
                
                handleFullScreenPress();
                
            } else if ("RIGHT".equals(event.getCode().toString())) {
               
                handleForwardPress();
          
            } else if ("ESCAPE".equals(event.getCode().toString())){
               
                handleExitFullScreen();
            }
              else if ("UP".equals(event.getCode().toString())){
                  
                 handleVolumeUpPress();
              }
             else if ("DOWN".equals(event.getCode().toString())){
                  
                 handleVolumeDownPress();
                  
             }
            
        });
    }
    private void handleVolumeDownPress() {
        
         if(volume > 0.0){
                 
             volume -= 0.1;
             mediaPlayer.setVolume(volume);
             Duration f = new Duration(volume);
             double vol = f.toMillis();
             volumeDisplay--;
             volumeText.setText(volumeDisplay.toString());
             
             if(volumeDisplay < 0.0) {
                 
                volumeText.setText("Mute");
                
            } else {
                 
                volumeSlider.setValue(vol);
             }
         }
                 
    }
    private void handleVolumeUpPress() {
        
         if(volume < 1.0) {
             
             volume += 0.1;
             mediaPlayer.setVolume(volume);
             Duration f = new Duration(volume);
             double vol = f.toMillis();
             volumeDisplay++;
             volumeText.setText(volumeDisplay.toString());
             volumeSlider.setValue(vol);
          }
        
    }
    private void handleExitFullScreen() {
        
        mediaView.setFitHeight(mediaViewHeight);
        mediaView.setFitWidth(mediaViewWidth);
        mediaView.relocate(174, 90);
        
    }
    private void handleForwardPress() {
        
         mediaPlayer.pause();
         Duration f = mediaPlayer.getCurrentTime();
         f.add(new Duration(2.0));
         mediaPlayer.seek(f);
         mediaPlayer.play();
        
    }
    private void handleFullScreenPress() {
        
        if(mp3 == false) {
            mediaViewHeight = mediaView.getFitHeight();
            mediaViewWidth = mediaView.getFitWidth();
            mediaView.relocate(0, 0);
            mediaView.setFitHeight(800);
            mediaView.setFitWidth(1280);
        }
        stage.setFullScreen(true);
    }
    
    private void handlePausePress() {
       
        if (mediaPlayer == null) return;
        
        if (paused == true) {
            mediaPlayer.play();
            paused = false;
        } else {
            mediaPlayer.pause();
            paused = true;
        }       
    }
                
    
    private void handleEndOfMedia() {
        mediaPlayer.stop();
        mediaPlayer.seek(Duration.ZERO);
        timeSlider.setValue(0);
    }
    
    @FXML
    private void handleAbout(Event event) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Kyle's Media Player");
        alert.setHeaderText("About the Developer");
        alert.setContentText("Kyle Whitney is a dual major student at the University of Missouri.\n He is majoring in Computer Science and Information Technology");
        alert.setGraphic(null);
        alert.showAndWait();
    }
    
    private void handleUpdate(double timestamp, double duration, float[] magnitudes, float[] phases) {
        Duration ct = mediaPlayer.getCurrentTime();
        double s = ct.toMillis();
        timeSlider.setValue(s);
        currentText.setText(String.format("%.1f sec", ct.toSeconds()));
        
     if(currentVisualizer != null)
        currentVisualizer.update(timestamp, duration, magnitudes, phases);
    }
   
    
    @FXML
    private void handleOpen(Event event) {
        Stage primaryStage = (Stage)vizPane.getScene().getWindow();
        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showOpenDialog(primaryStage);
        if (file != null) {
            openMedia(file);
        }
    }
    
    @FXML
    private void handlePlay(ActionEvent event) {
        if (mediaPlayer != null) {
            mediaPlayer.play();
        }
        
    }
   
    @FXML
    private void handlePause(ActionEvent event) {
        if (mediaPlayer != null) {
           mediaPlayer.pause(); 
        }
    }
    
    @FXML
    private void handleStop(ActionEvent event) {
        if (mediaPlayer != null) {
           mediaPlayer.stop(); 
        }
        
    }
    
    @FXML
    private void handleSliderMousePressed(Event event) {
        if (mediaPlayer != null) {
           mediaPlayer.pause(); 
        }  
    }
    
    @FXML
    private void handleSliderMouseReleased(Event event) {
        if (mediaPlayer != null) {
            mediaPlayer.seek(new Duration(timeSlider.getValue()));
            System.out.println(timeSlider.getValue());
            currentVisualizer.start(numBands, vizPane);
            mediaPlayer.play();
        }  
    }
}