package com.axmor.fsinphone.videomessages.common.extensions

import androidx.databinding.Observable
import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableList
import com.axmor.fsinphone.videomessages.ui.common.rv_adapters.UniversalAdapter
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable


fun <T : Observable> T.addOnPropertyChanged(callback: () -> Unit): Disposable =
    object : Observable.OnPropertyChangedCallback() {
        override fun onPropertyChanged(observable: Observable?, i: Int) =
            callback()
    }
        .also { addOnPropertyChangedCallback(it) }
        .let { Disposable.fromAction { removeOnPropertyChangedCallback(it) } }

fun <T : ObservableArrayList<*>> T.bindWithAdapter(adapter: UniversalAdapter): Disposable {
    adapter.setItems(this)
    val listener = object : ObservableList.OnListChangedCallback<ObservableList<*>>() {
        override fun onChanged(sender: ObservableList<*>) {
            adapter.setItems(sender)
        }

        override fun onItemRangeRemoved(
            sender: ObservableList<*>,
            positionStart: Int,
            itemCount: Int
        ) {
            adapter.setItems(sender)
        }

        override fun onItemRangeMoved(
            sender: ObservableList<*>,
            fromPosition: Int,
            toPosition: Int,
            itemCount: Int
        ) {
            adapter.setItems(sender)
        }

        override fun onItemRangeInserted(
            sender: ObservableList<*>,
            positionStart: Int,
            itemCount: Int
        ) {
            adapter.setItems(sender)
        }

        override fun onItemRangeChanged(
            sender: ObservableList<*>,
            positionStart: Int,
            itemCount: Int
        ) {
            adapter.setItems(sender)
        }
    }
    addOnListChangedCallback(listener)
    return Disposable.fromAction { removeOnListChangedCallback(listener) }
}

fun Disposable.disposeWith(cd: CompositeDisposable) = cd.add(this)

fun ObservableBoolean.toggle() = set(!get())