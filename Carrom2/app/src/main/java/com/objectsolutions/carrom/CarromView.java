package com.objectsolutions.carrom;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Typeface;
import android.support.v4.view.GestureDetectorCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Pterede on 10/6/2016.
 */

public class CarromView extends SurfaceView implements GestureDetector.OnGestureListener, GestureDetector.OnDoubleTapListener{
    final Point[] pockets = new Point[4];
    int secondsCount = 0;

    HashMap<String, String> processedCollisionsMap = new HashMap<String, String>();

    private static final int DISK_RADIUS = 20;
    private static final int STRIKER_RADIUS = 32;

    static final double SQRT2 = Math.sqrt(2);
    boolean dragStarted = false;
    boolean startBreak = true;

    float friction = 0.98f;
    int startx = -30;
    int starty = -30;
    int boardSize = 1575;


    private boolean flingStarted = false;
    //Visual elements
    private SurfaceHolder surfaceHolder;
    private Canvas myCanvas;
    private Paint paintBlack;
    private Bitmap carromBoard;
    private Paint paint;
    private Paint turnPaint;

    int currerntStrikePosn = 0;
    float[][] strikePosition = {
            {260f, 335f, 315f, 1185f},
            {340f, 250f, 1190f, 305f},
            {1215f, 330f, 1275f, 1180f},
            {340f, 1210f, 1190f, 1260f},
    };
    //mobile visual elements
    private MovingDisk striker;
    private MovingDisk queen;
    private List<MovingDisk> whites = new ArrayList<MovingDisk>(9);
    private List<MovingDisk> blacks = new ArrayList<MovingDisk>(9);

    private List<MovingDisk> allDisks = new ArrayList<>(20);

    private GestureDetectorCompat mDetector;
    private MyThread myThread;
    private TextView frictionTextView;
    private Context c;


    public CarromView(Context context) {
        super(context);
        init(context);
    }

