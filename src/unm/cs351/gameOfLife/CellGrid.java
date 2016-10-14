package unm.cs351.gameOfLife;

import java.util.Random;

/**
 * The CellGrid manages the generation of the grid of cells and its update each
 * frame
 * 
 * @author Nicholas Spurlock
 *
 */

public class CellGrid
{
  private final int MAX_DIMENSIONS;
  private final Cell[][][] cells;
  private final int r1, r2, r3, r4;
  private final Random rand;

  private final double aliveDeadRatio;
  private double shiftAmount;
  int[][][] newState;
  int[][][] currentState;

  public CellGrid(int[] rValues, int maxDim, int seed, double aliveDeadRatio)
  {
    MAX_DIMENSIONS = maxDim;
    newState = new int[MAX_DIMENSIONS][MAX_DIMENSIONS][MAX_DIMENSIONS];
    currentState = new int[MAX_DIMENSIONS][MAX_DIMENSIONS][MAX_DIMENSIONS];
    cells = new Cell[MAX_DIMENSIONS][MAX_DIMENSIONS][MAX_DIMENSIONS];

    if (seed > 0)
    {
      this.rand = new Random(seed);
    }
    else
    {
      this.rand = new Random();
    }

    this.aliveDeadRatio = aliveDeadRatio / 100;
    shiftAmount = 0;

    initArrays();

    r1 = rValues[0];
    r2 = rValues[1];
    r3 = rValues[2];
    r4 = rValues[3];
  }

  public int getMaxDimensions()
  {
    return MAX_DIMENSIONS;
  }

  public double getShiftAmount()
  {
    return shiftAmount;
  }

  public Cell[][][] getCellGrid()
  {
    return cells;
  }

  /**
   * The main update method for the grid of cells. For each cell it retrieves
   * the number of live neighbors, and based on that number and the initial
   * values the value of the current cell is determined. In the end the current
   * and new states of the board are swapped.
   */
  public void update()
  {
    int neighbors = 0;
    for (int i = 1; i < MAX_DIMENSIONS - 1; i++)
    {
      for (int j = 1; j < MAX_DIMENSIONS - 1; j++)
      {
        for (int k = 1; k < MAX_DIMENSIONS - 1; k++)
        {
          Cell c = cells[i][j][k];
          neighbors = getNeighbors(k, j, i);
          if (neighbors >= r1 && neighbors <= r2)
          {
            newState[i][j][k] = 1;
            c.setAlive(true);

          }
          if (neighbors > r3 || neighbors < r4)
          {
            newState[i][j][k] = 0;
            c.setAlive(false);
          }

          if (newState[i][j][k] != currentState[i][j][k])
          {
            c.setStateChanged(true);
          }
          else
          {
            c.setStateChanged(false);
          }
        }
      }
    }

    int[][][] tmp = newState;
    newState = currentState;
    currentState = tmp;
  }

  private int getNeighbors(int x, int y, int z)
  {
    int count = 0;

    for (int i = z - 1; i < z + 2; i++)
    {
      for (int j = y - 1; j < y + 2; j++)
      {
        for (int k = x - 1; k < x + 2; k++)
        {
          if (currentState[i][j][k] == 1 && (k != x && j != y && i != z))
          {
            count++;
          }
        }
      }
    }
    return count;
  }

  private void initArrays()
  {
    double maxRad = Cell.getMaxDimension();

    double shiftX = 0;
    double shiftY = 0;
    double shiftZ = 0;

    double x, y, z;
    x = y = z = 0;

    // initialize the oldState to 0's and 1's
    for (int i = 1; i < MAX_DIMENSIONS - 1; i++)
    {
      for (int j = 1; j < MAX_DIMENSIONS - 1; j++)
      {
        for (int k = 1; k < MAX_DIMENSIONS - 1; k++)
        {

          currentState[i][j][k] = (rand.nextFloat() < aliveDeadRatio) ? 1 : 0;
          newState[i][j][k] = 0;
          cells[i][j][k] = new Cell(x + shiftX, y + shiftY, z + shiftZ,
              (currentState[i][j][k] == 1 ? true : false)); // Addes a new cell
                                                            // to the grid and
                                                            // shifts it for
                                                            // spacing
          x += maxRad;
          shiftX += shiftAmount;
        }
        x = 0;
        shiftX = 0;
        shiftY += shiftAmount;
        y += maxRad;
      }
      x = 0;
      y = 0;
      shiftX = 0;
      shiftY = 0;
      shiftZ += shiftAmount;
      z += maxRad;
    }
  }
}
