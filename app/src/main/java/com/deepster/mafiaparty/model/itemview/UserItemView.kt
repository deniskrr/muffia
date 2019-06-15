package com.deepster.mafiaparty.model.itemview

import com.deepster.mafiaparty.R
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.user.view.*

class UserItemView(val user: String) : Item<ViewHolder>() {
    override fun getLayout(): Int = R.layout.user

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.text_player_name.text = user
    }
}