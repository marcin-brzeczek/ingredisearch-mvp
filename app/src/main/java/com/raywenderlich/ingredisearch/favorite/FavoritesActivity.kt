/*
 * Copyright (c) 2018 Razeware LLC
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * Notwithstanding the foregoing, you may not use, copy, modify, merge, publish,
 * distribute, sublicense, create a derivative work, and/or sell copies of the
 * Software in any work that is designed, intended, or marketed for pedagogical or
 * instructional purposes related to programming, coding, application development,
 * or information technology.  Permission for such use, copying, modification,
 * merger, publication, distribution, sublicensing, creation of derivative works,
 * or sale is expressly withheld.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.raywenderlich.ingredisearch.favorite

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.raywenderlich.ingredisearch.ChildActivity
import com.raywenderlich.ingredisearch.R
import com.raywenderlich.ingredisearch.recipe.Recipe
import com.raywenderlich.ingredisearch.recipe.RecipeAdapter
import com.raywenderlich.ingredisearch.recipe.RecipeRepository
import com.raywenderlich.ingredisearch.recipe.recipeIntent
import kotlinx.android.synthetic.main.activity_list.*
import kotlinx.android.synthetic.main.view_error.*
import kotlinx.android.synthetic.main.view_loading.*
import kotlinx.android.synthetic.main.view_noresults.*

class FavoritesActivity : ChildActivity(), FavoritePresenter.View {

    private val presenter: FavoritePresenter by lazy { FavoritePresenter(RecipeRepository.getRepository(this)) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)

        presenter.attachView(this)
        presenter.fetchFavoriteRecipes()
    }

    override fun showEmptyRecipes() {
        loadingContainer.visibility = View.GONE
        errorContainer.visibility = View.GONE
        list.visibility = View.VISIBLE
        noresultsContainer.visibility = View.VISIBLE
        noresultsTitle.text = getString(R.string.nofavorites)
    }

    override fun showRecipes(recipes: List<Recipe>) {
        loadingContainer.visibility = View.GONE
        errorContainer.visibility = View.GONE
        list.visibility = View.VISIBLE
        noresultsContainer.visibility = View.GONE

        list.layoutManager = LinearLayoutManager(this)
        list.adapter = RecipeAdapter(recipes, object : RecipeAdapter.Listener {
            override fun onClickItem(item: Recipe) {
                startActivity(recipeIntent(item.sourceUrl))
            }

            override fun onAddFavorite(item: Recipe) {
                presenter.addFavorite(item)
            }

            override fun onRemoveFavorite(item: Recipe) {
                presenter.removeFavorite(item)
                (list.adapter as RecipeAdapter).removeItem(item)
                list.adapter.notifyItemRemoved(recipes.indexOf(item))
                if (list.adapter.itemCount == 0) {
                    showEmptyRecipes()
                }
            }
        })
    }

    override fun showLoading() {
        loadingContainer.visibility = View.VISIBLE
        errorContainer.visibility = View.GONE
        list.visibility = View.GONE
        noresultsContainer.visibility = View.GONE
    }

    override fun refreshFavoriteResults(recipeIndex: Int) {
        list.adapter.notifyItemChanged(recipeIndex)
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.detachView()
    }
}
