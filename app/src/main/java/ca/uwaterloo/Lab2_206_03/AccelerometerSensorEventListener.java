package ca.uwaterloo.Lab2_206_03;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

import ca.uwaterloo.sensortoy.LineGraphView;

public class AccelerometerSensorEventListener implements SensorEventListener {
        TextView magnitudeView, xView, yView, zView;
        float xValue, yValue, zValue, maxX = 0, maxY = 0, maxZ = 0, currentMagnitude, prevMagnitude, currentDerivative, prevDerivative, magnitudeOfStep;
        Long prevTimestamp, lastStepTimestamp;
        int steps = 0, secondsElapsedSinceLastStep = 0;
        float[] values = new float[3];
        LineGraphView accelerometerGraph;
        Button reset_button;

        public AccelerometerSensorEventListener(LineGraphView graph, Button button, TextView view1, TextView view2, TextView view3) {
            accelerometerGraph = graph;
            reset_button = button;
            xView = view1;
            yView = view2;
            zView = view3;
        }

        private float[] highpass(float[] in) {
            float[] out = new float[in.length];
            float alpha = 0.2f;
            out[0] = 0;
            for(int i = 1; i < in.length; i++) {
                out[i] = alpha * out[i-1] + alpha * (in[i] - in[i-1]);
            }
            return out;
        }

        public float calcDerivative(float magnitude, Long timestamp) {
            // On first iteration, set the prevTimestamp & prevMagnitude
            if(prevTimestamp == null && prevMagnitude == 0.0f) {
                prevTimestamp = timestamp;
                prevMagnitude = magnitude;
                return 0;
            } else {
                float changeInY = magnitude-prevMagnitude;
                float changeInX = (float) (timestamp-prevTimestamp);
                float derivative = changeInY/changeInX;
                prevMagnitude = magnitude;
                prevTimestamp = timestamp;
                return derivative;
            }
        }

        public void onAccuracyChanged(Sensor s, int i) {

        }


        public void onSensorChanged(SensorEvent se) {
            if (se.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) {
                reset_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View vw) {
                        steps = 0;
                    }
                });
                // Get the 3 different acceleration values from se.values
                //float[] filteredValues = highpass(se.values);



                //values[0] = se.values[0];
                //values[1] = se.values[1];
                //values[2] = se.values[2];

                 values[0] += (se.values[0] - values[0]) / 3;
                 values[1] += (se.values[1] - values[1]) / 3;
                 values[2] += (se.values[2] - values[2]) / 3;

                 currentMagnitude = (float) Math.sqrt(((values[0]*values[0]) + (values[1]*values[1]) + (values[2]*values[2])));


//                if(Math.abs(values[0]) < 7) {
//                    values[0] = 0;
//                }
//                if(Math.abs(values[1]) < 7) {
//                    values[1] = 0;
//                }
//                if(Math.abs(values[2]) < 7) {
//                    values[2] = 0;
//                }
                if(lastStepTimestamp == null) {
                    lastStepTimestamp = se.timestamp;
                }

//                if( > 1000000) {
//                    secondsElapsedSinceLastStep += 1;
//                    lastStepTimestamp = se.timestamp;
//                }

                float timeDiff = (se.timestamp - lastStepTimestamp)/1000000;
                  if(prevDerivative == 0.0f) {
                      prevDerivative = calcDerivative(currentMagnitude, se.timestamp);
                  } else {
                      currentDerivative = calcDerivative(currentMagnitude, se.timestamp);
                      if(currentMagnitude > 1 && (currentDerivative < 0) && (prevDerivative > 0) && (timeDiff > 700) && (timeDiff < 1000)) {
                          steps += 1;
                          yView.setText("Time difference: " + timeDiff);
                          lastStepTimestamp = se.timestamp;
                          magnitudeOfStep = currentMagnitude;
                      }
                      prevDerivative = currentDerivative;
                  }
                if(timeDiff > 1000) {
                    lastStepTimestamp = se.timestamp;
                }
                xView.setText("Steps: " + steps);
//                if(Math.abs(values[0]) > Math.abs(maxX)) {
//                    maxX = values[0];
//                    xView.setText("X: " + maxX);
//                }
//                if(Math.abs(values[1]) > Math.abs(maxY)) {
//                    maxY = values[1];
//                    yView.setText("Y: " + maxY);
//                }
//                if(Math.abs(values[2]) > Math.abs(maxZ)) {
//                    maxZ = values[2];
//                    zView.setText("Z: " + maxZ);
//                }

               // magnitudeValue = (float) Math.sqrt(((xValue*xValue) + (yValue*yValue) + (zValue*zValue)));
                //float[] magnitudeArray = {magnitudeValue};
                accelerometerGraph.addPoint(values);
                // Update the text views
                //magnitudeView.setText("Accelerometer Magnitude: " + magnitudeValue);
            }
        }
    }
