package info.androidhive.Hamwig2_Course_Waitinglist.view;

/**************** Created by George B. Hamwi Homework 3 *******************/

import android.content.DialogInterface;
import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import java.util.ArrayList;
import java.util.List;
import info.androidhive.Hamwig2_Course_Waitinglist.R;
import info.androidhive.Hamwig2_Course_Waitinglist.database.DatabaseHelper;
import info.androidhive.Hamwig2_Course_Waitinglist.database.model.Note;
import info.androidhive.Hamwig2_Course_Waitinglist.utils.MyDividerItemDecoration;
import info.androidhive.Hamwig2_Course_Waitinglist.utils.RecyclerTouchListener;

public class MainActivity extends AppCompatActivity {

    /* creating new private classes  */
    private NotesAdapter mAdapter;
    private List<Note> notesList = new ArrayList<>();
    private CoordinatorLayout coordinatorLayout;
    private RecyclerView recyclerView;
    private TextView noNotesView;

    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /* creating new variables */
        coordinatorLayout = findViewById(R.id.coordinator_layout);
        recyclerView = findViewById(R.id.recycler_view);
        noNotesView = findViewById(R.id.empty_notes_view);

        db = new DatabaseHelper(this);

        notesList.addAll(db.getAllNotes());

        /* Floating action button when clicked the user is prompt to enter a students information  */
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showNoteDialog(false, null, -1);
            }
        });

        /* RecyclerView that is displayed to the user when he/she long presses on a students name that was entered  */
        mAdapter = new NotesAdapter(this, notesList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new MyDividerItemDecoration(this, LinearLayoutManager.VERTICAL, 16));
        recyclerView.setAdapter(mAdapter);
        toggleEmptyNotes();

        /* On long press on RecyclerView item, open alert dialog with options to choose Edit and Delete */

        recyclerView.addOnItemTouchListener
                (new RecyclerTouchListener(this,
                recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, final int position) {
            }

            @Override
            public void onLongClick(View view, int position) {

                showActionsDialog(position);
            }
        }));
    }

    /* Inserting new note into the database, and refreshing the waiting list */

    private void createNote(String note) {
        /* Inserting new note into the database, and getting the newly inserted note id */

        long id = db.insertNote(note);

        /* Gets the newly inserted note from the database */
        Note n = db.getNote(id);

        if (n != null) {

        /* This adds the new note to an array list at position 0 */
        notesList.add(0, n);

        /* This refreshes the waiting list */
         mAdapter.notifyDataSetChanged();

         toggleEmptyNotes();
    }
    }
    /* Updating note in database and adding the item to the waiting list by its position entered  */

    private void updateNote(String note, int position) {
        Note n = notesList.get(position);

        /* Updating the note entered in the text that is displayed to the user */
        n.setNote(note);

        /* Updating the note entered in the data base */
        db.updateNote(n);

        /* Refreshing the waiting list */
        notesList.set(position, n);
        mAdapter.notifyItemChanged(position);

        toggleEmptyNotes();
    }

    /* Deleting note from SQLite and removing the item from the waiting list by its position */

    private void deleteNote(int position) {

        //* deleting the note entered from the database */
        db.deleteNote(notesList.get(position));

        /* removing the note from the waiting list that is displayed to the user and refreshing the waiting list */
        notesList.remove(position);
        mAdapter.notifyItemRemoved(position);

        toggleEmptyNotes();
    }
    /* Opened the dialog with Edit and Delete options, Edit - 0, Delete - 0 */

    private void showActionsDialog(final int position) {
        CharSequence colors[] = new CharSequence[]{"Edit", "Delete"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose option");
        builder.setItems(colors, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    showNoteDialog(true, notesList.get(position), position);
                } else {
                    deleteNote(position);
                }
            }
        });
        builder.show();
    }

    /* This is the dialog that is shown to the user long presses on a students name that was entered  */

    private void showNoteDialog(final boolean shouldUpdate, final Note note, final int position) {
        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(getApplicationContext());
        View view = layoutInflaterAndroid.inflate(R.layout.note_dialog, null);

        AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(MainActivity.this);
        alertDialogBuilderUserInput.setView(view);

        /* This is the dialog that is shown when the user presses edit, it says edit info
        on top and the user can enter in the edit text box */

        final EditText inputNote = view.findViewById(R.id.note);
        TextView dialogTitle = view.findViewById(R.id.dialog_title);
        dialogTitle.setText(!shouldUpdate ? getString(R.string.lbl_new_note_title) : getString(R.string.lbl_edit_note_title));

        if (shouldUpdate && note != null) {
            inputNote.setText(note.getNote());
        }
        /* This gives the user an option to update which saves the new information entered by the user */
        alertDialogBuilderUserInput
                .setCancelable(false)
                .setPositiveButton(shouldUpdate ? "update" : "save", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogBox, int id) {

                    }
                })

        /* This gives the user to press cancel which doesn't save any new information entered by the user */
                .setNegativeButton("cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogBox, int id) {
                                dialogBox.cancel();
                            }
                        });

        final AlertDialog alertDialog = alertDialogBuilderUserInput.create();
        alertDialog.show();

        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

        /* When the user does not input any text a toast message is shown to them to enter a note */

                if (TextUtils.isEmpty(inputNote.getText().toString())) {
                    Toast.makeText(MainActivity.this, "Enter note!", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    alertDialog.dismiss();
                }

        /* Checks if the user is updating the note or not */

        if (shouldUpdate && note != null) {
        /* Updates the note by it's id */

           updateNote(inputNote.getText().toString(), position);
           } else {
        /* Creates a new note  */

           createNote(inputNote.getText().toString());
           }
            }
        });
    }


    /* Toggling list and empty notes view */

    private void toggleEmptyNotes() {

    /* Check if notesList.size() > 0 */

        if (db.getNotesCount() > 0) {
            noNotesView.setVisibility(View.GONE);
        } else {
            noNotesView.setVisibility(View.VISIBLE);
        }
    }
}
