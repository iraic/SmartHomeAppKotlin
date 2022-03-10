package com.example.smarthomeappkotlin

import android.view.View

interface RecyclerViewOnItemClickListener {
    fun onClick(v: View?, position: Int)
    fun onClickEdit(v: View?, position: Int)
    fun onClickDel(v: View?, position: Int)
}
