package uwaterloo.ca.lab4_203_11;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import map.InterceptPoint;
import map.MapLoader;
import map.MapView;
import map.NavigationalMap;
import map.PositionListener;
import map.VectorUtils;

public class MainActivity extends Activity{

    public static int stepsTaken = 0;
    static MapView mv;
    static PlaceholderFragment pf = new PlaceholderFragment();
    static String mapName = "Lab-room-peninsula.svg";

    final int E2_3344_ID = 0;
    final int LAB_ROOM_ID = 1;
    final int LAB_ROOM_PEN_ID = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, pf)
                    .commit();
        }

        mv = new MapView(getApplicationContext(), 1400, 1100, 70, 70);
        registerForContextMenu(mv);
        NavigationalMap map = MapLoader.loadMap(getExternalFilesDir(null), mapName);
        mv.setMap(map);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        menu.add(ContextMenu.NONE, E2_3344_ID, ContextMenu.NONE, "E2-3344 map");
        menu.add(ContextMenu.NONE, LAB_ROOM_ID, ContextMenu.NONE, "Lab-room map");
        menu.add(ContextMenu.NONE, LAB_ROOM_PEN_ID, ContextMenu.NONE, "Lab-room-peninsula map");
        return true;
    }

    @Override
    public void onCreateContextMenu(ContextMenu  menu , View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        mv.onCreateContextMenu(menu, v, menuInfo);
    }

    @Override
    public boolean onContextItemSelected(MenuItem  item) {
        return  super.onContextItemSelected(item) ||  mv.onContextItemSelected(item);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        else {
            if (id == E2_3344_ID) {
                mapName = "E2-3344-new.svg";
                mv.scale = new PointF(50, 50);
            }
            else if (id == LAB_ROOM_ID) {
                mapName = "Lab-room.svg";
                mv.scale = new PointF(70, 70);
            }
            else if (id == LAB_ROOM_PEN_ID) {
                mapName = "Lab-room-peninsula.svg";
                mv.scale = new PointF(70, 70);
            }

            NavigationalMap map = MapLoader.loadMap(getExternalFilesDir(null), mapName);
            mv.setMap(map);
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment implements PositionListener{

        private SensorManager sensorManager;
        private Vibrator v;

        ThreeValueSensorEventListener accelerometerListener;
        ThreeValueSensorEventListener linearaccelerometerListener;
        ThreeValueSensorEventListener magneticListener;

        float[] Gravity;
        float[] Geomagnetic;
        float[] LastZValues = new float[2];
        float orientation[] = new float[3];
        float averageOrientation = 0;

        boolean step;
        boolean destinationSet = false;

        PointF currentLocation;
        PointF destinationLocation;

        static CompassView compass;

        TextView northText;
        TextView eastText;
        TextView rotation;
        TextView steps;
        TextView currentLoc;

        double stepsNorth = 0;
        double stepsEast = 0;

        float baseOrientation = 0;
        
        int state = 0;

        public PlaceholderFragment() {}

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);

            LinearLayout layout = (LinearLayout) rootView.findViewById(R.id.layout);

            currentLoc = new TextView(rootView.getContext());
            currentLoc.setText("Destination Not Reached");
            currentLoc.setGravity(android.view.Gravity.CENTER);
            layout.addView(currentLoc);

            layout.addView(mv);
            mv.setVisibility(View.VISIBLE);

            mv.addListener(pf);

            Button button;

            button = (Button) rootView.findViewById(R.id.button);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    stepsTaken = 0;
                    stepsNorth = 0;
                    stepsEast = 0;
                    steps.setText("Steps: 0\n");
                }
            });

            compass = new CompassView(rootView.getContext());
            layout.addView(compass);
            compass.setVisibility(View.VISIBLE);

            steps = new TextView(rootView.getContext());
            northText = new TextView(rootView.getContext());
            eastText = new TextView(rootView.getContext());
            rotation = new TextView(rootView.getContext());
            steps.setText("Steps: 0");
            layout.addView(steps);
            layout.addView(northText);
            layout.addView(eastText);
            layout.addView(rotation);

            v = (Vibrator) rootView.getContext().getSystemService(Context.VIBRATOR_SERVICE);

            sensorManager = (SensorManager) rootView.getContext().getSystemService(SENSOR_SERVICE);

            Sensor linearaccelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
            Sensor accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
            Sensor magnetic = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

            linearaccelerometerListener = new ThreeValueSensorEventListener();
            accelerometerListener = new ThreeValueSensorEventListener();
            magneticListener = new ThreeValueSensorEventListener();

            sensorManager.registerListener(linearaccelerometerListener, linearaccelerometer, SensorManager.SENSOR_DELAY_FASTEST);
            sensorManager.registerListener(accelerometerListener, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
            sensorManager.registerListener(magneticListener, magnetic, SensorManager.SENSOR_DELAY_NORMAL);

            return rootView;
        }

        @Override
        public void originChanged(MapView source, PointF location) {
            mv.setUserPoint(location);
            currentLocation = location;
            if (destinationSet) {
                if (Math.abs(destinationLocation.x - currentLocation.x) < 0.4 && Math.abs(destinationLocation.y - currentLocation.y) < 0.4) {
                    currentLoc.setText("Destination reached!");
                    //destinationReached = true;
                    v.vibrate(500);
                }
                else {
                    mv.setUserPath(DijkstraAlgorithm.calculateRoute(currentLocation, destinationLocation, mapName));
                }
            }
        }

        @Override
        public void destinationChanged(MapView source, PointF location) {
            mv.setUserPath(DijkstraAlgorithm.calculateRoute(currentLocation, location, mapName));
            destinationLocation = location;
            destinationSet = true;
        }

        public class CompassView extends View {
            Paint paint = new Paint();
            int cStartX;
            int dStartX;
            //int startX;
            int startY;
            int radius;
            float angle;

            public CompassView(Context context) {
                super(context);

                this.setLayoutParams(new ViewGroup.LayoutParams(1400, 700));

                paint.setStyle(Paint.Style.STROKE);
                paint.setColor(Color.BLACK);
                paint.setTextSize(50);
            }

            public void orientationChanged() {
                angle = averageOrientation;
                invalidate();
            }

            @Override
            protected void onDraw(Canvas canvas) {
                super.onDraw(canvas);
                startY = getHeight() / 2 + 50;
                radius = startY - 125;

                // Draw compass
                cStartX = getWidth() / 4;
                angle = averageOrientation;

                canvas.drawText("Direction of Travel", 50, 50, paint);
                canvas.drawCircle(cStartX, startY, radius, paint);

                float cStopX = (float) (cStartX + radius * Math.sin(angle));
                float cStopY = (float) (startY + radius * -Math.cos(angle));

                canvas.drawLine(cStartX, startY, cStopX, cStopY, paint);

                // Draw directions
                dStartX = 3 * getWidth() / 4;

                canvas.drawText("Walk this way", getWidth() / 2, 50, paint);
                canvas.drawCircle(dStartX - 20, startY, radius, paint);

                if (destinationSet) {
                    List<PointF> route = DijkstraAlgorithm.calculateRoute(currentLocation, destinationLocation, mapName);
                    float directionAngle = VectorUtils.angleBetween(currentLocation, new PointF(currentLocation.x, currentLocation.y + 1), route.get(1));
                    float dStopX = (float) (dStartX + radius * Math.sin(directionAngle));
                    float dStopY = (float) (startY + radius * Math.cos(directionAngle));

                    canvas.drawLine(dStartX, startY, dStopX, dStopY, paint);
                }
            }
        }

        protected float[] lowPass(float[] in) {
            float[] out = new float[in.length];
            float alpha = (float) 0.1;
            out[0] = alpha * in[0] + (1 - alpha) * in[0];
            out[1] = alpha * in[1] + (1 - alpha) * in[1];
            return out;
        }

        class ThreeValueSensorEventListener implements SensorEventListener {

            @Override
            public void onSensorChanged(SensorEvent event) {
                if (event.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION && destinationSet) {
                    LastZValues[0] = LastZValues[1];
                    LastZValues[1] = (event.values[2]);

                    float[] filteredValues = lowPass(LastZValues);

                    //////////////////RISING POSITIVE STATE/////////////////
                    if (filteredValues[1] < 5 && filteredValues[1] > 0.7 && (filteredValues[1] - filteredValues[0]) > 0.225 && state == 0) {
                        state = 1;
                    }

                    //////////////////FALLING POSITIVE STATE/////////////////
                    if (filteredValues[1] < 5 && filteredValues[1] > 0.7 && (filteredValues[1] - filteredValues[0]) < -0.225 && state == 1) {
                        state = 2;
                    }

                    //////////////////FALLING NEGATIVE STATE/////////////////
                    if (filteredValues[1] < -0.7 && filteredValues[1] > -5 && (filteredValues[1] - filteredValues[0]) < -0.225 && state == 2) {
                        state = 3;
                    }

                    //////////////////RISING NEGATIVE STATE//////////////////
                    if (filteredValues[1] < -0.7 && filteredValues[1] > -5 && (filteredValues[1] - filteredValues[0]) > 0.225 && state == 3) {
                        state = 4;
                    }

                    //////////////////STATE MACHINE RESET////////////////////
                    if (filteredValues[1] > 5 || filteredValues[1] < -5) {
                        state = 0;
                    }

                    ///////////////////ADD STEP//////////////////////////////
                    if (state == 4) {
                        steps.setText("Steps: " + String.format("%d", ++stepsTaken) + "\n");
                        step = true;
                        state = 0;
                    }
                }
                if (event.sensor.getType() == Sensor.TYPE_GRAVITY) {
                    Gravity = event.values;
                }
                if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
                    double north;
                    double east;
                    PointF newLocation;

                    Geomagnetic = event.values;
                    float R[] = new float[9];
                    float I[] = new float[9];
                    SensorManager.getRotationMatrix(R, I, Gravity, Geomagnetic);
                    SensorManager.getOrientation(R, orientation);

                    if (Math.abs(baseOrientation - orientation[0]) > Math.PI/8) {
                        baseOrientation = orientation[0];
                    }
                    averageOrientation = ((baseOrientation + orientation[0]) / 2) + 0.35f;

                    rotation.setText("Azimuth: " + String.format("%f", averageOrientation));
                    compass.orientationChanged();
                    if (step && destinationSet) {

                        north = Math.cos(averageOrientation);
                        east = Math.sin(averageOrientation);

                        stepsNorth += north;
                        stepsEast += east;

                        northText.setText("North: " + String.format("%f", stepsNorth) + "\n");
                        eastText.setText("East: " + String.format("%f", stepsEast) + "\n");

                        if (mapName == "E2-3344-new.svg") {
                            north = north * -1.05;
                        }
                        else {
                            north = north * -0.85;
                            east = east * 0.85;
                        }
                        newLocation = new PointF(currentLocation.x + (float)east, currentLocation.y + (float)north);

                        List<InterceptPoint> intersections = mv.map.calculateIntersections(currentLocation, newLocation);
                        if (intersections.size() == 0) {
                            pf.originChanged(mv, newLocation);
                        }
                        step = false;
                    }
                }
            }
            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {
                // TODO Auto-generated method stub
            }
        }
    }
}
