package com.example.notesapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;

public class NoteDetails extends AppCompatActivity {
EditText titleEditText,contentEditText;
ImageButton saveNotebtn;
TextView pageTitleTextView;
String title,content,docId;
boolean isEditMode=false;
TextView deleteNoteTvbtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_details);

        titleEditText=findViewById(R.id.notes_title_text);
        contentEditText=findViewById(R.id.notes_content_text);
        saveNotebtn=findViewById(R.id.save_note_btn);
        pageTitleTextView=findViewById(R.id.page_title);
        deleteNoteTvbtn=findViewById(R.id.delete_note_tvBtn);

        title=getIntent().getStringExtra("title");
        content=getIntent().getStringExtra("content");
        docId=getIntent().getStringExtra("docId");

        if(docId!=null && !docId.isEmpty()){
            isEditMode=true;
        }

        titleEditText.setText(title);
        contentEditText.setText(content);

        if(isEditMode){
            pageTitleTextView.setText("Edit your note");
            deleteNoteTvbtn.setVisibility(View.VISIBLE);
        }

        saveNotebtn.setOnClickListener((V) ->saveNote());
        deleteNoteTvbtn.setOnClickListener((v)->deleteNoteFromFirebase());
    }

     void saveNote() {
        String noteTitle=titleEditText.getText().toString();
        String noteContent=contentEditText.getText().toString();

        if(noteTitle==null || noteTitle.isEmpty()){
            titleEditText.setError("Title is required");
            return;
        }

        NoteModel note=new NoteModel();
        note.setTitle(noteTitle);
        note.setContent(noteContent);
        note.setTimestamp(Timestamp.now());

        saveNoteToFirebase(note);
    }

    void saveNoteToFirebase(NoteModel note){
        DocumentReference documentReference;
        if(isEditMode){

            //update the note
            documentReference=Utility.getCollectionReferenceForNotes().document(docId);
        }
        else{
            //create new mode
            documentReference=Utility.getCollectionReferenceForNotes().document();
        }


        documentReference.set(note).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if(task.isSuccessful()){
                    Utility.showToast(NoteDetails.this,"Note added successfully");
                    finish();
                }
                else {
                    Utility.showToast(NoteDetails.this,"Failed while adding note");
                }

            }
        });
    }
    void deleteNoteFromFirebase(){
        DocumentReference documentReference;

            //update the note
            documentReference=Utility.getCollectionReferenceForNotes().document(docId);

        documentReference.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if(task.isSuccessful()){
                    Utility.showToast(NoteDetails.this,"Note deletd successfully");
                    finish();
                }
                else {
                    Utility.showToast(NoteDetails.this,"Failed while deleting note");
                }

            }
        });


    }
}