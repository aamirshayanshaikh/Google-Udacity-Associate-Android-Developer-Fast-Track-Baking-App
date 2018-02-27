package com.tachyonlabs.bakingapp.fragments;

import com.squareup.picasso.Picasso;
import com.tachyonlabs.bakingapp.R;
import com.tachyonlabs.bakingapp.activities.RecipeDetailActivity;
import com.tachyonlabs.bakingapp.adapters.RecipeStepAdapter;
import com.tachyonlabs.bakingapp.models.Recipe;
import com.tachyonlabs.bakingapp.models.RecipeIngredient;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;


public class RecipeDetailFragment extends Fragment implements RecipeStepAdapter.RecipeStepAdapterOnClickHandler {
    private static final String TAG = RecipeDetailActivity.class.getSimpleName();
    // Define a new interface OnStepClickListener that triggers a callback in the host activity
    OnStepClickListener mStepCallback;
    private RecyclerView mRecipeStepsRecyclerView;
    private RecipeStepAdapter mRecipeStepAdapter;
    private Recipe recipe;

    public RecipeDetailFragment() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // This makes sure that the host activity has implemented the callback interface
        // If not, it throws an exception
        try {
            mStepCallback = (OnStepClickListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnStepClickListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_recipe_detail, container, false);
        ImageView ivRecipeDetailPhoto = rootView.findViewById(R.id.iv_recipe_detail_photo);
        TextView tvRecipeName = rootView.findViewById(R.id.tv_recipe_detail_name);
        TextView tvRecipeServings = rootView.findViewById(R.id.tv_recipe_servings);
        TextView tvIngredientsList = rootView.findViewById(R.id.tv_ingredients_list);
        ScrollView svRecipeDetail = rootView.findViewById(R.id.sv_recipe_detail);

        // set up recyclerview and adapter to display the steps
        mRecipeStepsRecyclerView = rootView.findViewById(R.id.rv_recipe_steps);
        LinearLayoutManager stepsLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        mRecipeStepsRecyclerView.setLayoutManager(stepsLayoutManager);
        mRecipeStepAdapter = new com.tachyonlabs.bakingapp.adapters.RecipeStepAdapter(this);
        mRecipeStepsRecyclerView.setAdapter(mRecipeStepAdapter);

        if (savedInstanceState == null) {
            Intent callingIntent = getActivity().getIntent();
            recipe = callingIntent.getParcelableExtra(getString(R.string.recipe_key));

            // save selected recipe details to SharedPreferences for the widget to use
            SharedPreferences sharedPreferencesForWidget = getContext().getSharedPreferences(getString(R.string.pref_file_name), Context.MODE_PRIVATE | Context.MODE_MULTI_PROCESS);
            SharedPreferences.Editor editor = sharedPreferencesForWidget.edit();
            editor.putString(getString(R.string.recipe_name_key), recipe.getName());
            editor.putString(getString(R.string.recipe_ingredients_key), getAndFormatIngredients());
            editor.putString(getString(R.string.recipe_thumbnail_url_key), recipe.getThumbnailUrl());
            editor.commit();
        } else {
            recipe = savedInstanceState.getParcelable(getString(R.string.recipe_key));
        }

        Picasso.with(getContext())
                .load(recipe.getImageUrl())
                .placeholder(R.drawable.ic_launcher_foreground)
                .error(R.drawable.ic_launcher_foreground)
                .into(ivRecipeDetailPhoto);

        tvRecipeName.setText(recipe.getName());
        String servingsDesc = String.format("%d serving%s", recipe.getServings(), recipe.getServings() != 1 ? "s" : "");
        tvRecipeServings.setText(servingsDesc);
        tvIngredientsList.setText(getAndFormatIngredients());
        ActionBar ab = ((AppCompatActivity) getActivity()).getSupportActionBar();
        ab.setTitle(recipe.getName());

        mRecipeStepAdapter.setRecipeStepData(recipe.getSteps());

        // start with the scrollview scrolled all the way up if we're not restoring a savedInstanceState
        if (savedInstanceState == null) {
            svRecipeDetail.smoothScrollTo(0, 0);
        }

        return rootView;
    }

    private String getAndFormatIngredients() {
        RecipeIngredient[] recipeIngredients = recipe.getIngredients();
        String[] ingredients = new String[recipeIngredients.length];
        for (int i = 0; i < ingredients.length; i++) {
            ingredients[i] = recipeIngredients[i].getQuantityUnitNameString();
        }
        return TextUtils.join("\n", ingredients);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(getString(R.string.recipe_key), recipe);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onClick(int whichStep) {
        mStepCallback.onStepSelected(whichStep);
    }

    // OnStepClickListener interface, calls a method in the host activity named onStepSelected
    public interface OnStepClickListener {
        void onStepSelected(int position);
    }
}