    public CarromView(Context context,
                      AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CarromView(Context context,
                      AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context ctx) {

        allDisks.clear();
        whites.clear();
        blacks.clear();
        if ( myThread != null ) {
            myThread.end();
            myThread = null;
        }
        pockets[0] = new Point(65,65);
        pockets[1] = new Point(1400,65);
        pockets[2] = new Point(1400,1386);
        pockets[3] = new Point(65,1386);
        paint = new Paint();
        paint.setAntiAlias(true);

        paint.setColor(Color.BLACK);
        paint.setTextSize(36);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        paint.setAlpha(100);

        paintBlack = new Paint();
        paintBlack.setColor(Color.BLACK);
        paintBlack.setAlpha(100);


        turnPaint = new Paint();
        turnPaint.setAntiAlias(true);
        turnPaint.setColor(Color.parseColor("#FF0000"));
        turnPaint.setAlpha(50);

        this.c = ctx;
        setLongClickable(true);
        if ( mDetector == null  )  mDetector = new GestureDetectorCompat(ctx, this);


        //Striker
        striker = new MovingDisk(ctx, "Striker", R.drawable.c_striker, STRIKER_RADIUS, 250, 400);

        striker.mass = 30; //in some units...This will make striker 4 times heavier than disks.
        striker.threshold = 8;
        //Queen
        Point qpt = createRandomDiskPosn();
        queen = new MovingDisk(ctx, "Queen", R.drawable.queen_carrom_disk, DISK_RADIUS, qpt.x, qpt.y);



        allDisks.add(striker);
        allDisks.add(queen);

        int x_center = 746;
        int y_center = 736;
        queen.x = x_center;
        queen.y = y_center;

        //OUTER ring
        //Whites
        for (int i = 0; i < 6; i++) {
            int my_x = x_center + (int)(4.1 * DISK_RADIUS * Math.cos(Math.toRadians(45+60*i)));
            int my_y = y_center + (int)(4.1 * DISK_RADIUS * Math.sin(Math.toRadians(45+60*i)));
            Point tmp_pt = new Point(my_x, my_y);
            MovingDisk disk = new MovingDisk(ctx, "white"+(i+1), R.drawable.white_carrom_disk, DISK_RADIUS, tmp_pt.x, tmp_pt.y);

            whites.add(disk);
            allDisks.add(disk);
        //Blacks
            my_x = x_center + (int)(4.1 * DISK_RADIUS * Math.cos(Math.toRadians(75+(60*i))));
            my_y = y_center + (int)(4.1 * DISK_RADIUS * Math.sin(Math.toRadians(75+(60*i))));
            tmp_pt = new Point(my_x, my_y);
            disk = new MovingDisk(ctx, "black"+(i+1),R.drawable.black_carrom_disk, DISK_RADIUS, tmp_pt.x, tmp_pt.y);

            blacks.add(disk);
            allDisks.add(disk);
        }
        for (int i = 0; i < 3; i++) {
            int my_x = x_center + (int)(2.05 * DISK_RADIUS * Math.cos(Math.toRadians(45+120*i)));
            int my_y = y_center + (int)(2.05 * DISK_RADIUS * Math.sin(Math.toRadians(45+120*i)));
            Point tmp_pt = new Point(my_x, my_y);
            MovingDisk disk = new MovingDisk(ctx, "white"+(i+1), R.drawable.white_carrom_disk, DISK_RADIUS, tmp_pt.x, tmp_pt.y);

            whites.add(disk);
            allDisks.add(disk);
            //Blacks
            my_x = x_center + (int)(2.05 * DISK_RADIUS * Math.cos(Math.toRadians(105+(120*i))));
            my_y = y_center + (int)(2.05 * DISK_RADIUS * Math.sin(Math.toRadians(105+(120*i))));
            tmp_pt = new Point(my_x, my_y);
            disk = new MovingDisk(ctx, "black"+(i+1),R.drawable.black_carrom_disk, DISK_RADIUS, tmp_pt.x, tmp_pt.y);

            blacks.add(disk);
            allDisks.add(disk);
        }

        //Carrom Board
        carromBoard = BitmapFactory.decodeResource(getResources(),
                R.drawable.carrom);

        carromBoard = Bitmap.createScaledBitmap(carromBoard, boardSize, boardSize, true);

        //surface on which all above rests
        if ( surfaceHolder == null  ) {
            surfaceHolder = getHolder();
            surfaceHolder.addCallback(new SurfaceHolder.Callback() {

                @Override
                public void surfaceCreated(SurfaceHolder holder) {
                    Canvas canvas = holder.lockCanvas(null);
                    myCanvas = canvas;
                    noneMoves();
                    drawSomething(canvas);
                    holder.unlockCanvasAndPost(canvas);
                }

                @Override
                public void surfaceChanged(SurfaceHolder holder,
                                           int format, int width, int height) {
                    // TODO Auto-generated method stub
                    Log.e("CarromView", "surfaceChanged");

                }

                @Override
                public void surfaceDestroyed(SurfaceHolder holder) {
                    // TODO Auto-generated method stub

                }
            });
        }

    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        /*
        float my_x = event.getX();
        float my_y = event.getY();

        //Log.e("OnTouchEvent", event.toString());
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:

                int rightBound = striker.x + 60;
                int leftBound = striker.x - 60;
                int downBound = striker.y + 60;
                int upBound = striker.y - 60;

                if (my_x > leftBound && my_x < rightBound && my_y > upBound && my_y < downBound) {
                    dragStarted = true;
                } else {
                    dragStarted = false;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (dragStarted) {
                    striker.x = (int) my_x;
                    striker.y = (int) my_y;
                    if (myThread == null) {
                        myThread = new MyThread(this);
                    }
                    if ( !myThread.isRunning() )
                        myThread.start();
                }
                break;
            case MotionEvent.ACTION_UP:
                dragStarted = false;
                if (myThread != null) {
                    myThread.end();
                    myThread = null;
                }
        }
        */
        this.mDetector.onTouchEvent(event);
        // Be sure to call the superclass implementation
        return super.onTouchEvent(event);

    }

    private void noneMoves() {

        for(MovingDisk md:allDisks) {
            md.isMoving = false;
            md.previousCollisionWith = null;
        }
        secondsCount = 0;
    }

    private Point createRandomDiskPosn() {
        final int left_x = 50;
        final int right_x = 1440;
        final int top_y = 50;
        final int bottom_y = 1440;

        Point p = new Point();
        p.x = (int) (Math.random() * boardSize);
        if (p.x < left_x ) p.x = left_x + STRIKER_RADIUS;
        if (p.x > right_x) p.x = right_x - STRIKER_RADIUS;
        p.y = (int) (Math.random() * boardSize);
        if (p.y < top_y) p.y = top_y + STRIKER_RADIUS;
        if (p.y > bottom_y) p.y = bottom_y - STRIKER_RADIUS;

        //If collision, try again

        for(MovingDisk m1:allDisks) {
            double distanceSquaredBetweenDisks = Math.pow(m1.x - p.x, 2) + Math.pow(m1.y - p.y, 2);

            double clearanceNeeded = m1.radius + STRIKER_RADIUS;
            if ( distanceSquaredBetweenDisks < (clearanceNeeded*clearanceNeeded)) {
                return createRandomDiskPosn();
            }
        }

        return p;
    }

    private void renderAllDisks(Canvas canvas) {



        String msg = "All Stopped";
        boolean stopped = hasEveryOneStoppedMoving();
        int whitesPocketed = 0;
        int blacksPocketed = 0;
        int queenPocketed = 0;
        if (stopped) {
            int pocketedDisks = 0;
            for (int i = 0; i < allDisks.size(); i++) {
                MovingDisk d = allDisks.get(i);
                if (!d.inPlay()) {
                    pocketedDisks++;
                    d.y = 1500;
                    d.x = 100 + pocketedDisks * 50;
                }
            }
            try {
                Thread.sleep(50);
                pocketedDisks = 0;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            for (int i = 0; i < allDisks.size(); i++) {
                MovingDisk d = allDisks.get(i);
                if (!d.inPlay()) {
                    pocketedDisks++;
                    d.showOriginal();
                    if (d.diskId == R.drawable.white_carrom_disk) whitesPocketed++;
                    if (d.diskId == R.drawable.black_carrom_disk) blacksPocketed++;
                    if (d.diskId == R.drawable.queen_carrom_disk) queenPocketed++;
                    d.y = 1500;
                    d.x = 100 + pocketedDisks * 50;
                }
            }

        }

        for (MovingDisk d : allDisks) {

            //draw
            d.drawMeOn(canvas);
            if (stopped) {
                canvas.drawText("Player #" + currerntStrikePosn + " :whites = " + whitesPocketed + ", blacks=" + blacksPocketed + ", queen=" + (queenPocketed == 0 ? "free" : "captured"), 500, 1650+(currerntStrikePosn*100), this.paint);

            } else {
                canvas.drawText("Striker at x = " + striker.x + ", y=" + striker.y, 500, 1650, this.paint);
            }

        }
        if ( startBreak ) draw1stTurn(canvas);
        if ( stopped ) {
            drawTurn(canvas);
            Log.e("THREAD", myThread==null?"NULL":myThread.getName());
            if (myThread != null) {
                myThread.end();
                myThread = null;
            }
        }
    }

    private void drawTurn(Canvas canvas) {
        if (!flingStarted) return;

        currerntStrikePosn++;
        currerntStrikePosn = currerntStrikePosn % 4;
        //Log.e("CURRENTPOSN", "currentpos:"+currerntStrikePosn + " alreadyMoved:" +alreadyMoved );
        canvas.drawRect(strikePosition[currerntStrikePosn][0],
                strikePosition[currerntStrikePosn][1],
                strikePosition[currerntStrikePosn][2],
                strikePosition[currerntStrikePosn][3],
                turnPaint);
        flingStarted = false;

    }
    private void draw1stTurn(Canvas canvas) {

        currerntStrikePosn=0;
        currerntStrikePosn = currerntStrikePosn % 4;
        //Log.e("CURRENTPOSN", "currentpos:"+currerntStrikePosn + " alreadyMoved:" +alreadyMoved );
        canvas.drawRect(strikePosition[currerntStrikePosn][0],
                strikePosition[currerntStrikePosn][1],
                strikePosition[currerntStrikePosn][2],
                strikePosition[currerntStrikePosn][3],
                turnPaint);
        flingStarted = false;

    }

    private boolean hasEveryOneStoppedMoving() {
        for(MovingDisk md: allDisks) {
            if ( md.isMoving ) return false;
        }
        return true;
    }
/*
    protected void dragStriker(Canvas canvas) {

        //this autostop only applies when u r NOT dragging

        if (dragStarted) {

            canvas.drawColor(Color.WHITE);
            canvas.drawBitmap(carromBoard,
                    startx, starty, null);

            renderAllDisks(canvas);
            return;
        }
    }
*/
    /**
     * Notified when a tap occurs with the down {@link MotionEvent}
     * that triggered it. This will be triggered immediately for
     * every down event. All other events should be preceded by this.
     *
     * @param e The down motion event.
     */
    @Override
    public boolean onDown(MotionEvent e) {
        //Log.e("ON Down", "AGAIN On DOwn");
        return true;
    }


    /**
     * The user has performed a down {@link MotionEvent} and not performed
     * a move or up yet. This event is commonly used to provide visual
     * feedback to the user to let them know that their action has been
     * recognized i.e. highlight an element.
     *
     * @param e The down motion event
     */
    @Override
    public void onShowPress(MotionEvent e) {

    }

    /**
     * Notified when a tap occurs with the up {@link MotionEvent}
     * that triggered it.
     *
     * @param e The up motion event that completed the first tap
     * @return true if the event is consumed, else false
     */
    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    /**
     * Notified when a scroll occurs with the initial on down {@link MotionEvent} and the
     * current move {@link MotionEvent}. The distance in x and y is also supplied for
     * convenience.
     *
     * @param e1        The first down motion event that started the scrolling.
     * @param e2        The move motion event that triggered the current onScroll.
     * @param distanceX The distance along the X axis that has been scrolled since the last
     *                  call to onScroll. This is NOT the distance between {@code e1}
     *                  and {@code e2}.
     * @param distanceY The distance along the Y axis that has been scrolled since the last
     *                  call to onScroll. This is NOT the distance between {@code e1}
     *                  and {@code e2}.
     * @return true if the event is consumed, else false
     */
    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        //Log.e("SurfaceView", "Scrollllllllllllllllliiiiiiiiiiiiinnnnnnnnnnnng");
        return false;
    }

    /**
     * Notified when a long press occurs with the initial on down {@link MotionEvent}
     * that trigged it.
     *
     * @param  event The initial on down motion event that started the longpress.
     */
    @Override
    public void onLongPress(MotionEvent event) { moveStrikerTo(event);}

    private void moveStrikerTo(MotionEvent event) {

        //Log.e("LONGPRESS", event.toString());
        float my_x = event.getX();
        float my_y = event.getY();
        striker.x = (int) my_x;
        striker.y = (int) my_y;
        Log.e("LONGPRESS", myThread == null ? "NEW THREAD" : myThread.getName());
        if (myThread == null) {

            myThread = new MyThread(this);
        }
        myThread.start();
    }

    /**
     * Notified of a fling event when it occurs with the initial on down {@link MotionEvent}
     * and the matching up {@link MotionEvent}. The calculated velocity is supplied along
     * the x and y axis in pixels per second.
     *
     * @param e1        The first down motion event that started the fling.
     * @param e2        The move motion event that triggered the current onFling.
     * @param velocityX The velocity of this fling measured in pixels per second
     *                  along the x axis.
     * @param velocityY The velocity of this fling measured in pixels per second
     *                  along the y axis.
     * @return true if the event is consumed, else false
     */
    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

        if (myThread != null) {
            return false;
        }

        //if the Striker is not in the designated area, NO FLING Possible
        if ( notInStrikeZone(striker) ) {
            return false;
        }
        Log.e("COLLISION FLING", ": x=" + velocityX + " y="+ velocityY);
        //NEW Fling means reset the deltaX & deltaY
        flingStarted = true;
        //no one else is moving
        noneMoves();
        if ( startBreak ) {
            for(MovingDisk d : allDisks){
                Point p = createRandomDiskPosn();
                d.x = p.x;
                d.y = p.y;
                //checkForPocket(d);
            }
            startBreak = false;
        } else {
            striker.deltaX = velocityX / 100f;
            striker.deltaY = velocityY / 100f;
        }
        //striker is the only guy moving...initially....
        striker.isMoving = true;

        myThread = new MyThread(this);

        myThread.start();

        return false;
    }

