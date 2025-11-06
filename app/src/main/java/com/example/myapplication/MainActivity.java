package com.example.myapplication;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    private EditText txtid, txtnombre, txtapellido;
    private FeedReaderDbHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        dbHelper = new FeedReaderDbHelper(this);

        txtid = findViewById(R.id.editTextText);
        txtnombre = findViewById(R.id.editTextText3);
        txtapellido = findViewById(R.id.editTextText2);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public void Buscar(View vista) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] projection = {
                BaseColumns._ID,
                FeedReaderContract.FeedEntry.column1,
                FeedReaderContract.FeedEntry.column2
        };

        String selection = FeedReaderContract.FeedEntry._ID + " = ?";
        String[] selectionArgs = {txtid.getText().toString()};

        String sortOrder = FeedReaderContract.FeedEntry.column2 + " ASC";

        try {
            Cursor cursor = db.query(
                    FeedReaderContract.FeedEntry.nameTable,
                    projection,
                    selection,
                    selectionArgs,
                    null,
                    null,
                    sortOrder
            );

            if (cursor.moveToFirst()) {
                String nombre = cursor.getString(cursor.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.column1));
                txtnombre.setText(nombre);
                String apellido = cursor.getString(cursor.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.column2));
                txtapellido.setText(apellido);
            } else {
                Toast.makeText(this, "Registro no encontrado", Toast.LENGTH_SHORT).show();
                txtnombre.setText("");
                txtapellido.setText("");
            }
            cursor.close();
        } catch (Exception e) {
            Toast.makeText(this, "Error al buscar: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void Eliminar(View vista) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String selection = FeedReaderContract.FeedEntry._ID + " = ?";
        String[] selectionArgs = {txtid.getText().toString()};

        try {
            int deletedRows = db.delete(FeedReaderContract.FeedEntry.nameTable, selection, selectionArgs);
            if (deletedRows > 0) {
                Toast.makeText(getApplicationContext(), "Se eliminó " + deletedRows + " registro(s)", Toast.LENGTH_SHORT).show();
                txtid.setText("");
                txtnombre.setText("");
                txtapellido.setText("");
            } else {
                Toast.makeText(getApplicationContext(), "No se encontró el registro para eliminar", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Error al eliminar: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void Actualizar(View vista) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String nombre = txtnombre.getText().toString();
        String apellido = txtapellido.getText().toString();
        ContentValues values = new ContentValues();
        values.put(FeedReaderContract.FeedEntry.column1, nombre);
        values.put(FeedReaderContract.FeedEntry.column2, apellido);

        String selection = FeedReaderContract.FeedEntry._ID + " = ?";
        String[] selectionArgs = {txtid.getText().toString()};

        try {
            int count = db.update(FeedReaderContract.FeedEntry.nameTable,
                    values,
                    selection,
                    selectionArgs);

            if (count > 0) {
                Toast.makeText(getApplicationContext(), "Se actualizó " + count + " registro(s)", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), "No se encontró el registro para actualizar", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Error al actualizar: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}
