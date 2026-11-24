package dev.leg1tka.utils;
import dev.leg1tka.animation.*;
import dev.leg1tka.clickgui.*;
import dev.leg1tka.core.*;
import dev.leg1tka.core.Module;
import dev.leg1tka.discord.*;
import dev.leg1tka.hud.*;
import dev.leg1tka.protection.*;
import dev.leg1tka.render.*;
import dev.leg1tka.rotation.*;
import dev.leg1tka.settings.*;
import dev.leg1tka.storage.*;


import java.util.Random;

public class NeuralNetwork {
   private final int inputSize;
   private final int hiddenSize;
   private final int outputSize;
   private double[][] weightsInputHidden;
   private double[][] weightsHiddenOutput;
   private double[] biasHidden;
   private double[] biasOutput;
   private double learningRate = 0.01;
   private double[][] momentumInputHidden;
   private double[][] momentumHiddenOutput;
   private double momentumFactor = 0.9;
   private final Random random = new Random();

   public NeuralNetwork(
      int n,
      int n2,
      int n3
   ) {
      this.inputSize = n;
      this.hiddenSize = n2;
      this.outputSize = n3;
      this.initializeWeights();
   }

   private void initializeWeights() {
      this.weightsInputHidden = new double[this.inputSize][this.hiddenSize];
      this.weightsHiddenOutput = new double[this.hiddenSize][this.outputSize];
      this.biasHidden = new double[this.hiddenSize];
      this.biasOutput = new double[this.outputSize];
      this.momentumInputHidden = new double[this.inputSize][this.hiddenSize];
      this.momentumHiddenOutput = new double[this.hiddenSize][this.outputSize];
      double x = Math.sqrt(
         6.0 / (double)(this.inputSize + this.hiddenSize)
      );
      double x2 = Math.sqrt(
         6.0 / (double)(this.hiddenSize + this.outputSize)
      );

      for (int i = 0;
         i
            < this.inputSize;
         i++
      ) {
         for (int i2 = 0;
            i2
               < this.hiddenSize;
            i2++
         ) {
            this.weightsInputHidden[i][i2] = this.random
                     .nextDouble()
                  * 2.0
                  * x
               - x;
         }
      }

      for (int i3 = 0;
         i3
            < this.hiddenSize;
         i3++
      ) {
         for (int i4 = 0;
            i4
               < this.outputSize;
            i4++
         ) {
            this.weightsHiddenOutput[i3][i4] = this.random
                     .nextDouble()
                  * 2.0
                  * x2
               - x2;
         }
      }
   }

   public double[] forward(
      double[] val
   ) {
      if (val.length
         != this.inputSize) {
         throw new IllegalArgumentException("Input size mismatch");
      } else {
         double[] val2 = new double[this.hiddenSize];

         for (int i = 0;
            i
               < this.hiddenSize;
            i++
         ) {
            double x = this.biasHidden[i];

            for (int i2 = 0;
               i2
                  < this.inputSize;
               i2++
            ) {
               x += val[i2]
                  * this.weightsInputHidden[i2][i];
            }

            val2[i] = this.relu(
               x
            );
         }

         double[] val3 = new double[this.outputSize];

         for (int i3 = 0;
            i3
               < this.outputSize;
            i3++
         ) {
            double x2 = this.biasOutput[i3];

            for (int i4 = 0;
               i4
                  < this.hiddenSize;
               i4++
            ) {
               x2 += val2[i4]
                  * this.weightsHiddenOutput[i4][i3];
            }

            val3[i3] = this.tanh(
               x2
            );
         }

         return val3;
      }
   }

   public void train(
      double[] val,
      double[] val2,
      double d
   ) {
      if (val.length
            == this.inputSize
         && val2.length
            == this.outputSize) {
         double[] val3 = new double[this.hiddenSize];

         for (int i = 0;
            i
               < this.hiddenSize;
            i++
         ) {
            double x = this.biasHidden[i];

            for (int i2 = 0;
               i2
                  < this.inputSize;
               i2++
            ) {
               x += val[i2]
                  * this.weightsInputHidden[i2][i];
            }

            val3[i] = this.relu(
               x
            );
         }

         double[] val4 = new double[this.outputSize];

         for (int i3 = 0;
            i3
               < this.outputSize;
            i3++
         ) {
            double x2 = this.biasOutput[i3];

            for (int i4 = 0;
               i4
                  < this.hiddenSize;
               i4++
            ) {
               x2 += val3[i4]
                  * this.weightsHiddenOutput[i4][i3];
            }

            val4[i3] = this.tanh(
               x2
            );
         }

         double[] val5 = new double[this.outputSize];

         for (int i5 = 0;
            i5
               < this.outputSize;
            i5++
         ) {
            val5[i5] = (
                  val2[i5]
                     - val4[i5]
               )
               * this.tanhDerivative(
                  val4[i5]
               )
               * d;
         }

         double[] val6 = new double[this.hiddenSize];

         for (int i6 = 0;
            i6
               < this.hiddenSize;
            i6++
         ) {
            double x3 = 0.0;

            for (int i7 = 0;
               i7
                  < this.outputSize;
               i7++
            ) {
               x3 += val5[i7]
                  * this.weightsHiddenOutput[i6][i7];
            }

            val6[i6] = x3
               * this.reluDerivative(
                  val3[i6]
               );
         }

         for (int i8 = 0;
            i8
               < this.hiddenSize;
            i8++
         ) {
            for (int i9 = 0;
               i9
                  < this.outputSize;
               i9++
            ) {
               double x4 = this.learningRate
                  * val5[i9]
                  * val3[i8];
               this.momentumHiddenOutput[i8][i9] = this.momentumFactor
                     * this.momentumHiddenOutput[i8][i9]
                  + x4;
               this.weightsHiddenOutput[i8][i9] = this.weightsHiddenOutput[i8][i9]
                  + this.momentumHiddenOutput[i8][i9];
            }
         }

         for (int i10 = 0;
            i10
               < this.inputSize;
            i10++
         ) {
            for (int i11 = 0;
               i11
                  < this.hiddenSize;
               i11++
            ) {
               double x5 = this.learningRate
                  * val6[i11]
                  * val[i10];
               this.momentumInputHidden[i10][i11] = this.momentumFactor
                     * this.momentumInputHidden[i10][i11]
                  + x5;
               this.weightsInputHidden[i10][i11] = this.weightsInputHidden[i10][i11]
                  + this.momentumInputHidden[i10][i11];
            }
         }

         for (int i12 = 0;
            i12
               < this.outputSize;
            i12++
         ) {
            this.biasOutput[i12] = this.biasOutput[i12]
               + this.learningRate
                  * val5[i12];
         }

         for (int i13 = 0;
            i13
               < this.hiddenSize;
            i13++
         ) {
            this.biasHidden[i13] = this.biasHidden[i13]
               + this.learningRate
                  * val6[i13];
         }
      } else {
         throw new IllegalArgumentException("Size mismatch");
      }
   }

   private double relu(
      double d
   ) {
      return Math.max(
         0.0,
         d
      );
   }

   private double reluDerivative(
      double d
   ) {
      return d
            > 0.0
         ? 1.0
         : 0.0;
   }

   private double tanh(
      double d
   ) {
      return Math.tanh(
         d
      );
   }

   private double tanhDerivative(
      double d
   ) {
      return 1.0
         - d
            * d;
   }

   public void setLearningRate(
      double d
   ) {
      this.learningRate = d;
   }

   public double getLearningRate() {
      return this.learningRate;
   }
}
