package com.lfg.mobileang;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;

public class SensorActivity extends Activity implements SensorEventListener {
    private SensorManager mSensorManager;
    private Sensor[] mSensor = new Sensor[10];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor);

       //Check Gravity Sensor
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensor[0] = mSensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
        mSensor[1] = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensor[2] = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        mSensor[3] = mSensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);
        mSensor[4] = mSensorManager.getDefaultSensor(Sensor.TYPE_RELATIVE_HUMIDITY);
        mSensor[5] = mSensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);
        mSensor[6] = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        mSensor[7] = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        mSensor[8] = mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        mSensor[9] = mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);



    }
    @Override
    protected void onResume() {
        super.onResume();
        // register this class as a listener for the orientation and
        // accelerometer sensors
        for (int i = 0; i < mSensor.length;i++) {
            Sensor m = mSensor[i];
            if (m != null) {
                boolean b = mSensorManager.registerListener(this,
                        m,
                        SensorManager.SENSOR_DELAY_NORMAL);
                System.out.println("Registered?" + b);
            } else {
                System.out.println("Index="+ i + " was null.");
            }
        }

    }

    @Override
    protected void onPause() {
        // unregister listener
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    /**
     * Called when sensor values have changed.
     * <p>See {@link SensorManager SensorManager}
     * for details on possible sensor types.
     * <p>See also {@link SensorEvent SensorEvent}.
     * <p/>
     * <p><b>NOTE:</b> The application doesn't own the
     * {@link SensorEvent event}
     * object passed as a parameter and therefore cannot hold on to it.
     * The object may be part of an internal pool and may be reused by
     * the framework.
     *
     * @param event the {@link SensorEvent SensorEvent}.
     */
    @Override
    public void onSensorChanged(SensorEvent event) {


        //System.out.println("Systime="+System.currentTimeMillis());
        int t = event.sensor.getType();
        switch(t) {
            case Sensor.TYPE_GRAVITY: {
                TextView text_x, text_y, text_z = null;
                float[] values = event.values;
                text_x = (TextView) findViewById(R.id.gsensor_x);
                text_y = (TextView) findViewById(R.id.gsensor_y);
                text_z = (TextView) findViewById(R.id.gsensor_z);
                text_x.setText(String.valueOf(values[0]));
                text_y.setText(String.valueOf(values[1]));
                text_z.setText(String.valueOf(values[2]));
                break;
            }
            case Sensor.TYPE_ACCELEROMETER: {
                TextView text_x, text_y, text_z = null;
                float[] values = event.values;
                text_x = (TextView) findViewById(R.id.asensor_x);
                text_y = (TextView) findViewById(R.id.asensor_y);
                text_z = (TextView) findViewById(R.id.asensor_z);
                text_x.setText(String.valueOf(values[0]));
                text_y.setText(String.valueOf(values[1]));
                text_z.setText(String.valueOf(values[2]));
                break;
            }
            case Sensor.TYPE_GYROSCOPE: {
                TextView text_x, text_y, text_z = null;
                float[] values = event.values;
                text_x = (TextView) findViewById(R.id.ysensor_x);
                text_y = (TextView) findViewById(R.id.ysensor_y);
                text_z = (TextView) findViewById(R.id.ysensor_z);
                text_x.setText(String.valueOf(values[0]));
                text_y.setText(String.valueOf(values[1]));
                text_z.setText(String.valueOf(values[2]));
                // Movement
                break;
            }
            case Sensor.TYPE_AMBIENT_TEMPERATURE: {
                TextView text = null;
                float[] values = event.values;
                text = (TextView) findViewById(R.id.temp);
                text.setText(String.valueOf(values[0]));
                // Movement
                break;
            }
            case Sensor.TYPE_PRESSURE: {
                TextView text = null;
                float[] values = event.values;
                text = (TextView) findViewById(R.id.press);
                text.setText(String.valueOf(values[0]));
                // Movement
                break;
            }
            case Sensor.TYPE_RELATIVE_HUMIDITY: {
                TextView text = null;
                float[] values = event.values;
                text = (TextView) findViewById(R.id.hum);
                text.setText(String.valueOf(values[0]));
                // Movement
                break;
            }
            case Sensor.TYPE_ORIENTATION:  {
                TextView text_x, text_y, text_z = null;
                float[] values = event.values;
                text_x = (TextView) findViewById(R.id.osensor_x);
                text_y = (TextView) findViewById(R.id.osensor_y);
                text_z = (TextView) findViewById(R.id.osensor_z);
                text_x.setText(String.valueOf(values[0]) + " Azimuth");
                text_y.setText(String.valueOf(values[1])+ " Pitch");
                text_z.setText(String.valueOf(values[2])+ " Roll");
                // Movement
                break;
            }
            case Sensor.TYPE_MAGNETIC_FIELD: {
                TextView text_x, text_y, text_z = null;
                float[] values = event.values;
                text_x = (TextView) findViewById(R.id.msensor_x);
                text_y = (TextView) findViewById(R.id.msensor_y);
                text_z = (TextView) findViewById(R.id.msensor_z);
                text_x.setText(String.valueOf(values[0]) + " µT");
                text_y.setText(String.valueOf(values[1])+ " µT");
                text_z.setText(String.valueOf(values[2])+ " µT");
                // Movement
                break;
            }
            case Sensor.TYPE_PROXIMITY: {
                TextView text = null;
                float[] values = event.values;
                text = (TextView) findViewById(R.id.proximity);
                text.setText(String.valueOf(values[0]) + "cms");
                // Movement
                break;
            }
            case Sensor.TYPE_LIGHT: {
                TextView text = null;
                float[] values = event.values;
                text = (TextView) findViewById(R.id.light);
                text.setText(String.valueOf(values[0]) + " lux");
                // Movement
                break;
            }
        }
    }

    /**
     * Called when the accuracy of the registered sensor has changed.
     * <p/>
     * <p>See the SENSOR_STATUS_* constants in
     * {@link SensorManager SensorManager} for details.
     *
     * @param sensor
     * @param accuracy The new accuracy of this sensor, one of
     *                 {@code SensorManager.SENSOR_STATUS_*}
     */
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        System.out.println("Sensor Accuracy changed to:" + accuracy);
    }
}
