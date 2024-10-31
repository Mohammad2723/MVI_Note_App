package com.github.ebrahimi16153.mvinoteapp.ui.main

import android.os.Bundle
import android.view.Menu
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.github.ebrahimi16153.mvinoteapp.R
import com.github.ebrahimi16153.mvinoteapp.data.adapters.NoteAdapter
import com.github.ebrahimi16153.mvinoteapp.databinding.ActivityMainBinding
import com.github.ebrahimi16153.mvinoteapp.ui.note.NoteFragment
import com.github.ebrahimi16153.mvinoteapp.viewmodel.main.MainIntent
import com.github.ebrahimi16153.mvinoteapp.viewmodel.main.MainState
import com.github.ebrahimi16153.mvinoteapp.viewmodel.main.MainViewModel
import com.github.ebrahimi16153.noteapp.utils.Constant.ALL
import com.github.ebrahimi16153.noteapp.utils.Constant.DELETE
import com.github.ebrahimi16153.noteapp.utils.Constant.EDIT
import com.github.ebrahimi16153.noteapp.utils.Constant.HIGH
import com.github.ebrahimi16153.noteapp.utils.Constant.ID_KEY
import com.github.ebrahimi16153.noteapp.utils.Constant.LOW
import com.github.ebrahimi16153.noteapp.utils.Constant.MEDIUM
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    //binding
    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!

    //viewModel
    private val mainViewModel: MainViewModel by viewModels()

    //adapter
    @Inject
    lateinit var adapter: NoteAdapter

    //selected priority
    private var selectedItem = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.apply {

            setSupportActionBar(notesToolbar)
            // open BottomSheet
            fab.setOnClickListener {
                NoteFragment().show(supportFragmentManager, NoteFragment().tag)
            }

            //            click on filter
            notesToolbar.setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.main_menu_Filter -> {
                        filterDialogByPriority { priority ->
                            lifecycleScope.launch {
                                when (priority) {
                                    ALL -> {
                                        mainViewModel.intent.send(MainIntent.NoteListAll)
                                    }

                                    HIGH -> {
                                        mainViewModel.intent.send(MainIntent.NoteListHigh)
                                    }

                                    LOW -> {
                                        mainViewModel.intent.send(MainIntent.NoteListLow)
                                    }

                                    MEDIUM -> {
                                        mainViewModel.intent.send(MainIntent.NoteListMedium)
                                    }
                                }
                            }
                        }

                        return@setOnMenuItemClickListener true
                    }

                    else -> {
                        return@setOnMenuItemClickListener false
                    }
                }
            }


            // state
            lifecycleScope.launch {
                mainViewModel.intent.send(MainIntent.NoteListAll)
                mainViewModel.state.collectLatest { itSate ->
                    when (itSate) {
                        is MainState.Empty -> {
                            noteList.isVisible = false
                            emptyLay.isVisible = true
                        }

                        is MainState.Idle -> {}

                        is MainState.ListState -> {
                            noteList.isVisible = true
                            emptyLay.isVisible = false
                            showList(itSate)
                        }

                        is MainState.DeleteNote -> {}
                    }
                }
            }
        }
    }

    private fun showList(state: MainState.ListState) {
        val list = state.notes
        binding.apply {
            adapter.setData(list)
            noteList.adapter = adapter
            noteList.layoutManager =
                StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)

            adapter.seOnItemClickListener { noteModel, menuItem ->

                when (menuItem) {
                    DELETE -> {
                        lifecycleScope.launch {
                            mainViewModel.intent.send(MainIntent.DeleteNote(note = noteModel))
                        }
                    }

                    EDIT -> {
                        // send noteID as bundle to NoteFragment
                        val bundle = Bundle()
                        val noteFragment = NoteFragment()
                        bundle.putInt(ID_KEY, noteModel.id)
                        noteFragment.arguments = bundle
                        noteFragment.show(supportFragmentManager, noteFragment.tag)

                    }
                }


            }
        }
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        // menu and searchBar
        menuInflater.inflate(R.menu.main_menu, menu)
        val search = menu?.findItem(R.id.main_menu_search)
        val searchView = search?.actionView as SearchView
        // hint
        searchView.queryHint = "Search..."
        // on change Text
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let { itText ->
                    lifecycleScope.launch {
                        mainViewModel.intent.send(MainIntent.SearchNote(itText))
                    }
                }
                return true
            }

        })

        return super.onCreateOptionsMenu(menu)
    }


    private fun filterDialogByPriority(priority: (String) -> Unit) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(getString(R.string.select_priority))
        val listOfPriority = arrayOf(ALL, HIGH, MEDIUM, LOW)
        builder.setSingleChoiceItems(listOfPriority, selectedItem) { dialog, item ->
            when (item) {
                0 -> {
                    priority(ALL)
                }

                1 -> {
                    priority(HIGH)
                }

                2 -> {
                    priority(MEDIUM)
                }

                3 -> {
                    priority(LOW)
                }
            }
            selectedItem = item
            dialog.dismiss()

        }
        val dialog: AlertDialog = builder.create()
        dialog.show()
        //change background dialog
        dialog.window?.setBackgroundDrawableResource(R.color.charcoal)

    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}