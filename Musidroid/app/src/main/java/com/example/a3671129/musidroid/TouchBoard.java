package com.example.a3671129.musidroid;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ConcurrentModificationException;
import java.util.ListIterator;

import l2i013.musidroid.model.InstrumentPart;
import l2i013.musidroid.model.Note;
import l2i013.musidroid.util.NoteName;



public class TouchBoard extends SurfaceView implements SurfaceHolder.Callback{
    int w,h;
    TheApplication app;
    boolean onActionMove=false;
    int firstX,firstY,lastX,lastY,longueur;
    public TouchBoard(Context context) {
        super(context);
        getHolder().addCallback(this);
    }
    public TouchBoard(Context context, AttributeSet attrs) {
        super(context, attrs);
        getHolder().addCallback(this);
        app = (TheApplication) (context.getApplicationContext());
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

        w= width;
        h= height;
        reDraw();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }
    public void onDraw(Canvas c) {
        int instantDebut=app.getInstantDebut();

        InstrumentPart ip = app.getInstrument();
        Paint p = new Paint();
        ListIterator<Note> it =   ip.getNotes().listIterator();
        c.drawColor(Color.WHITE);
        int H=h/13;
        int  W=w/8;
        int RAYON=(int)Math.sqrt((W/2)*(W/2)+(H/2)*(H/2))/2;
        if(w>h) {
            RAYON=RAYON/4;
        }
        int x,y;

        for(int j=1;j<13;j++) {

            y=j*H+H/2;
            p.setColor(Color.BLACK);
            p.setTextSize((float)W/4);
         //   p.setTextAlign(Align.);
            c.drawText(""+ NoteName.ofNum(12-j),(float)W/4,y+RAYON*(float)(0.4),p);

            for (int i=1;i<8;i++) {

                x=i*W+W/2;
                p.setColor(Color.BLACK);
                p.setTextSize((float)W/4);
                //   p.setTextAlign(Align.);
                c.drawText(""+ (i-1+app.getInstantDebut()),x-RAYON*(float)(0.4),(float)H*(float)0.6,p);

                p.setColor(Color.BLACK);

                c.drawCircle(x,y, RAYON/2, p);

            }
        }
        while (it.hasNext()) {

            Note xy = it.next();
            if ((xy.getInstant()+xy.getDuration()-1) >= instantDebut  && (xy.getInstant()+xy.getDuration()-1) <= instantDebut+6
                    || ( (xy.getInstant()) >= instantDebut  && (xy.getInstant()) <= instantDebut+6)
                    ) {
                p.setColor(Color.RED);


                if (xy.getDuration() == 1) {//debutafficher est ce que la fin est dans la fenetre d'affichage // est ce que le debut est superieur ala colonne de fin / soit la fin est inferieur a la colonne fin et superieur a la colonne au debut

                    c.drawCircle( (((xy.getInstant()-instantDebut)% 7)+1) * W + W /2, (12-xy.getName().getNum()) * H + H / 2, RAYON, p);
                } else if (xy.getDuration() > 1) {
                    int recfirstX,reclastX;
                    if(xy.getInstant()<instantDebut) {
                        recfirstX = (int) Math.round(1+W + W / (double) 4);
                        reclastX = (int) Math.round(recfirstX + W * (xy.getDuration()-(instantDebut-xy.getInstant())) - W / (double) 2);

                    }
                    else
                    {
                        recfirstX = (int) Math.round((((xy.getInstant() - instantDebut) % 7) + 1) * W + W / (double) 4);
                        reclastX = (int) Math.round(recfirstX + W * xy.getDuration() - W / (double) 2);

                    }
                    int recfirstY = (int) Math.round(((12-xy.getName().getNum()) * H + H) - H / (double) 4);


                    int reclastY = (int) Math.round(recfirstY - H + (H / (double) 2));

                    //       Log.wtf("W H RecFirstY", W + " " + H + " " + recfirstY +" " +(recfirstY/0.25));

                    c.drawRect(recfirstX, recfirstY, reclastX, reclastY, p);
                    //  c.drawRect(Math.round(recfirstX+20), Math.round(recfirstY-50), Math.round(reclastX+60), Math.round(reclastY+50), p);

                }


            }
        }
    }
    public void reDraw() {
        Canvas c = getHolder().lockCanvas();
        if (c != null) {
            this.onDraw(c);
            getHolder().unlockCanvasAndPost(c);
        }
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        InstrumentPart ip = app.getInstrument();

        int H=h/13;
        int W=w/8;
        int x = (int) event.getX();
        int y = (int) event.getY();
        int action = event.getAction();
        int X= (int) Math.round((x-W/(double)2)/(double)W)-1;
        int Y= (int) Math.round((y-H/(double)2)/(double)H)-1;
        switch (action) {
            case MotionEvent.ACTION_DOWN: {
                firstX = app.getInstantDebut()+X;
                firstY = 11-Y;
                reDraw();
                return true;

            }
            case MotionEvent.ACTION_MOVE: {

                lastX = app.getInstantDebut()+X;
                lastY = Y;
                // reDraw();

                return true;

            }

            case MotionEvent.ACTION_UP:{
                lastX = app.getInstantDebut()+X;
                lastY = Y;
                longueur=lastX - firstX;
                if(longueur<0)
                    longueur=0;
                //   longueur = lastX - firstX;
                boolean add=true;


                for(Note note:ip.getNotes()) {
                    if(firstX >=app.getInstantDebut() && firstY<12 && firstY>=0 && note.getInstant() == firstX && note.getName().toString().equals(NoteName.ofNum(firstY).toString())) {
                        try {
                            ip.removeNote(firstX, NoteName.ofNum(firstY));
                        }
                        catch(ConcurrentModificationException e) {
                            Log.wtf("ConcurrentModificationException", "Exception survenue");
                        }
                        add=false;
                        break;
                    }

                }

                if(add && firstX >=app.getInstantDebut() && firstY<12  && firstY>=0) {

                    Log.wtf("firstY",""+firstY);

                    ip.addNote(firstX, NoteName.ofNum(firstY), 1 + longueur);
                }
                //  }

                reDraw();
                return true;
            }
            default:
                onActionMove=false;

        }

        return false;
    }

}



