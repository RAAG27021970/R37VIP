package com.r37vip.app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class MainActivity : AppCompatActivity() {
    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewPager = findViewById(R.id.viewPager)
        tabLayout = findViewById(R.id.tabLayout)

        // Mostrar la página de bienvenida primero
        showWelcomeScreen()
    }

    fun showWelcomeScreen() {
        // Ocultar las pestañas y el ViewPager
        tabLayout.visibility = ViewPager2.GONE
        viewPager.visibility = ViewPager2.GONE

        // Mostrar el fragmento de bienvenida
        supportFragmentManager.beginTransaction()
            .replace(R.id.mainContainer, WelcomeFragment.newInstance())
            .commit()
    }

    fun showMainContent() {
        // Ocultar el fragmento de bienvenida
        val welcomeFragment = supportFragmentManager.findFragmentById(R.id.mainContainer)
        if (welcomeFragment is WelcomeFragment) {
            supportFragmentManager.beginTransaction()
                .remove(welcomeFragment)
                .commit()
        }

        // Mostrar las pestañas y el ViewPager
        tabLayout.visibility = TabLayout.VISIBLE
        viewPager.visibility = ViewPager2.VISIBLE

        // Set up the ViewPager with the sections adapter
        val pagerAdapter = ViewPagerAdapter(this)
        viewPager.adapter = pagerAdapter

        // Connect the TabLayout with the ViewPager
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "Input"
                1 -> "Grid"
                2 -> "Table"
                3 -> "PLATE"
                else -> null
            }
        }.attach()
    }
}

private class ViewPagerAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {
    override fun getItemCount(): Int = 4

    override fun createFragment(position: Int): Fragment = when (position) {
        0 -> InputFragment.newInstance()
        1 -> HistoryGridFragment.newInstance()
        2 -> TableFragment.newInstance()
        3 -> PlateFragment.newInstance()
        else -> throw IllegalArgumentException("Invalid position $position")
    }
}