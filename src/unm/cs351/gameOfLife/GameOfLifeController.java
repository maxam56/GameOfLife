package unm.cs351.gameOfLife;

import java.net.URL;
import java.util.Random;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.SubScene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.StackPane;

/**
 * The main controller for the game, used to process the FXML file objects. This
 * controls how changes in GUI are handled by the game
 * 
 * @author Nicholas Spurlock
 *
 */
public class GameOfLifeController implements Initializable
{
  private Game game;
  private final int MAX_NEIGHBORS = 26;
  Presets currentPreset = null;
  
  @FXML
  private ComboBox<Integer> comboBoxOne;
  @FXML
  private ComboBox<Integer> comboBoxTwo;
  @FXML
  private ComboBox<Integer> comboBoxThree;
  @FXML
  private ComboBox<Integer> comboBoxFour;
  @FXML
  private ComboBox<Integer> gridDimensionComboBox;
  
  @FXML
  private ComboBox<Presets> presetComboBox;

  @FXML
  private ComboBox<Integer> aliveDeadRatioComboBox;
  
  @FXML
  private Button startButton;
  @FXML
  private Button exitButton;
  @FXML
  private Button restartButton;
  @FXML
  private Button rotationControlButton;
  @FXML
  private Button randomeValueButton;

  @FXML
  private StackPane pane;
  @FXML
  private SubScene subScene;

  public enum Presets
  {
    PRESET4("Preset 4")
    {
      public String getPreset() {return "110110000000110110110000000";}
    }, 
    PRESET1("Preset 1")
    {
      public String getPreset() {return "110000110110000110000000000";}
    }, 
    PRESET2("Preset 2")
    {
      public String getPreset() {return "110000110110000110000000000";}
    }, 
    PRESET3("Preset 3")
    {
      public String getPreset() {return "110000110110000110000000000";}
    }, 
    PRESET5("Preset 5")
    {
      public String getPreset() {return null;}
    };

    private final String NAME;

   
    private Presets(String name)
    {
      this.NAME = name;
    }
   
    @Override
    public String toString()
    {
      return this.NAME;
    }
    public abstract String getPreset();
  }

  public void setGame(Game game)
  {
    this.game = game;
  }

  @Override
  public void initialize(URL arg0, ResourceBundle arg1)
  {
    Random rand = new Random();

    ObservableList<Integer> numbers = FXCollections.observableArrayList();
    for (int i = 0; i < MAX_NEIGHBORS; i++)
    {
      numbers.add(i);
    }

    comboBoxOne.getItems().addAll(numbers);
    comboBoxTwo.getItems().addAll(numbers);
    comboBoxThree.getItems().addAll(numbers);
    comboBoxFour.getItems().addAll(numbers);
    
    gridDimensionComboBox.getItems().addAll(numbers);

    comboBoxOne.setValue(rand.nextInt(MAX_NEIGHBORS) + 1);
    comboBoxTwo.setValue(rand.nextInt(MAX_NEIGHBORS) + 1);
    comboBoxThree.setValue(rand.nextInt(MAX_NEIGHBORS) + 1);
    comboBoxFour.setValue(rand.nextInt(MAX_NEIGHBORS) + 1);
    gridDimensionComboBox.setValue(30);

    numbers.clear();
    for (int i = 10; i < 110; i += 10)
    {
      numbers.add(i);
    }
    aliveDeadRatioComboBox.getItems().addAll(numbers);
    aliveDeadRatioComboBox.setValue(50);
    
    presetComboBox.getItems().add(Presets.PRESET1);
    presetComboBox.getItems().add(Presets.PRESET2);
    presetComboBox.getItems().add(Presets.PRESET3);
    presetComboBox.getItems().add(Presets.PRESET4);
    presetComboBox.getItems().add(Presets.PRESET5);
    
    rotationControlButton.setText("Stop Rotation");
  }

  public int[] getRValues()
  {
    int[] values = { comboBoxOne.getValue(), comboBoxTwo.getValue(), comboBoxThree.getValue(), comboBoxFour.getValue() };
    return values;
  }

  public int getMaxDim()
  {
    return gridDimensionComboBox.getValue() + 2;
  }

  public SubScene getSubScene()
  {
    return subScene;
  }

