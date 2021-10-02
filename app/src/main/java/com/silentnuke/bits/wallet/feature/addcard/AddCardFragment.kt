package com.silentnuke.bits.wallet.feature.addcard

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.silentnuke.bits.wallet.ADD_RESULT_OK
import com.silentnuke.bits.wallet.EventObserver
import com.silentnuke.bits.wallet.R
import com.silentnuke.bits.wallet.databinding.AddCardFragmentBinding
import com.silentnuke.bits.wallet.util.setupSnackbar
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class AddCardFragment : Fragment() {

    private lateinit var viewDataBinding: AddCardFragmentBinding

    private val viewModel by viewModels<AddCardViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewDataBinding = AddCardFragmentBinding.inflate(inflater, container, false).apply {
            viewmodel = viewModel
            lifecycleOwner = viewLifecycleOwner
        }
        setHasOptionsMenu(true)
        return viewDataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupSnackbar()
        setupNavigation()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_done -> {
                viewModel.saveCard()
                true
            }
            else -> false
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.card_add_fragment_menu, menu)
    }

    private fun setupSnackbar() {
        view?.setupSnackbar(this, viewModel.snackbarText, Snackbar.LENGTH_SHORT)
    }

    private fun setupNavigation() {
        viewModel.cardCreatedEvent.observe(viewLifecycleOwner, EventObserver {
            val action = AddCardFragmentDirections
                .actionAddCardFragmentToCardsFragment(ADD_RESULT_OK)
            findNavController().navigate(action)
        })
    }
}
