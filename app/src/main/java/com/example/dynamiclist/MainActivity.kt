package com.example.dynamiclist

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager.widget.ViewPager
import com.example.dynamiclist.databinding.ActivityMainBinding
import com.example.dynamiclist.databinding.NewItemBottomSheetBinding
import com.google.android.material.bottomsheet.BottomSheetDialog

class MainActivity : AppCompatActivity() {

    private lateinit var activityBinding: ActivityMainBinding

    private val viewModel: DynamicListViewModel by viewModels<DynamicListViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(activityBinding.root)

        activityBinding.apply {

            rvImageList.isNestedScrollingEnabled = false

            vpImageList.adapter = ImageListAdapter(viewModel.listOfImages)

            val dotscount: Int = viewModel.listOfImages.size
            val dots = arrayListOf<ImageView>()

            (0 until dotscount).forEachIndexed { _, _ ->
                val dot = ImageView(this@MainActivity).apply {
                    setImageDrawable(
                        ContextCompat.getDrawable(
                            this@MainActivity,
                            R.drawable.unselected_dot
                        )
                    )
                }

                val params = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    setMargins(8, 0, 8, 0)
                }

                llSliderDots.addView(dot, params)
                dots.add(dot)
            }

            dots[0].setImageDrawable(ContextCompat.getDrawable(
                    this@MainActivity, R.drawable.selected_dot
            ))

            vpImageList.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {

                override fun onPageScrolled(
                    position: Int,
                    positionOffset: Float,
                    positionOffsetPixels: Int,
                ) {
                    //Will be implemented later
                }

                override fun onPageSelected(position: Int) {
                    for (i in 0 until dotscount) {
                        dots[i].setImageDrawable(
                            ContextCompat.getDrawable(
                                this@MainActivity,
                                R.drawable.unselected_dot
                            )
                        )
                    }
                    dots[position].setImageDrawable(
                        ContextCompat.getDrawable(
                            this@MainActivity,
                            R.drawable.selected_dot
                        )
                    )
                }

                override fun onPageScrollStateChanged(state: Int) {
                    /** Do Nothing */
                }
            })

            nsvUIItems.setOnScrollChangeListener { _, _, scrollY, _, _ ->
                handleScroll(scrollY)
            }

            atvSearch.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(charSequence: CharSequence?, start: Int, count: Int, after: Int) {
                    // No action needed
                }

                override fun onTextChanged(charSequence: CharSequence?, start: Int, before: Int, count: Int) {
                    val query = charSequence.toString()
                    filterList(query)
                }

                override fun afterTextChanged(editable: Editable?) {
                    // No action needed
                }
            })

            rvImageList.apply {
                layoutManager = LinearLayoutManager(this@MainActivity, LinearLayoutManager.VERTICAL, false)
                adapter = ListItemAdapter(viewModel.listOfItems)
                setOnTouchListener { _, event ->
                    activityBinding.nsvUIItems.requestDisallowInterceptTouchEvent(true)
                    false
                }
            }

            fabAction.setOnClickListener {
                showNewItemBottomSheet()
            }

        }

    }

    private fun showNewItemBottomSheet() {
        val view = NewItemBottomSheetBinding.inflate(LayoutInflater.from(this))
        val dialog = BottomSheetDialog(this)
        dialog.setContentView(view.root)

        // Generate statistics
        val itemCount = viewModel.listOfItems.size // Replace with actual page count logic
        val characterStats = calculateCharacterFrequency(viewModel.listOfItems.map { it.title }) // Replace with actual list

        // Update the TextViews
        view.tvItemCount.text = "List 1 ($itemCount items)"
        view.tvTopCharacters.text = characterStats.joinToString("\n") { "${it.first} = ${it.second}" }

        dialog.show()
    }

    private fun calculateCharacterFrequency(list: List<String>): List<Pair<Char, Int>> {
        val frequencyMap = mutableMapOf<Char, Int>()

        list.forEach { item ->
            item.forEach { char ->
                if (char.isLetter()) { // Only count letters
                    frequencyMap[char] = frequencyMap.getOrDefault(char, 0) + 1
                }
            }
        }

        // Sort by frequency in descending order and take the top 3
        return frequencyMap.entries
            .sortedByDescending { it.value }
            .take(3)
            .map { it.key to it.value }
    }

    fun filterList(query: String) {
        val filteredList = viewModel.listOfItems.filter { item ->
            item.title.contains(query, ignoreCase = true) || item.desc.contains(query, ignoreCase = true)
        } as ArrayList<ListDataModel>
        updateRecyclerView(filteredList)
    }

    fun updateRecyclerView(filteredList: ArrayList<ListDataModel>) {
        (activityBinding.rvImageList.adapter as ListItemAdapter).updateList(filteredList)
    }

    private fun handleScroll(scrollY: Int) {

        val targetView = activityBinding.atvSearch
        val targetViewTop = targetView.top - 40

        // Check if the AutoCompleteTextView is above the visible area
        if (scrollY >= targetViewTop) {
            // Scroll to keep the AutoCompleteTextView in view
            activityBinding.nsvUIItems.smoothScrollTo(0, targetViewTop)

            // Dynamically adjust RecyclerView height to match_parent - 20dp
            val layoutParams = activityBinding.rvImageList.layoutParams
            val parentHeight = activityBinding.nsvUIItems.height // Total height of NestedScrollView
            val density = activityBinding.root.context.resources.displayMetrics.density
            val offset = (90 * density).toInt() // Convert 20dp to pixels
            layoutParams.height = parentHeight - offset
            activityBinding.rvImageList.layoutParams = layoutParams
        }
    }

}