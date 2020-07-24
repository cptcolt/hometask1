package ru.skillbranch.gameofthrones

import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import android.widget.SearchView
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.animation.doOnEnd
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.fragment.app.FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.fragment_character_screen.*
import kotlinx.android.synthetic.main.fragment_character_screen.toolbar
import kotlinx.android.synthetic.main.fragment_charters_list_screen.*
import java.lang.Double.max
import kotlin.math.hypot

class ChartersListScreen : Fragment() {
    lateinit var colors: Array<Int>
    lateinit var pagerAdapter: HousePagerAdapter
    var currentColor : Int = -1


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        colors = requireActivity().run {
            arrayOf(
                getColor(R.color.stark_primary),
                getColor(R.color.lannister_primary),
                getColor(R.color.targaryen_primary),
                getColor(R.color.baratheon_primary),
                getColor(R.color.greyjoy_primary),
                getColor(R.color.martel_primary),
                getColor(R.color.tyrel_primary)
            )
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.search_menu, menu)
        (menu.findItem(R.id.app_bar_search)?.actionView as SearchView).queryHint = "Search"
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_charters_list_screen, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (requireActivity() as MainActivity).setSupportActionBar(toolbar)
        toolbar.title = getString(R.string.app_name)
        if (currentColor != -1) appbar.setBackgroundColor(currentColor)
        pagerAdapter = HousePagerAdapter(childFragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT)
        view_pager.adapter = pagerAdapter

        with (tabs) {
            setupWithViewPager(view_pager)
            addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabReselected(p0: TabLayout.Tab?) {

                }

                override fun onTabUnselected(p0: TabLayout.Tab?) {

                }

                override fun onTabSelected(p0: TabLayout.Tab) {
                    val position: Int = p0.position
                    if ((appbar.background as ColorDrawable).color != colors[position]) {
                        val rect = Rect()
                        val tabView = p0.view as View

                        tabView.postDelayed({
                            tabView.getGlobalVisibleRect(rect)
                            appBarReveal(position, rect.centerX(), rect.centerY())
                        }, 300)


                    }
                }

            })
        }
    }

    private fun appBarReveal(position: Int, centerX: Int, centerY: Int) {
        val endRadius = Math.max(hypot(centerX.toDouble(), centerY.toDouble()), hypot(appbar.width.toDouble()-centerX.toDouble(), centerY.toDouble()))

        with (reveal_view) {
            visibility = View.VISIBLE
            setBackgroundColor(colors[position])
        }

        ViewAnimationUtils.createCircularReveal(reveal_view, centerX, centerY, 0f, endRadius.toFloat()).apply {
            doOnEnd {
                appbar?.setBackgroundColor(colors[position])
                reveal_view?.visibility = View.INVISIBLE
            }
            start()
        }

        currentColor = colors[position]


    }

    class HousePagerAdapter(fragmentManager: FragmentManager, behavior: Int): FragmentStatePagerAdapter(fragmentManager, behavior) {
        override fun getItem(position: Int): Fragment =
            HouseFragment.newInstance(HouseType.values()[position].title)

        override fun getCount(): Int = HouseType.values().size

        override fun getPageTitle(position: Int): CharSequence? {
            super.getPageTitle(position)
            return HouseType.values()[position].title
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ChartersListScreen()
    }
}


