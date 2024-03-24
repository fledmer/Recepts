package com.example.recepts

import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import androidx.appcompat.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (findViewById<View>(R.id.container) != null) {
            if (savedInstanceState != null) {
                return
            }

            supportFragmentManager.beginTransaction()
                .add(R.id.container, RecipeListFragment())
                .commit()
        }
    }
}

class RecipeDetailFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_recipe_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recipe = arguments?.getParcelable<Recipe>("recipe")
        recipe?.let { showRecipeDetails(it) }
    }

    private fun showRecipeDetails(recipe: Recipe) {
        val recipeNameTextView = view?.findViewById<TextView>(R.id.recipe_name)
        val recipeDescriptionTextView = view?.findViewById<TextView>(R.id.recipe_description)
        val recipeImageView = view?.findViewById<ImageView>(R.id.recipe_image)

        recipeNameTextView?.text = recipe.name
        recipeDescriptionTextView?.text = recipe.description
        recipeImageView?.setImageResource(recipe.imageResId)
    }

    companion object {
        fun newInstance(recipe: Recipe): Fragment {
            val fragment = RecipeDetailFragment()
            val args = Bundle()
            args.putParcelable("recipe", recipe)
            fragment.arguments = args
            return fragment
        }
    }
}

class RecipeListAdapter(
    private val recipes: List<Recipe>,
    private val onItemClick: (Recipe) -> Unit
) : RecyclerView.Adapter<RecipeListAdapter.RecipeViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_recipe, parent, false)
        return RecipeViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        val recipe = recipes[position]
        holder.bind(recipe)
    }

    override fun getItemCount(): Int {
        return recipes.size
    }

    inner class RecipeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameTextView: TextView = itemView.findViewById(R.id.recipe_name)
        private val descriptionTextView: TextView = itemView.findViewById(R.id.recipe_description)
        private val imageView: ImageView = itemView.findViewById(R.id.recipe_image)

        init {
            itemView.setOnClickListener {
                onItemClick(recipes[adapterPosition])
            }
        }

        fun bind(recipe: Recipe) {
            nameTextView.text = recipe.name
            descriptionTextView.text = recipe.description
            imageView.setImageResource(recipe.imageResId)
        }
    }
}

class RecipeListFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_recipe_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recipes = getDummyRecipes()
        val recyclerView = view.findViewById<RecyclerView>(R.id.recipe_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = RecipeListAdapter(recipes) { recipe ->
            val detailFragment = RecipeDetailFragment.newInstance(recipe)
            val bundle = Bundle()
            bundle.putParcelable("recipe", recipe)
            detailFragment.arguments = bundle

            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.container, detailFragment)
                .addToBackStack(null)
                .commit()
        }
    }

    private fun getDummyRecipes(): List<Recipe> {
        return listOf(
            Recipe("Паста с соусом болоньезе", "Популярное итальянское блюдо", R.drawable.pasta),
            Recipe("Картофельное пюре", "Простой и вкусный гарнир", R.drawable.pure),
            Recipe("Цезарь с курицей", "Классический салат с курицей и соусом Цезарь", R.drawable.caesar)
        )
    }
}

data class Recipe(
    val name: String,
    val description: String,
    val imageResId: Int
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readInt()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeString(description)
        parcel.writeInt(imageResId)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Recipe> {
        override fun createFromParcel(parcel: Parcel): Recipe {
            return Recipe(parcel)
        }

        override fun newArray(size: Int): Array<Recipe?> {
            return arrayOfNulls(size)
        }
    }
}

/*
MainActivity - это точка входа в приложение. В onCreate устанавливается контент из макета activity_main.xml и добавляется фрагмент RecipeListFragment в контейнер (если он существует).

RecipeDetailFragment - это фрагмент, отображающий подробную информацию о рецепте. Он получает данные о рецепте через аргументы, которые передаются из RecipeListAdapter. В методе newInstance создается новый экземпляр фрагмента с передачей аргументов.

RecipeListAdapter - это адаптер для списка рецептов. Он связывает данные о рецептах с элементами RecyclerView и обрабатывает клики на элементах списка.

RecipeListFragment - это фрагмент, отображающий список рецептов. В onViewCreated устанавливается адаптер для RecyclerView, который содержит список рецептов. При клике на элемент списка создается новый экземпляр RecipeDetailFragment, и он заменяет текущий фрагмент.

Recipe - это класс, представляющий модель рецепта. Он реализует интерфейс Parcelable для возможности передачи данных между фрагментами.

В общем, эти классы работают вместе для создания приложения, отображающего список рецептов и позволяющего просматривать подробную информацию о каждом рецепте.

 */