  public StackPane getStackPane()
  {
    return pane;
  }
  
  private void handleGameRestart()
  {
    if (currentPreset != null) game.restart(currentPreset.getPreset());
    else game.restart(null);
    startButton.setText("Play");
  }

  @FXML
  private void handleAliveDeadRatio()
  {
    game.setPause();
    game.setAliveDeadRatio((double)(aliveDeadRatioComboBox.getValue()));
    handleGameRestart();
  }
  
  @FXML
  private void handleRandomValues()
  {
    Random rand = new Random();
    comboBoxOne.setValue(rand.nextInt(MAX_NEIGHBORS) + 1);
    comboBoxTwo.setValue(rand.nextInt(MAX_NEIGHBORS) + 1);
    comboBoxThree.setValue(rand.nextInt(MAX_NEIGHBORS) + 1);
    comboBoxFour.setValue(rand.nextInt(MAX_NEIGHBORS) + 1);
    currentPreset = null;
    handleGameRestart();
  }

  @FXML
  private void handleRotationControl(ActionEvent ev)
  {
    if (game.setRotationTime())
    {
      rotationControlButton.setText("Stop Rotation");
    }
    else
    {
      rotationControlButton.setText("Start Rotation");
    }
  }

  @FXML
  private void handleMousePressed(MouseEvent me)
  {
    game.pauseRotation();
    rotationControlButton.setText("Start Rotation");
  }

  @FXML
  private void handleMouseDrag(MouseEvent event)
  {
    game.setMouseDragPosition(event);
  }

  /**
   * If a preset is selected we need to change the combo box values and set the
   * seed used by the game's number generator
   */
  @FXML
  private void handlePreset()
  {
    Presets preset = null;
    if (presetComboBox.getValue() != null)
    {
      switch (presetComboBox.getValue())
      {
      case PRESET1:
      {
        comboBoxOne.setValue(4);
        comboBoxTwo.setValue(4);
        comboBoxThree.setValue(3);
        comboBoxFour.setValue(4);
        aliveDeadRatioComboBox.setValue(10);
        gridDimensionComboBox.setValue(3);
        game.setSeed(123456789);
        preset = Presets.PRESET1;
        break;
      }

      case PRESET2:
      {
        comboBoxOne.setValue(4);
        comboBoxTwo.setValue(4);
        comboBoxThree.setValue(4);
        comboBoxFour.setValue(3);
        gridDimensionComboBox.setValue(3);
        preset = Presets.PRESET2;
        break;
      }

      case PRESET3:
      {
        comboBoxOne.setValue(4);
        comboBoxTwo.setValue(5);
        comboBoxThree.setValue(3);
        comboBoxFour.setValue(4);
        gridDimensionComboBox.setValue(3);
        preset = Presets.PRESET3;
        break;
      }

      case PRESET4:
      {
        comboBoxOne.setValue(6);
        comboBoxTwo.setValue(6);
        comboBoxThree.setValue(7);
        comboBoxFour.setValue(5);
        gridDimensionComboBox.setValue(3);
        preset = Presets.PRESET4;
        break;
      }
      case PRESET5:
      {
        comboBoxOne.setValue(5);
        comboBoxTwo.setValue(10);
        comboBoxThree.setValue(15);
        comboBoxFour.setValue(8);
        gridDimensionComboBox.setValue(5);
        game.setSeed(123456789);
        preset = Presets.PRESET5;
        break;
      }
      default:
        break;
      }

    }
    currentPreset = preset;
    handleGameRestart();
  }

  @FXML
  private void handleDimenstionChange(ActionEvent event)
  {
    game.setPause();
    handleGameRestart();
      
  }

  @FXML
  private void handleStart(ActionEvent event)
  {
    if (game == null)
    {
      System.out.println("Game is null, unable to set.");
    }
    if (game.setPlayPause())
    {
      startButton.setText("Play");
    }
    else
    {
      startButton.setText("Pause");
    }
  }

  @FXML
  private void handleRestart(ActionEvent ev)
  {
    handleGameRestart();
  }

  @FXML
  private void handleMouseScroll(ScrollEvent se)
  {
    game.handleMouseScroll(se);
  }

  @FXML
  private void handleExit(ActionEvent event)
  {
    Platform.exit();
  }

}
