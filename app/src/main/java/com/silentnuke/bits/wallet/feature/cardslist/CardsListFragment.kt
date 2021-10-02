package com.silentnuke.bits.wallet.feature.cardslist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.silentnuke.bits.wallet.EventObserver
import com.silentnuke.bits.wallet.R
import com.silentnuke.bits.wallet.databinding.CardsFragmentBinding
import com.silentnuke.bits.wallet.util.setupRefreshLayout
import com.silentnuke.bits.wallet.util.setupSnackbar
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CardsListFragment : Fragment() {

    private val viewModel by viewModels<CardsListViewModel>()
    private val args: CardsListFragmentArgs by navArgs()

    private lateinit var viewDataBinding: CardsFragmentBinding
    private lateinit var cardsListAdapter: CardsListAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewDataBinding = CardsFragmentBinding.inflate(inflater, container, false).apply {
            viewmodel = viewModel
            lifecycleOwner = viewLifecycleOwner
        }
        setHasOptionsMenu(true)
        return viewDataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupSnackbar()
        setupListAdapter()
        setupRefreshLayout(viewDataBinding.refreshLayout, viewDataBinding.cardsList)
        setupNavigation()
    }

    override fun onOptionsItemSelected(item: MenuItem) =
        when (item.itemId) {
            R.id.menu_add -> {
                viewModel.addNewCard()
                true
            }
            else -> false
        }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.cards_fragment_menu, menu)
    }

    private fun setupNavigation() {
        viewModel.openCardEvent.observe(viewLifecycleOwner, EventObserver {
            openCardDetails(it)
        })
        viewModel.newCardEvent.observe(viewLifecycleOwner, EventObserver {
            navigateToAddNewCard()
        })
    }

    private fun setupSnackbar() {
        view?.setupSnackbar(viewLifecycleOwner, viewModel.snackbarText, Snackbar.LENGTH_SHORT)
        arguments?.let {
            viewModel.showResultMessage(args.userMessage)
        }
    }

    private fun navigateToAddNewCard() {
        val action = CardsListFragmentDirections
            .actionCardsFragmentToAddCardFragment()
        findNavController().navigate(action)
    }

    private fun openCardDetails(cardId: String) {
        val action = CardsListFragmentDirections.actionCardsFragmentToCardDetailsFragment(cardId)
        findNavController().navigate(action)
    }

    private fun setupListAdapter() {
        cardsListAdapter = CardsListAdapter(viewModel)
        viewDataBinding.cardsList.adapter = cardsListAdapter

    }
}
