package com.raywenderlich.ingredisearch.favorite

import com.raywenderlich.ingredisearch.BasePresenter
import com.raywenderlich.ingredisearch.recipe.Recipe
import com.raywenderlich.ingredisearch.recipe.RecipeRepository

class FavoritePresenter(private val repository: RecipeRepository) : BasePresenter<FavoritePresenter.View>() {

    private var recipes: List<Recipe>? = null


    fun fetchFavoriteRecipes() {
        view?.showLoading()
        recipes = repository.getFavoriteRecipes()
        if (recipes != null && recipes!!.isNotEmpty()) {
            view?.showRecipes(recipes!!)
        } else {
            view?.showEmptyRecipes()
        }
    }


    fun addFavorite(recipe: Recipe) {
        recipe.isFavorited = true
        repository.addFavorite(recipe)
        val indexRecipe = recipes?.indexOf(recipe)
        indexRecipe?.let { view?.refreshFavoriteResults(it) }
    }

    fun removeFavorite(recipe: Recipe) {
        repository.removeFavorite(recipe)
        val indexRecipe = recipes?.indexOf(recipe)
        indexRecipe?.let { view?.refreshFavoriteResults(it) }
    }

    interface View {
        fun showLoading()
        fun showEmptyRecipes()
        fun showRecipes(recipes: List<Recipe>)
        fun refreshFavoriteResults(recipeIndex: Int)
    }
}