    private boolean notInStrikeZone(MovingDisk striker) {
        return false;
        /*
        return  (  striker.x < strikePosition[currerntStrikePosn][0]-50
                || striker.x > strikePosition[currerntStrikePosn][2]+50
                || striker.y < strikePosition[currerntStrikePosn][1]-50
                || striker.y > strikePosition[currerntStrikePosn][3]+50 );*/
    }

    // A brute force & Not a very smart detection algorithm but adequate for just 20 moving disks ( 9 whites, 9 blacks and a Queen and a striker
    private void detectCollisions(MovingDisk m1) {

        //for each disk on board other than the moving disk md
        //for (MovingDisk m1 : allDisks) {

            if ( !m1.inPlay() || !m1.isMoving ) return;

            for (MovingDisk m2 : allDisks) {
                if (!m2.inPlay() ) continue;//cnt collide if not on board or is marked for removal
                if (m1 == m2) continue; //cant colide with self.

                //is this collision already processed?.
                if ( m1.previousCollisionWith == m2 || m2.previousCollisionWith == m1 ) {
                    continue;
                }

                double distanceSquaredBetweenDisks = Math.pow(m1.x - m2.x, 2) + Math.pow(m1.y - m2.y, 2);
                double distanceBetweenDisks = Math.sqrt(distanceSquaredBetweenDisks); //sqrt
                double clearanceNeeded = m1.radius + m2.radius;

                // if the distance between the centers is less than the DIAMETER of the the md and the aDisk...then its a collision
                if (distanceBetweenDisks < clearanceNeeded) {
                    //Log.e("BEFORE COLLISION", "Collided : " + m1.name + ":" + "x="+m1.deltaX + " y="+m1.deltaY + " name:"+ m2.name + "x="+m2.deltaX + " y="+m2.deltaY);

                    m1.previousCollisionWith = m2;
                    m2.previousCollisionWith = m1;

                    processedCollisionsMap.put(m2.name+","+m1.name, "TRUE");

                    //we have a collision. Perfectly elastic...i.e. momentum and KE conserved.
                    float massSum = (m1.mass + m2.mass);
                    //new position & velocity of m1
                    m1.deltaX = (m1.deltaX * (m1.mass - m2.mass) + (2 * m2.mass * m2.deltaX)) / massSum;
                    m1.deltaY = (m1.deltaY * (m1.mass - m2.mass) + (2 * m2.mass * m2.deltaY)) / massSum;

                    //new position & velocity of m2
                    m2.deltaX = (m2.deltaX * (m2.mass - m1.mass) + (2 * m1.mass * m1.deltaX)) / massSum;
                    m2.deltaY = (m2.deltaY * (m2.mass - m1.mass) + (2 * m1.mass * m1.deltaY)) / massSum;

                    adjustOverlap(m1,m2,clearanceNeeded-distanceBetweenDisks);

                    // both disks: is moving if either X or Y velocity is non zero...with floats checking qualitto zero is not "nice"
                    m1.isMoving = (Math.abs(m1.deltaX) > m1.threshold || Math.abs(m1.deltaY) > m1.threshold);
                    m2.isMoving = (Math.abs(m2.deltaX) > m2.threshold || Math.abs(m2.deltaY) > m2.threshold);
                    Log.e("AFTER COLLISION", "Collided : " + m1.name + ":" + "x="+m1.deltaX + " y="+m1.deltaY + " name:"+ m2.name + "x="+m2.deltaX + " y="+m2.deltaY);
                }
            }
        //}
    }

