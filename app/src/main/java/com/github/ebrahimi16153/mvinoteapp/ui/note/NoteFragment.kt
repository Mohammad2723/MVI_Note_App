package com.github.ebrahimi16153.mvinoteapp.ui.note

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.github.ebrahimi16153.mvinoteapp.databinding.FragmentNoteBinding
import com.github.ebrahimi16153.mvinoteapp.utils.setUpSpinnerByAdapter
import com.github.ebrahimi16153.mvinoteapp.viewmodel.note.NoteIntent
import com.github.ebrahimi16153.mvinoteapp.viewmodel.note.NoteState
import com.github.ebrahimi16153.mvinoteapp.viewmodel.note.NoteViewModel
import com.github.ebrahimi16153.noteapp.data.model.NoteModel
import com.github.ebrahimi16153.noteapp.utils.Constant.ID_KEY
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class NoteFragment : BottomSheetDialogFragment() {


    // binding
    private var _binding: FragmentNoteBinding? = null
    private val binding get() = _binding!!
    //viewModel
    private val noteViewModel: NoteViewModel by viewModels()
    // noteEntity
    @Inject
    lateinit var noteEntity: NoteModel
    private var noteId = 0
    private var category = mutableListOf<String>()
    private var priority = mutableListOf<String>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentNoteBinding.inflate(layoutInflater)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        noteId = arguments?.getInt(ID_KEY)?:0

        binding.apply {

            //close button
            closeImg.setOnClickListener { dismiss() }

            //saveButton
            saveNote.setOnClickListener {
                noteEntity.id = noteId
                noteEntity.title = titleEdt.text.toString()
                noteEntity.description = descEdt.text.toString()

                if (noteEntity.priority.isNotEmpty() && noteEntity.category.isNotEmpty()
                    && noteEntity.title.isNotEmpty() && noteEntity.description.isNotEmpty()) {

                    lifecycleScope.launch {
                        noteViewModel.noteIntent.send(NoteIntent.SaveNote(noteEntity))
                    }
                } else {
                    Toast.makeText(requireContext(), "empty fields", Toast.LENGTH_SHORT).show()
                }
            }


            //manage State
            lifecycleScope.launch {
                //setup spinners
                noteViewModel.noteIntent.send(NoteIntent.SpinnersList)
                //if noteID > 0
                if (noteId > 0)
                    noteViewModel.noteIntent.send(NoteIntent.NoteByID(noteID = noteId))

                //State
                noteViewModel.noteState.collect { itState ->

                    when (itState) {
                        NoteState.Idle -> {}
                        is NoteState.Error -> showError(itState)
                        is NoteState.SaveNote -> dismiss()
                        is NoteState.Spinners -> fillSpinners(itState)
                        is NoteState.GetNote -> { updateUi(itState) }
                    }
                }
            }
        }
    }

    private fun updateUi(itState: NoteState.GetNote) {
          val note = itState.note
        binding.apply {
            titleEdt.setText(note.title)
            descEdt.setText(note.description)
            categoriesSpinner.setSelection(category.indexOf(note.category))
            prioritySpinner.setSelection(priority.indexOf(note.priority))
        }
    }


    private fun fillSpinners(noteState: NoteState.Spinners) {
        binding.apply {

            category = noteState.categories
            priority = noteState.priorities

            categoriesSpinner.setUpSpinnerByAdapter(
                list = category,
                item = { itSelectedItem ->
                    noteEntity.category = itSelectedItem
                })

            prioritySpinner.setUpSpinnerByAdapter(
                list = priority,
                item = { itSelectedItem ->
                    noteEntity.priority = itSelectedItem
                })
        }
    }


    private fun showError(state: NoteState.Error) {
        Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}