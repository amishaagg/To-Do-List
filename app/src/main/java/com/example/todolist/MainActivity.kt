package com.example.todolist

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.annotation.ColorInt
import androidx.appcompat.widget.SearchView
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.InvalidationTracker
import androidx.room.Room
import com.example.todolist.R.*
import io.reactivex.Observer
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_task.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList


class MainActivity : AppCompatActivity() {

    val list= arrayListOf<ToDoModel>()
    val adapter=ToDoAdapter(list)

    val db by lazy{
        AppDataBase.getDatabase(this)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layout.activity_main)
        setSupportActionBar(toolbar)

        rvToDos.layoutManager=LinearLayoutManager(this)
        rvToDos.adapter=adapter
        initSwipe()
        db.tododao().getTask().observe(this, androidx.lifecycle.Observer {
            if (!it.isNullOrEmpty()) {
                list.clear()
                list.addAll(it)
                adapter.notifyDataSetChanged()
            }else{
                list.clear()
                adapter.notifyDataSetChanged()
            }
        })


    }



    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu,menu)
        //SEARCH FUNCTIONALITY
        val item= menu.findItem(R.id.search)
        val searchView=item.actionView as SearchView
        item.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
            override fun onMenuItemActionExpand(p0: MenuItem?): Boolean {
                displayTodo()
                return true
            }

            override fun onMenuItemActionCollapse(p0: MenuItem?): Boolean {
                displayTodo()
                return true
            }

        })
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if(!newText.isNullOrEmpty()){
                    displayTodo(newText)
                }
                return true
            }

        })

        return super.onCreateOptionsMenu(menu)
    }



    //for searching
    private fun displayTodo(newText: String="") {
        db.tododao().getTask().observe(this, androidx.lifecycle.Observer {
            if (list.isNotEmpty()) {
                list.clear()
                list.addAll(
                    it.filter { toDoModel ->
                        toDoModel.title.contains(newText,true)
                    }
                )
                adapter.notifyDataSetChanged()
            }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId== id.history)
        startActivity(Intent(this,HistoryActivity::class.java))
        return super.onOptionsItemSelected(item)
    }

    fun openNewTask(view: View) {
        view.setOnClickListener {
            startActivity(Intent(this,TaskActivity::class.java))
        }
    }

    private fun initSwipe(){
        val simpleitemtouchcallback= object : ItemTouchHelper.SimpleCallback(0,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT){
            /**
             * Called when ItemTouchHelper wants to move the dragged item from its old position to
             * the new position.
             */
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean =false
            /**
             * Called when a ViewHolder is swiped by the user.
             *@param viewHolder The ViewHolder which has been swiped by the user.
             * @param direction  The direction to which the ViewHolder is swiped. It is one of
             * [.UP], [.DOWN],
             * [.LEFT] or [.RIGHT]. If your
             * [.getMovementFlags]
             * method
             * returned relative flags instead of [.LEFT] / [.RIGHT];
             * `direction` will be relative as well. ([.START] or [                   ][.END]).
             */
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position=viewHolder.adapterPosition
                //left swipe==delete
                if (direction == ItemTouchHelper.LEFT) {
                    GlobalScope.launch(Dispatchers.IO) {
                        db.tododao().deleteTask(adapter.getItemId(position))
                    }
                } else if (direction == ItemTouchHelper.RIGHT) {
                    GlobalScope.launch(Dispatchers.IO) {
                        db.tododao().finishTask(adapter.getItemId(position))
                    }
                }

            }


            @SuppressLint("ResourceType")
            override fun onChildDraw(
                canvas: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {
                //from here code written by me starts
                if(actionState==ItemTouchHelper.ACTION_STATE_SWIPE){
                    val itemView=viewHolder.itemView
                    //Paint helps paint the color when swiping
                    val paint= android.graphics.Paint()
                    val icon:Bitmap
                    //LEFT TO RIGHT SWIPE
                    if(dX>0){
                        icon=BitmapFactory.decodeResource(resources, mipmap.ic_check_white_png)
                        val color = android.graphics.Color.parseColor("#388E3C")
                        paint.color= color
                        //right==left+dX, cause dont wanna swipe till right all at once
                        canvas.drawRect(itemView.left.toFloat(),itemView.top.toFloat(),
                            itemView.left.toFloat()+dX,itemView.bottom.toFloat(),paint
                        )
                        canvas.drawBitmap(icon,itemView.left.toFloat(),
                        itemView.top.toFloat()+(itemView.bottom.toFloat()-itemView.top.toFloat()-icon.height.toFloat())/2,
                            paint)

                    }
                    else{
                        icon=BitmapFactory.decodeResource(resources, mipmap.ic_delete_white_png)
                        val color = android.graphics.Color.parseColor("#D32F2F")

                        paint.color = color

                        canvas.drawRect(itemView.right.toFloat()+dX,itemView.top.toFloat(),
                            itemView.right.toFloat(),itemView.bottom.toFloat(),paint
                        )
                        canvas.drawBitmap(
                            icon,
                            itemView.right.toFloat() - icon.width,
                            itemView.top.toFloat() + (itemView.bottom.toFloat() - itemView.top.toFloat() - icon.height.toFloat()) / 2,
                            paint
                        )

                    }
                    viewHolder.itemView.translationX=dX
                }

                //Here my code ends
                //'else' also written by me
                else {
                    super.onChildDraw(
                        canvas,
                        recyclerView,
                        viewHolder,
                        dX,
                        dY,
                        actionState,
                        isCurrentlyActive
                    )
                }
            }

        }


        val itemTouchHelper=ItemTouchHelper(simpleitemtouchcallback)
        itemTouchHelper.attachToRecyclerView(rvToDos)
    }
}