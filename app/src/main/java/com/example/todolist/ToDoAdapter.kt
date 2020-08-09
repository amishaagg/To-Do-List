package com.example.todolist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_task.*
import kotlinx.android.synthetic.main.item_todo.view.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.random.Random

class ToDoAdapter( val TodoList:List<ToDoModel>): RecyclerView.Adapter<ToDoAdapter.TodoViewHolder>(){
    class TodoViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        fun bind(todoModel: ToDoModel) {
            with(itemView){
                val colors=resources.getIntArray(R.array.random_color)
                val randomcol=colors[Random.nextInt(colors.size)]
                viewColorTag.setBackgroundColor(randomcol)
                tvTitle.text=todoModel.title
                tvTask.text=todoModel.description
                tvshowCategory.text=todoModel.category
                updateTime(todoModel.time)
                updateDate(todoModel.date)

            }
        }
        private fun updateTime(time:Long) {
            //2:30 am
            val myFormat="hh:mm a"
            val sdf= SimpleDateFormat(myFormat)
            itemView.tvShowTime.text=sdf.format(Date( time))

        }
        private fun updateDate(time:Long) {
            val myFormat="EEE, d MMM YYYY"
            val sdf=SimpleDateFormat(myFormat)
            itemView.tvShowDate.text=sdf.format(Date(time))

        }


    }

    /**
     * Called when RecyclerView needs a new [ViewHolder] of the given type to represent
     * an item.
     *
     *
     * This new ViewHolder should be constructed with a new View that can represent the items
     * of the given type. You can either create a new View manually or inflate it from an XML
     * layout file.
     *
     *
     * The new ViewHolder will be used to display items of the adapter using
     * [.onBindViewHolder]. Since it will be re-used to display
     * different items in the data set, it is a good idea to cache references to sub views of
     * the View to avoid unnecessary [View.findViewById] calls.
     *
     * @param parent The ViewGroup into which the new View will be added after it is bound to
     * an adapter position.
     * @param viewType The view type of the new View.
     *
     * @return A new ViewHolder that holds a View of the given view type.
     * @see .getItemViewType
     * @see .onBindViewHolder
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.item_todo,parent,false)
        return TodoViewHolder(itemView)
    }

    override fun getItemId(position: Int): Long {
        return TodoList[position].id
    }

    /**
     * Returns the total number of items in the data set held by the adapter.
     *
     * @return The total number of items in this adapter.
     */
    override fun getItemCount(): Int {
        return TodoList.size
    }

    /**
     * Called by RecyclerView to display the data at the specified position. This method should
     * update the contents of the [ViewHolder.itemView] to reflect the item at the given
     * position.
     *
     *
     * Note that unlike [android.widget.ListView], RecyclerView will not call this method
     * again if the position of the item changes in the data set unless the item itself is
     * invalidated or the new position cannot be determined. For this reason, you should only
     * use the `position` parameter while acquiring the related data item inside
     * this method and should not keep a copy of it. If you need the position of an item later
     * on (e.g. in a click listener), use [ViewHolder.getAdapterPosition] which will
     * have the updated adapter position.
     *
     * Override [.onBindViewHolder] instead if Adapter can
     * handle efficient partial bind.
     *
     * @param holder The ViewHolder which should be updated to represent the contents of the
     * item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    override fun onBindViewHolder(holder: TodoViewHolder, position: Int) {

        holder.bind(TodoList[position])

    }
}