package com.snowble.android.widget.verticalstepper;

import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.lang.annotation.Retention;

import static java.lang.annotation.RetentionPolicy.SOURCE;

public class ValidationResult {
    @Retention(SOURCE)
    @IntDef({VALID_COMPLETE, VALID_INCOMPLETE, INVALID})
    public @interface Result {}
    /**
     * For use with a step that is both valid and complete. This can be used for both optional and required steps.
     *
     * @see #VALID_COMPLETE_RESULT
     */
    public static final int VALID_COMPLETE = 1;
    /**
     * For use with a step that is both valid but incomplete. Use this for an optional step that's incomplete but
     * that isn't considered an error.
     *
     * @see #VALID_INCOMPLETE_RESULT
     */
    public static final int VALID_INCOMPLETE = 2;
    /**
     * For use with a step that has an error of some kind. This can be used for both optional and required steps.
     */
    public static final int INVALID = 3;

    /**
     * @see #VALID_COMPLETE
     */
    public static ValidationResult VALID_COMPLETE_RESULT = new ValidationResult(VALID_COMPLETE, null);

    /**
     * @see #VALID_INCOMPLETE
     */
    public static ValidationResult VALID_INCOMPLETE_RESULT = new ValidationResult(VALID_INCOMPLETE, null);

    private final int result;
    private final String error;

    /**
     * Constructs an invalid result.
     *
     * @param error error to be used. Should not be null.
     *
     * @see #ValidationResult(int, String)
     * @see #VALID_COMPLETE_RESULT
     * @see #VALID_INCOMPLETE_RESULT
     */
    public ValidationResult(@NonNull String error) {
        this(INVALID, error);
    }

    /**
     * Constructs a validation result.
     *
     * @param result one of {@link Result}.
     * @param error error to be used if {@code result} is {@link #INVALID}.
     *
     * @see #ValidationResult(String)
     * @see #VALID_COMPLETE_RESULT
     * @see #VALID_INCOMPLETE_RESULT
     */
    public ValidationResult(@Result int result, @Nullable String error) {
        this.result = result;
        this.error = error;
    }

    public @Result int getResult() {
        return result;
    }

    public String getError() {
        return error;
    }
}
