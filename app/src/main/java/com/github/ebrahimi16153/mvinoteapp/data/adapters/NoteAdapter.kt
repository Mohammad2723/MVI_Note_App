package com.github.ebrahimi16153.mvinoteapp.data.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.github.ebrahimi16153.mvinoteapp.R
import com.github.ebrahimi16153.mvinoteapp.databinding.ItemNotesBinding
import com.github.ebrahimi16153.noteapp.data.model.NoteModel
import com.github.ebrahimi16153.noteapp.utils.Constant
import javax.inject.Inject

class NoteAdapter @Inject constructor(): RecyclerView.Adapter<NoteAdapter.ViewHolder>(){
    private lateinit var context: Context
    private lateinit var binding: ItemNotesBinding
    private var movieList: List<NoteModel> = emptyList()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        binding = ItemNotesBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        context = parent.context
        return ViewHolder()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindViews(item = movieList[position])
        // for handel duplicated item
        holder.setIsRecyclable(false)
    }

    override fun getItemCount() = movieList.size

    inner class ViewHolder : RecyclerView.ViewHolder(binding.root) {

        fun bindViews(item: NoteModel) {
            binding.titleTxt.text = item.title
            binding.descTxt.text = item.description
            // category
            when(item.category){
                Constant.HOME -> binding.categoryImg.setImageResource(R.drawable.home)
                Constant.WORK -> binding.categoryImg.setImageResource(R.drawable.work)
                Constant.EDUCATION -> binding.categoryImg.setImageResource(R.drawable.education)
                Constant.HEALTH -> binding.categoryImg.setImageResource(R.drawable.healthcare)
            }
            // priority
            when(item.priority){
                Constant.HIGH -> binding.priorityColor.setBackgroundColor(ContextCompat.getColor(context,R.color.red))
                Constant.MEDIUM -> binding.priorityColor.setBackgroundColor(ContextCompat.getColor(context,R.color.green))
                Constant.LOW -> binding.priorityColor.setBackgroundColor(ContextCompat.getColor(context,R.color.aqua))
            }

            // popUp menu (Context Menu)
            binding.menuImg.setOnClickListener {menuItem ->
                val popupMenu = PopupMenu(context,menuItem)
                popupMenu.menuInflater.inflate(R.menu.popup_menu,popupMenu.menu)
                popupMenu.show()

                //click
                popupMenu.setOnMenuItemClickListener { item1 ->

                    when(item1.itemId){
                        R.id.popUp_delete -> {

                            onItemClickListener?.let {
                                it(item,Constant.DELETE)
                            }
                        }
                        R.id.popUp_edit -> {
                            onItemClickListener?.let {
                                it(item,Constant.EDIT)
                            }
                        }
                    }
                    return@setOnMenuItemClickListener true
                }
            }

            //onItemClickListener
            binding.root.setOnClickListener {
                onItemClickListener?.let { it(item,Constant.EDIT) }
            }
        }

    }


    // a differ class and function for dynamic adapter -> when data of adapter can change many times
    // whe must handel oldItems and new items, in fact we handel how adapter update new items

    fun setData(data:List<NoteModel>){

        val movieListDiffer = MovieListDiffer(oldItems = movieList , newItems = data)
        val diffUtil = DiffUtil.calculateDiff(movieListDiffer)
        movieList = data
        diffUtil.dispatchUpdatesTo(this)

    }




    class MovieListDiffer(private val oldItems: List<NoteModel>, private val newItems: List<NoteModel>) :
        DiffUtil.Callback() {

        override fun getOldListSize() = oldItems.size

        override fun getNewListSize() = newItems.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
            oldItems[oldItemPosition] === newItems[newItemPosition]

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
            oldItems[oldItemPosition] === newItems[newItemPosition]

    }



    // onClickListener
    private var onItemClickListener:((NoteModel , String) -> Unit)? = null

    fun seOnItemClickListener(listener:(NoteModel ,String) -> Unit){
        onItemClickListener = listener
    }


}