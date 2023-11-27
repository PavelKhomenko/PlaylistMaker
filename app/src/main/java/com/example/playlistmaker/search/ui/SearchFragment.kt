package com.example.playlistmaker.search.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.databinding.FragmentSearchBinding
import com.example.playlistmaker.player.domain.model.Track
import com.example.playlistmaker.player.ui.PlayerActivity
import com.example.playlistmaker.search.presentation.SearchState
import com.example.playlistmaker.search.presentation.SearchStatus
import com.example.playlistmaker.search.presentation.SearchViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchFragment : Fragment() {

    private lateinit var queryInput: EditText
    private lateinit var ivClearInputText: ImageView
    private lateinit var clearHistoryButton: Button
    private lateinit var refresh: Button
    private lateinit var progressBar: ProgressBar

    private lateinit var rvTrack: RecyclerView
    private lateinit var rvHistoryTrack: RecyclerView

    private lateinit var nothingFound: LinearLayout
    private lateinit var networkIssue: LinearLayout
    private lateinit var searchHistoryFragment: LinearLayout

    private lateinit var trackAdapter: TrackAdapter
    private lateinit var historyTracksAdapter: TrackAdapter

    private var isClickAllowed = true
    private val handler = Handler(Looper.getMainLooper())

    private val viewModel by viewModel<SearchViewModel>()
    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        setupAdapters()
        setupListeners()

        viewModel.observeState().observe(viewLifecycleOwner) {
            render(it)
        }
    }

    override fun onStart() {
        super.onStart()
        historyTracksAdapter.notifyDataSetChanged()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun render(it: SearchState) {
        when (it) {
            is SearchState.Loading -> showLoading()
            is SearchState.Content -> showContent(it.tracks)
            is SearchState.Empty -> showEmpty()
            is SearchState.Error -> showError()
            is SearchState.HistoryContent -> showHistory(it.historyList)
        }
    }

    private fun showHistory(historyList: List<Track>) {
        setStatus(SearchStatus.HISTORY)
    }

    private fun showLoading() {
        setStatus(SearchStatus.PROGRESS)
    }

    private fun showContent(tracks: List<Track>) {
        setStatus(SearchStatus.SUCCESS)
        trackAdapter.tracks.clear()
        trackAdapter.tracks.addAll(tracks)
        trackAdapter.notifyDataSetChanged()
    }

    private fun showEmpty() {
        setStatus(SearchStatus.EMPTY_SEARCH)
        trackAdapter.tracks.clear()
        trackAdapter.notifyDataSetChanged()
    }

    private fun showError() {
        setStatus(SearchStatus.CONNECTION_ERROR)
        trackAdapter.tracks.clear()
        trackAdapter.notifyDataSetChanged()
    }

    private fun initView() {
        queryInput = binding.searchInput
        ivClearInputText = binding.clearText
        clearHistoryButton = binding.deleteHistoryButton
        refresh = binding.refreshButton
        progressBar = binding.progressBar
        rvTrack = binding.recyclerSearch
        rvHistoryTrack = binding.recyclerHistory
        nothingFound = binding.errorNothingFound
        networkIssue = binding.errorNoConnection
        searchHistoryFragment = binding.searchHistoryFragment
    }

    private fun setupAdapters() {
        val onClickListener = TrackClickListener { track ->
            if (clickDebounce()) {
                transferDataToPlayerActivity(track)
                viewModel.addTrackToSearchHistory(track)
                historyTracksAdapter.tracks = viewModel.getTracksFromSearchHistory()
            }
        }

        trackAdapter = TrackAdapter(onClickListener)
        rvTrack.adapter = trackAdapter

        historyTracksAdapter = TrackAdapter(onClickListener)
        historyTracksAdapter.tracks = viewModel.getTracksFromSearchHistory()
        rvHistoryTrack.adapter = historyTracksAdapter

        if (viewModel.getTracksFromSearchHistory().isNotEmpty())
            searchHistoryFragment.visibility = View.VISIBLE
    }

    private fun transferDataToPlayerActivity(track: Track) {
        val intent = Intent(requireContext(), PlayerActivity::class.java)
        intent.putExtra(SEARCH_INPUT_KEY, track)
        startActivity(intent)
    }

    private fun setupListeners() {
        refresh.setOnClickListener { viewModel.searchDebounce(queryInput.text.toString()) }

        ivClearInputText.setOnClickListener {
            viewModel.clearSearchText()
            queryInput.text = null
            val inputMethodManager =
                requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.hideSoftInputFromWindow(queryInput.windowToken, 0)
            rvTrack.visibility = View.GONE
            if (viewModel.getTracksFromSearchHistory().isEmpty()) {
                setStatus(SearchStatus.ALL_GONE)
            } else setStatus(SearchStatus.HISTORY)
        }

        queryInput.apply { requestFocus() }
        queryInput.doOnTextChanged { text, _, _, _ ->
            if (text.isNullOrEmpty()) {
                ivClearInputText.visibility = View.GONE
                if (viewModel.getTracksFromSearchHistory().isEmpty()) {
                    setStatus(SearchStatus.ALL_GONE)
                } else setStatus(SearchStatus.HISTORY)
                viewModel.removeAllCallbacks()
            } else {
                viewModel.searchDebounce(queryInput.text.toString())
                ivClearInputText.visibility = View.VISIBLE
                searchHistoryFragment.visibility = View.GONE
            }
        }
        queryInput.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                if (queryInput.text.isNotEmpty())
                    search()
                else {
                    if (viewModel.getTracksFromSearchHistory().isEmpty()) {
                        setStatus(SearchStatus.ALL_GONE)
                    } else setStatus(SearchStatus.HISTORY)
                }
            }
            false
        }
        queryInput.setOnFocusChangeListener { _, hasFocus ->
            searchHistoryFragment.visibility =
                if (hasFocus && queryInput.text.isEmpty()) View.VISIBLE else View.GONE
        }

        clearHistoryButton.setOnClickListener {
            viewModel.clearSearchHistory()
            searchHistoryFragment.visibility = View.GONE
            historyTracksAdapter.tracks = ArrayList()
            rvHistoryTrack.adapter?.notifyDataSetChanged()
        }
    }

    private fun search() {
        if (queryInput.text.isNotEmpty()) {
            setStatus(SearchStatus.PROGRESS)
            viewModel.searchDebounce(queryInput.text.toString())
        }
    }

    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }

    private fun setStatus(status: SearchStatus) {
        when (status) {
            SearchStatus.PROGRESS -> {
                progressBar.visibility = View.VISIBLE
                searchHistoryFragment.visibility = View.GONE
                networkIssue.visibility = View.GONE
                nothingFound.visibility = View.GONE
                rvTrack.visibility = View.GONE
            }

            SearchStatus.CONNECTION_ERROR -> {
                networkIssue.visibility = View.VISIBLE
                searchHistoryFragment.visibility = View.GONE
                rvTrack.visibility = View.GONE
                nothingFound.visibility = View.GONE
                progressBar.visibility = View.GONE
            }

            SearchStatus.EMPTY_SEARCH -> {
                nothingFound.visibility = View.VISIBLE
                searchHistoryFragment.visibility = View.GONE
                networkIssue.visibility = View.GONE
                rvTrack.visibility = View.GONE
                progressBar.visibility = View.GONE
            }

            SearchStatus.SUCCESS -> {
                rvTrack.visibility = View.VISIBLE
                searchHistoryFragment.visibility = View.GONE
                networkIssue.visibility = View.GONE
                nothingFound.visibility = View.GONE
                progressBar.visibility = View.GONE
            }

            SearchStatus.HISTORY -> {
                searchHistoryFragment.visibility = View.VISIBLE
                networkIssue.visibility = View.GONE
                nothingFound.visibility = View.GONE
                rvTrack.visibility = View.GONE
                progressBar.visibility = View.GONE
            }

            SearchStatus.ALL_GONE -> {
                searchHistoryFragment.visibility = View.GONE
                networkIssue.visibility = View.GONE
                nothingFound.visibility = View.GONE
                rvTrack.visibility = View.GONE
                progressBar.visibility = View.GONE
            }
        }
    }

    companion object {
        const val SEARCH_INPUT_KEY = "SEARCH_INPUT"
        const val CLICK_DEBOUNCE_DELAY = 1000L
    }
}