    private void adjustOverlap(MovingDisk m1, MovingDisk m2, double overlap) {
        if ( m1.radius == m2.radius ) {
            pushOut(m1, overlap);
            pushOut(m2, overlap);
        } else {
            if (m1.radius > m2.radius) {
                pushOut(m2, overlap);
            } else {
                pushOut(m1, overlap);
            }
        }
    }

    private void pushOut(MovingDisk m, double overlap) {

        double ratioY2X = m.deltaY /m.deltaX;
        double xPortion = overlap/SQRT2;
        double yPortion = overlap/SQRT2;


        if ( m.deltaX > 0 ) m.x += xPortion;
        if ( m.deltaX < 0 ) m.x -= xPortion ;
        reflectX(m);

        if ( m.deltaY > 0 ) m.y += yPortion ;
        if ( m.deltaY < 0 ) m.y -= yPortion ;
        reflectY(m);
    }

    protected void drawSomething(Canvas canvas)  {

        secondsCount++;

        /*
        //If all we want to do is drag striker.....
        if (dragStarted) {
            dragStriker(canvas);
            return;
        }
        */


        for (MovingDisk md : allDisks) {
            //is the disk on board and is still moving ? if not...remove it and get to next disk
            if ( !md.isMoving ) continue;

            //Log.e("PROCESSING", "Disk:" + md.name);
            //if we want to render after a "fling" of the striker
            double brakefactor = 1+ Math.log(secondsCount);
            md.deltaX = friction * md.deltaX;
            md.deltaY = friction * md.deltaY;

            //is either axis over 5? if not stop it
            md.isMoving =  (Math.abs(md.deltaX) > md.threshold || Math.abs(md.deltaY) > md.threshold);
            if ( !md.isMoving ) {
                md.deltaX = 0;
                md.deltaY = 0;
                continue;
            }

            //DETECT COLLISION...if collision impart velocity and add to moving disks
            detectCollisions(md);

            //MOVE HORIZONTAL
            md.x += md.deltaX;
            //MOVE VERTICAL
            md.y += md.deltaY;

            if ( !"Striker".equals(md.name) ) {
                checkForPocket(md);
            }

            reflectX(md);
            reflectY(md);
        }

        //PAINT
        canvas.drawColor(Color.GRAY);
        canvas.drawBitmap(carromBoard,
                startx, starty, null);
        renderAllDisks(canvas);

    }

