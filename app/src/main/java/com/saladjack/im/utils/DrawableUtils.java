package com.saladjack.im.utils;

import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.support.v4.graphics.drawable.DrawableCompat;

/**
 * Created by SaladJack on 2017/2/8.
 */

public class DrawableUtils {
    public static Drawable tintDrawable(Drawable drawable, ColorStateList colors) {
        int r = drawable.getBounds().right;
        int b = drawable.getBounds().bottom;
        final Drawable wrappedDrawable = DrawableCompat.wrap(drawable);
        DrawableCompat.setTintList(wrappedDrawable, colors);
        wrappedDrawable.setBounds(0, 0, r, b);
        return wrappedDrawable;
    }
}
