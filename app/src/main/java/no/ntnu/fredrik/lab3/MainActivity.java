package no.ntnu.fredrik.lab3;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private CanvasView canvas;
    private int circleRadius = 30;
    private float circleX;
    private float circleY;
    private Handler handler;
    private float sensorX;
    private float sensorY;
    private long lastSensorUpdateTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set orientation to landscape
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.activity_main);

        //Sound and haptic feedback
        final Vibrator vibratorFeedback = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        final ToneGenerator tg = new ToneGenerator(AudioManager.STREAM_NOTIFICATION, 100);

        //Sensors
        SensorManager sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        assert sensorManager != null;
        Sensor accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this, accelerometer,SensorManager.SENSOR_DELAY_FASTEST);

        //Display
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        //Gets screen size for height and width
        final int screenWidth = size.x;
        final int screenHeight = size.y;

        //Modified screen sizes
        final int screenSizeHeight = screenHeight - 300;
        final int screenSizeWidth =  screenWidth - 100;

        circleX = screenWidth / 2 - circleRadius;
        circleY = screenHeight / 2 - circleRadius;

        canvas = new CanvasView(this);
        setContentView(canvas);

        handler = new Handler(){

            @Override
            public void handleMessage(Message message){
                canvas.invalidate();
            }

        };

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {

                //Draws on the x axis
                //makes sure that it does not draw outside screen
                if (circleX < screenSizeWidth && circleX > 100){
                    circleX = circleX + sensorY * 6.0f;
                }
                else if (circleX < screenSizeWidth){
                    circleX = circleX + 100 -(sensorX * 6.0f);
                    assert vibratorFeedback != null;
                    vibratorFeedback.vibrate(50);
                    tg.startTone(ToneGenerator.TONE_PROP_BEEP);

                }
                else if (circleX > 0){
                    circleX = circleX - 100 -(sensorX * 6.0f);
                    assert vibratorFeedback != null;
                    vibratorFeedback.vibrate(50);
                    tg.startTone(ToneGenerator.TONE_PROP_BEEP);
                }

                // Draws on the Y axis
                //makes sure that it does not draw outside screen
                if (circleY < screenSizeHeight && circleY > 100){
                    circleY = circleY + sensorX * 6.0f;
                }
                else if (circleY < screenSizeHeight){
                    circleY = circleY + 100 -(sensorY * 6.0f);
                    assert vibratorFeedback != null;
                    vibratorFeedback.vibrate(50);
                    tg.startTone(ToneGenerator.TONE_PROP_BEEP);
                }
                else if (circleY > 0){
                    circleY = circleY - 100 -(sensorY * 6.0f);
                    assert vibratorFeedback != null;
                    vibratorFeedback.vibrate(50);
                    tg.startTone(ToneGenerator.TONE_PROP_BEEP);
                }

                // prints to log for development use
                System.out.println("x " + circleX);
                System.out.println("y " + circleY);
                System.out.println("heigth " + screenSizeHeight);
                System.out.println("width "+ screenSizeWidth);

                handler.sendEmptyMessage(0);
            }
        },0,100);
    }

    // Updating sensor
    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        Sensor mySensor = sensorEvent.sensor;

        if (mySensor.getType() == Sensor.TYPE_ACCELEROMETER){

            long currentTime = System.currentTimeMillis();

            if ((currentTime - lastSensorUpdateTime) > 100){
                lastSensorUpdateTime= currentTime;

                sensorX= sensorEvent.values[0];
                sensorY= sensorEvent.values[1];
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
    }

    // Draws in Canvas View
    public class CanvasView extends View {
        private Paint ball;
        private Paint background;

        public CanvasView(Context context){
            super(context);
            setFocusable(true);

            ball = new Paint();
            background = new Paint();
        }
        //Sets circle and rectangle
        public void onDraw(Canvas screen){
            //Circle
            ball.setStyle(Paint.Style.FILL);
            ball.setColor(Color.RED);
            ball.setAntiAlias(true);
            ball.setTextSize(30f);

            //Rectangle
            background.setStyle(Paint.Style.FILL);
            background.setColor(Color.GRAY);
            background.setAntiAlias(true);
            background.setTextSize(30f);

            //Rectangle position
            int canvasW = getWidth();
            int canvasH = getHeight();
            Point centerOfCanvas = new Point(canvasW / 2, canvasH / 2);
            int rectW = canvasW -100;
            int rectH = canvasH -100;
            int left = centerOfCanvas.x - (rectW / 2);
            int top = centerOfCanvas.y - (rectH / 2);
            int right = centerOfCanvas.x + (rectW / 2);
            int bottom = centerOfCanvas.y + (rectH / 2);
            Rect rect = new Rect(left, top, right, bottom);

            //draws
            screen.drawRect(rect, background);
            screen.drawCircle(circleX,circleY,circleRadius,ball);

        }
    }
}
