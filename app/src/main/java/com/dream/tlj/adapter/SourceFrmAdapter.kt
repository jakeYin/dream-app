package com.dream.tlj.adapter

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.view.ViewGroup
import com.dream.tlj.fragment.SourceFrm

class SourceFrmAdapter(fm: FragmentManager, private val mFragments: List<SourceFrm>, private val mTabs: List<String>) : FragmentPagerAdapter(fm) {

    lateinit var currentFragment: SourceFrm
    override fun getItem(position: Int): Fragment {
        return mFragments[position]
    }

    override fun getCount(): Int {
        return mFragments.size
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return mTabs[position]
    }

    override fun setPrimaryItem(container: ViewGroup, position: Int, `object`: Any) {
        super.setPrimaryItem(container, position, `object`)
        currentFragment = `object` as SourceFrm
    }
}

