package info.androidhive.Hamwig2_Course_Waitinglist.utils;

/**************** Created by George B. Hamwi Homework 3 *******************/

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

public class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

    /* Creating new click listener */
    private ClickListener clicklistener;

    /* Creating new gesture detector */
    private GestureDetector gestureDetector;

    /* RecyclerTouchListener  */
    public RecyclerTouchListener(Context context, final RecyclerView recycleView, final ClickListener clicklistener) {

    /* Assigning each variable clicklistener and gesture detector  */
        this.clicklistener = clicklistener;
        gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            @Override

    /* Detects clicks, on short clicks on a students information nothing happens */
            public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }

    /* Detects clicks, on long clicks the user is prompt to edit a students information or to delete the note  */
            @Override
            public void onLongPress(MotionEvent e) {
                View child = recycleView.findChildViewUnder(e.getX(), e.getY());
                if (child != null && clicklistener != null) {
                    clicklistener.onLongClick(child, recycleView.getChildAdapterPosition(child));
                }
            }
        });
    }

    @Override
    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
        View child = rv.findChildViewUnder(e.getX(), e.getY());
        if (child != null
                && clicklistener != null && gestureDetector.onTouchEvent(e)) {
            clicklistener.onClick(child, rv.getChildAdapterPosition(child));
        }
        return false;
    }

    @Override
    public void onTouchEvent(RecyclerView rv, MotionEvent e) {
    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
    }

    public interface ClickListener {
        void onClick(View view, int position);
        void onLongClick(View view, int position);
    }
}
