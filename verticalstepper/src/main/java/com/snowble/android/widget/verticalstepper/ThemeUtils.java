package com.snowble.android.widget.verticalstepper;

import android.content.res.Resources;
import android.util.TypedValue;

class ThemeUtils {
    static int getResolvedAttributeData(Resources.Theme theme, int attr, int defaultData) {
        TypedValue value = new TypedValue();
        theme.resolveAttribute(attr, value, false);
        int resolvedAttributeData;
        if (value.type != TypedValue.TYPE_NULL) {
            resolvedAttributeData = value.data;
        } else {
            resolvedAttributeData = defaultData;
        }
        return resolvedAttributeData;
    }
}
