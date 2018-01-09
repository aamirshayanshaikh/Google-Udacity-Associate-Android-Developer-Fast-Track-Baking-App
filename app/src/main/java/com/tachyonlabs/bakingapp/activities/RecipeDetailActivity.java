package com.tachyonlabs.bakingapp.activities;

import com.tachyonlabs.bakingapp.R;
import com.tachyonlabs.bakingapp.fragments.RecipeDetailFragment;
import com.tachyonlabs.bakingapp.fragments.RecipeStepFragment;
import com.tachyonlabs.bakingapp.models.Recipe;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

public class RecipeDetailActivity extends AppCompatActivity implements RecipeDetailFragment.OnStepClickListener {
    private static final String TAG = RecipeDetailActivity.class.getSimpleName();
    private Recipe recipe;
    private boolean mTwoPane;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);
        Intent callingIntent = getIntent();
        recipe = callingIntent.getParcelableExtra("recipe");

        if (findViewById(R.id.recipe_detail_fragment_for_tablet) != null) {
            mTwoPane = true;
        } else {
            mTwoPane = false;
        }
    }

    public void onStepSelected(int whichStep) {
        if (mTwoPane) {
            RecipeStepFragment recipeStepFragment = (RecipeStepFragment) getSupportFragmentManager().findFragmentById(R.id.recipe_step_fragment_for_tablet);
            recipeStepFragment.updateStepNumberDescriptionAndVideo(whichStep);
        } else {
            Intent intent = new Intent(this, RecipeStepActivity.class);
            intent.putExtra("recipe", recipe);
            intent.putExtra("whichStep", whichStep);
            startActivity(intent);
        }
    }
}