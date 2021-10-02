package com.silentnuke.bits.wallet.feature.carddetails

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.silentnuke.bits.wallet.databinding.CardDetailsFragmentBinding
import com.silentnuke.bits.wallet.util.setupRefreshLayout
import com.silentnuke.bits.wallet.util.setupSnackbar
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CardDetailsFragment : Fragment() {
    private lateinit var viewDataBinding: CardDetailsFragmentBinding

    private val args: CardDetailsFragmentArgs by navArgs()

    private val viewModel by viewModels<CardDetailsViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewDataBinding = CardDetailsFragmentBinding.inflate(inflater, container, false).apply {
            viewmodel = viewModel
            lifecycleOwner = viewLifecycleOwner
        }
        viewModel.start(args.cardId)

        return viewDataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.setupSnackbar(this, viewModel.snackbarText, Snackbar.LENGTH_SHORT)
        this.setupRefreshLayout(viewDataBinding.refreshLayout)
    }

}
