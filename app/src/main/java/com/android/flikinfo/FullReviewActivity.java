package com.android.flikinfo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

public class FullReviewActivity extends AppCompatActivity {

    private TextView fullReviewTextView;
    private TextView authorTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_review);

        fullReviewTextView = findViewById(R.id.tv_review_full);
        authorTextView = findViewById(R.id.tv_author_full);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.review_activity_title);

        Intent intent = getIntent();

        if (intent != null) {
            if (intent.hasExtra(FlikInfoConstants.IntentConstants.INTENT_KEY_REVIEW)) {
                fullReviewTextView.setText(intent.getStringExtra(FlikInfoConstants.IntentConstants.INTENT_KEY_REVIEW));
            }
            if (intent.hasExtra(FlikInfoConstants.IntentConstants.INTENT_KEY_REVIEW_AUTHOR)) {
                authorTextView.setText("-" + intent.getStringExtra(FlikInfoConstants.IntentConstants.INTENT_KEY_REVIEW_AUTHOR));
            }
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == android.R.id.home) {
            this.finish();
        }
        return true;
    }
}
