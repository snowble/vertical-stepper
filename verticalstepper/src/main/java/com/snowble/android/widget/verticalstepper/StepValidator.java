package com.snowble.android.widget.verticalstepper;

import android.view.View;

public interface StepValidator {
    /**
     * Validates the step on completion
     *
     * @param v the view of the step being completed.
     * @param isOptional whether or not this step is considered optional.
     *
     * @return the result of validation. This will be used to update the visible state of the step
     *         and show errors, if any.
     */
    ValidationResult validate(View v, boolean isOptional);
}
