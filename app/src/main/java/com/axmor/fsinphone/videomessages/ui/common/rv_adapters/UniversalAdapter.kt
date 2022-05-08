package com.axmor.fsinphone.videomessages.ui.common.rv_adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.axmor.fsinphone.videomessages.core.entities.chat.base.ChatMessageItem
import com.axmor.fsinphone.videomessages.databinding.ViewEmptyItemBinding
import timber.log.Timber

private const val UNKNOWN_VIEW_TYPE : Int = -1

class UniversalAdapter : RecyclerView.Adapter<UniversalAdapter.BaseViewHolder>() {
    private val items = ArrayList<Any>(10)
    private val delegates = ArrayList<UniversalAdapterDelegate<Any>>(5)

    interface UniversalAdapterDelegate<T> {
        fun getBinding(parent: ViewGroup, layoutInflater: LayoutInflater): ViewDataBinding?
        fun onBindViewHolder(baseViewHolder: BaseViewHolder, position: Int, item: T)
        fun canWorkWith(item: Any): Boolean
        fun getItemId(item: T, position: Int): Long
    }

    init {
        setHasStableIds(true)
    }

    fun setItems(items: List<Any>) {
        this.items.clear()
        this.items.addAll(items)
        notifyDataSetChanged()
    }

    fun clearItems() {
        this.items.clear()
        notifyDataSetChanged()
    }

    fun removeItem(itemToDelete: Any) {
        this.items.remove(itemToDelete)
        notifyDataSetChanged()
    }

    fun addItem(newItem: Any, position: Int) {
        if (this.items.contains(newItem)) {
            return
        }

        this.items.add(position, newItem)
        notifyDataSetChanged()
    }

    fun getPosition(item: Any): Int {
        return items.indexOf(item)
    }

    fun addDelegate(newDelegate: UniversalAdapterDelegate<out Any>): UniversalAdapter {
        delegates.add(newDelegate as UniversalAdapterDelegate<Any>)
        return this
    }

    override fun onBindViewHolder(viewHolder: BaseViewHolder, position: Int) {
        val item = items[position]

        for (currentDelegate in delegates) {
            if (currentDelegate.canWorkWith(item)) {
                currentDelegate.onBindViewHolder(viewHolder, position, item)
                viewHolder.binding.executePendingBindings()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        if(delegates.isEmpty() || viewType == UNKNOWN_VIEW_TYPE) {
            val binding = ViewEmptyItemBinding.inflate(inflater)
            val layoutParams = RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, 0)
            binding.root.layoutParams = layoutParams
            return BaseViewHolder(binding)
        }

        val currentDelegate = delegates[viewType]
        val binding = currentDelegate.getBinding(parent, inflater)
        return BaseViewHolder(binding!!)
    }

    override fun getItemViewType(position: Int): Int {
        val item = items[position]

        for (i in delegates.indices) {
            val currentDelegate = delegates[i]

            if (currentDelegate.canWorkWith(item)) {
                return i
            }
        }

        return UNKNOWN_VIEW_TYPE
    }

    override fun getItemId(position: Int): Long {
        val item = items[position]

        for (currentDelegate in delegates) {
            if (currentDelegate.canWorkWith(item)) {
                return currentDelegate.getItemId(item, position)
            }
        }

        return super.getItemId(position)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    class BaseViewHolder(val binding: ViewDataBinding) : RecyclerView.ViewHolder(binding.root)
}
