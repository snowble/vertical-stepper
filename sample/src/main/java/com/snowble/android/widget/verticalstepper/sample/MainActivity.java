package com.snowble.android.widget.verticalstepper.sample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.snowble.android.widget.verticalstepper.StepValidator;
import com.snowble.android.widget.verticalstepper.ValidationResult;
import com.snowble.android.widget.verticalstepper.VerticalStepper;

public class MainActivity extends AppCompatActivity {

    private VerticalStepper stepper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        stepper = (VerticalStepper) findViewById(R.id.activity_main);
        addStepValidation();
    }

    private void addStepValidation() {
        final EditText reqText = (EditText) findViewById(R.id.required_text);
        final EditText optText = (EditText) findViewById(R.id.optional_text);
        stepper.setStepValidator(new StepValidator() {
            @Override
            public ValidationResult validate(View v, boolean isOptional) {
                if (v.getId() == R.id.step_with_requirement) {
                    Editable text = reqText.getText();
                    if (TextUtils.isEmpty(text)) {
                        return new ValidationResult("Text cannot be empty");
                    }
                    stepper.setStepSummary(R.id.step_with_requirement, "Satisfied requirement: " + text);
                } else if (v.getId() == R.id.optional_step) {
                    Editable text = optText.getText();
                    if (TextUtils.isEmpty(text)) {
                        return ValidationResult.VALID_INCOMPLETE_RESULT;
                    } else if (text.charAt(text.length() - 1) != '.'){
                        return new ValidationResult("Text must end in a period");
                    } else {
                        stepper.setStepSummary(R.id.optional_step, "Satisfied requirement ending in period.");
                        return ValidationResult.VALID_COMPLETE_RESULT;
                    }
                }
                return ValidationResult.VALID_COMPLETE_RESULT;
            }
        });
    }
}
