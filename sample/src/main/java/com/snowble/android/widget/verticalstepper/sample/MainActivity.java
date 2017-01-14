package com.snowble.android.widget.verticalstepper.sample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

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
        final EditText editText = (EditText) findViewById(R.id.required_text);
        stepper.setStepValidator(new VerticalStepper.StepValidator() {
            @Override
            public String validate(View v) {
                if (v.getId() == R.id.step_with_requirement) {
                    Editable text = editText.getText();
                    if (TextUtils.isEmpty(text)) {
                        return "Text cannot be empty";
                    }
                    stepper.setStepSummary(R.id.step_with_requirement, "Satisfied requirement: " + text);
                }
                return null;
            }
        });
    }
}
