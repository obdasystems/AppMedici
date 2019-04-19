package com.obdasystems.pocmedici.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.support.v4.content.ContextCompat;

import com.applandeo.materialcalendarview.CalendarUtils;
import com.obdasystems.pocmedici.R;

/**
 * Created by Mateusz Kornakiewicz on 02.08.2018.
 */

public final class DrawableUtils {

    private DrawableUtils() {

    }

    public static Drawable getCircleDrawableWithText(Context context, String string) {
        Drawable background = ContextCompat.getDrawable(context, R.drawable.bg_circle_red);
        Drawable text = CalendarUtils.getDrawableText(context, string, null, android.R.color.white, 12);

        Drawable[] layers = {background, text};
        return new LayerDrawable(layers);
    }

}
