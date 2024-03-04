package com.example.dz3surface;

import static android.view.MotionEvent.ACTION_DOWN;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import androidx.annotation.NonNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.security.auth.callback.Callback;
public class mySurface extends SurfaceView implements SurfaceHolder.Callback {

    private List<DrawThread> threads = new ArrayList<>();
    private SurfaceHolder backHolder;

    public mySurface(Context context) {
        super(context);
        getHolder().addCallback(this);
    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder holder) {
        backHolder = holder;
        start();
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
        stop();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case ACTION_DOWN:
                DrawThread thread = new DrawThread(getHolder(), (int) event.getX(), (int) event.getY());
                thread.setRadius(rndRadius());
                thread.setColor(getRandomColor());
                thread.setSpeed(rndSpeed(),rndSpeed());
                threads.add(thread);
                thread.start();
                return true;
            default:
                return super.onTouchEvent(event);
        }
    }
    public static int rndRadius() {
        int radius = (int) (Math.random() * (110 - 10 + 1) + 10);
        return radius;
    }
    public static int getRandomColor() {
        Random random = new Random();
        int red = random.nextInt(256);
        int green = random.nextInt(256);
        int blue = random.nextInt(256);
        int color = Color.argb(255, red, green, blue);
        return color;
    }
    public static int rndSpeed() {
        int d= (int) (Math.random() * (50 - 10 + 1)) + 10;
        return d;
    }

    public void start(){
        for (DrawThread thread : threads) {
            thread.start();
        }
    }
    public void stop(){
        for (DrawThread thread : threads) {
            thread.interrupt();
        }
        threads.clear();
    }
    private class DrawThread extends Thread{
        int radius, color, dx,dy;
        Point point;
        Paint paint;
        SurfaceHolder holder;
        boolean flagx;
        boolean flagy;
        public DrawThread(SurfaceHolder holder, int startX, int startY){
            this.holder=holder;
            this.paint = new Paint();
            this.point = new Point(startX, startY);
        }
        public void setPoint(Point point){
            this.point = point;
        }
        public void setRadius(int radius){
            this.radius=radius;
        }
        public void setColor(int color){
            this.color=color;
        }
        public void setSpeed(int dx, int dy){
            this.dx= dx;
            this.dy=dy;
        }

        @Override
        public void run() {
            while(!isInterrupted()){
                update();
                draw();
                control();
            }

        }
        public void update(){

            int x= point.x;
            int y=point.y;
            if(point.x+radius>=getWidth()){
                flagx=true;
            }
            else if(point.x-radius<=0) flagx=false;
            else if(point.y+radius>=getHeight())flagy=true;
            else if (point.y-radius<=0)flagy=false;

            if(flagx==true&&flagy==true){
                x=point.x-dx;
                y=point.y-dy;
            }
            else if(flagx==true&&flagy==false){
                x=point.x-dx;
                y=point.y+dy;
            }
            else if(flagx==false&&flagy==true){
                x=point.x+dx;
                y=point.y-dy;
            }
            else if(flagx==false&&flagy==false){
                x=point.x+dx;
                y=point.y+dy;
            }
            point.set(x,y);
        }
        public void draw(){
            if(backHolder.getSurface().isValid()){
                Canvas canvas = backHolder.lockCanvas();
                canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
                for (DrawThread thread : threads) {
                    paint.setColor(thread.color);
                    canvas.drawCircle(thread.point.x, thread.point.y, thread.radius, paint);
                }
                backHolder.unlockCanvasAndPost(canvas);
            }
        }
        public void control(){
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}