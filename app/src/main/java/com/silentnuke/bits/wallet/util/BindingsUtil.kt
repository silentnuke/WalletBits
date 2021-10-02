package com.silentnuke.bits.wallet.feature.cardslist

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.silentnuke.bits.wallet.data.Card
import androidx.databinding.InverseBindingAdapter

import android.text.Editable

import android.text.TextWatcher
import android.view.View

import androidx.databinding.InverseBindingListener
import com.braintreepayments.cardform.view.CardEditText
import com.braintreepayments.cardform.view.ErrorEditText


@BindingAdapter("app:items")
fun setItems(listView: RecyclerView, items: List<Card>?) {
    items?.let {
        (listView.adapter as CardsListAdapter).submitList(items)
    }
}

@BindingAdapter(value = ["errorMessageAttrChanged"])
fun setListener(editText: ErrorEditText, listener: InverseBindingListener?) {
    if (listener != null) {
        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun afterTextChanged(editable: Editable) {
                listener.onChange()
            }
        })
    }
}

@BindingAdapter("errorMessage")
fun setRealValue(editText: ErrorEditText, value: String?) {
    if (editText.errorMessage != value) {
        editText.setError(value)
    }
}

@InverseBindingAdapter(attribute = "errorMessage")
fun getRealValue(editText: ErrorEditText): String? {
    return if (editText.isValid) null else editText.errorMessage
}