    private boolean checkForPocket(MovingDisk m1) {

//Log.e("CHECKPOCKET", m1.name);
        for( Point p: pockets)
        {
            double distanceSquaredBetweenDisks = Math.pow(m1.x - p.x, 2) + Math.pow(m1.y - p.y, 2);
            double distanceBetweenDisks = Math.sqrt(distanceSquaredBetweenDisks);
            Log.e("CHECKPOCKET", m1.name + " distance=" + distanceBetweenDisks + " x="+m1.x + " y="+ m1.y );
            if (  distanceBetweenDisks < 33 ) {

                m1.deltaX = 0; m1.deltaY = 0;
                m1.isMoving = true;
                m1.setPlay(false);

                return true;
            }
        }
        return false;
    }

    private void reflectY(MovingDisk md) {

        if (md.deltaY > 0) {
            if (md.y >= (boardSize - 180)) {
                md.previousCollisionWith = null;
                md.deltaY *= -1;
            }
        } else {
            if (md.y <= 50) {
                md.previousCollisionWith = null;
                md.deltaY *= -1;
            }
        }
    }

    private void reflectX(MovingDisk md) {
        if (md.deltaX > 0) {
            if (md.x >= (boardSize - 170)) {
                md.previousCollisionWith = null;
                md.deltaX *= -1;
            }
        } else {
            if (md.x <= 60) {
                md.previousCollisionWith = null;
                md.deltaX *= -1;
            }
        }
    }

    /**
     * Notified when a single-tap occurs.
     * <p>
     * Unlike {@link GestureDetector.OnGestureListener#onSingleTapUp(MotionEvent)}, this
     * will only be called after the detector is confident that the user's
     * first tap is not followed by a second tap leading to a double-tap
     * gesture.
     *
     * @param e The down motion event of the single-tap.
     * @return true if the event is consumed, else false
     */
    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) {
        return false;
    }

    /**
     * Notified when a double-tap occurs.
     *
     * @param event The down motion event of the first tap of the double-tap.
     * @return true if the event is consumed, else false
     */
    @Override
    public boolean onDoubleTap(MotionEvent event) {
        moveStrikerTo(event);
        return true;
    }

    /**
     * Notified when an event within a double-tap gesture occurs, including
     * the down, move, and up events.
     *
     * @param e The motion event that occurred during the double-tap gesture.
     * @return true if the event is consumed, else false
     */
    @Override
    public boolean onDoubleTapEvent(MotionEvent e) {
        return false;
    }

    public void resetStartBreak() {
        startBreak = true;
    }
}
