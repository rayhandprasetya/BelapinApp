package com.example.belapin;

import android.widget.Filter;

import java.util.ArrayList;

public class FilterRecipeUser extends Filter {

    private AdapterRecipeUser adapter;
    private ArrayList<ModelRecipeUser> filterList;

    public FilterRecipeUser(AdapterRecipeUser adapter, ArrayList<ModelRecipeUser> filterList) {
        this.adapter = adapter;
        this.filterList = filterList;
    }

    @Override
    protected FilterResults performFiltering(CharSequence charSequence) {
        FilterResults results = new FilterResults();
        // validate data for search

        if (charSequence != null && charSequence.length() > 0) {
            // search field not empty, searching something, perform search

            // change to upper case, to make case sensitive
            charSequence = charSequence.toString().toUpperCase();

            // store filter list
            ArrayList<ModelRecipeUser> filteredModel = new ArrayList<>();
            for (int i = 0; i < filterList.size(); i++) {
                // check, search by title and kategori
                if (filterList.get(i).getRecipeName().toUpperCase().contains(charSequence)) {
                    // add filtered data to list
                    filteredModel.add(filterList.get(i));

                }
            }
            results.count = filteredModel.size();
            results.values = filteredModel;
        }
        else {
            // search field empty, not searching, return original/all/complete list
            results.count = filterList.size();
            results.values = filterList;
        }
        return results;
    }

    @Override
    protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
        adapter.recipeAdminArrayList = (ArrayList<ModelRecipeUser>)filterResults.values;
        // refresh adapter
        adapter.notifyDataSetChanged();

    }
}