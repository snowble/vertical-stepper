package com.snowble.android.verticalstepper;

import android.app.Activity;

import org.robolectric.Robolectric;

public class RobolectricTestUtils {

    public static VerticalStepper.LayoutParams createTestLayoutParams(Activity activity,
                                                                      int leftMargin, int topMargin,
                                                                      int rightMargin, int bottomMargin) {
        VerticalStepper.LayoutParams lp = createTestLayoutParams(activity);
        lp.leftMargin = leftMargin;
        lp.topMargin = topMargin;
        lp.rightMargin = rightMargin;
        lp.bottomMargin = bottomMargin;

        return lp;
    }

    public static VerticalStepper.LayoutParams createTestLayoutParams(Activity activity) {
        Robolectric.AttributeSetBuilder attributeSetBuilder = Robolectric.buildAttributeSet();
        attributeSetBuilder.addAttribute(android.R.attr.layout_width, "match_parent");
        attributeSetBuilder.addAttribute(android.R.attr.layout_height, "wrap_content");
        attributeSetBuilder.addAttribute(R.attr.step_title, "title");

        VerticalStepper.LayoutParams lp =
                new VerticalStepper.LayoutParams(activity, attributeSetBuilder.build());

        return lp;
    }
}
