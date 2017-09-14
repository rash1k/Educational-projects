package ua.rash1k.twittersearches;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.WindowManager;

public class ItemDiver extends RecyclerView.ItemDecoration {

    private final Drawable divider;

    public ItemDiver(Context context) {
        int[] attr = {android.R.attr.listDivider};
        divider = context.obtainStyledAttributes(attr).getDrawable(0);
    }

    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDrawOver(c, parent, state);

        //        Вычисляем координаты х
        int leftX = parent.getPaddingLeft();
        int rightX = parent.getWidth() - parent.getPaddingRight();

        for (int i = 0; i < parent.getChildCount() - 1; i++) {
            View item = parent.getChildAt(i);

            int top = item.getBottom() +
                    ((RecyclerView.LayoutParams) item.getLayoutParams()).bottomMargin;

            int bottom = top + divider.getIntrinsicHeight();

            divider.setBounds(leftX, top, rightX, bottom);
            divider.draw(c);
        }
    }
}
