package simpleapp.android.taobits.net.simpleorientationsensorapplication;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.widget.TextView;


public class SimpleOrientationSensorApplicationActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple_application);

        sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        findViews();
    }

    private void findViews() {
        azimuthTextView = (TextView) findViewById(R.id.azimuth);
        pitchTextView = (TextView) findViewById(R.id.pitch);
        rollTextView = (TextView) findViewById(R.id.roll);
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(sensorListener, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(sensorListener, magnetometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(sensorListener);
    }

    TextView azimuthTextView;
    TextView pitchTextView;
    TextView rollTextView;

    SensorManager sensorManager;
    SensorListener sensorListener = new SensorListener();
    Sensor accelerometer;
    Sensor magnetometer;

    class SensorListener implements SensorEventListener {

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {} // Must be implemented but nothing todo here

        float[] gravity;
        float[] geomagnetic;

        @Override
        public void onSensorChanged(SensorEvent event) {
            float[] rotationMatrixR = new float[9];
            float[] rotationMatrixI = new float[9];
            float[] orientation = new float[3];
            if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
                gravity = event.values.clone();
            if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
                geomagnetic = event.values.clone();
            if (gravity == null || geomagnetic == null) return; // Not all data

            boolean success =  SensorManager.getRotationMatrix(rotationMatrixR, rotationMatrixI, gravity, geomagnetic);
            if (success) {
                sensorManager.getOrientation(rotationMatrixR, orientation);
                float azimuthAngle = orientation[0];
                float pitchAngle = orientation[1];
                float rollAngle = orientation[2];
                double azimuthDegrees = Math.toDegrees(azimuthAngle);
                double pitchDegrees = Math.toDegrees(pitchAngle);
                double rollDegrees = Math.toDegrees(rollAngle);

                azimuthTextView.setText(Double.toString(azimuthDegrees));
                pitchTextView.setText(Double.toString(pitchDegrees));
                rollTextView.setText(Double.toString(rollDegrees));
            }
        }

    }
}
