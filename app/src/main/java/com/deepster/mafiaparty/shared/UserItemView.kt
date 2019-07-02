package com.deepster.mafiaparty.shared

import com.deepster.mafiaparty.R
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.user.view.*

class UserItemView(val user: String, val role: Role? = null) : Item<ViewHolder>() {
    override fun getLayout(): Int = R.layout.user

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.text_player_name.text = user
        if (role != null) {
            viewHolder.itemView.text_player_role.text = role.name
        } else {
            viewHolder.itemView.text_player_role.text = ""
        }
    }
}