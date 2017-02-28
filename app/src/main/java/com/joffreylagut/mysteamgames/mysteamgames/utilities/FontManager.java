package com.joffreylagut.mysteamgames.mysteamgames.utilities;

import android.content.Context;
import android.graphics.Typeface;

/**
 * Created by Joffrey on 20/02/2017.
 */

public class FontManager {
    public static final String ROOT = "fonts/";
    public static final String CUSTOMFONTICON = ROOT + "custom_fonticon_pack.ttf";

    public static Typeface getTypeface(Context context, String font) {
        return Typeface.createFromAsset(context.getAssets(), font);
    }
}
