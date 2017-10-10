package com.example.leonid.debterautocorrect;

import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private static final int CM_DELETE_ID = 1;
    private static final String MINE_DEBTS = "\"Мои долги\"";
    private static final String OTHERS_DEBTS = "\"Чужие долги\"";

    private static final String DB_TABLE1 = "mytab1";
    private static final String DB_TABLE2 = "mytab2";

    EditText etName, etDebt;
    Button btnAdd, btnMine, btnOthers;
    ListView lvDebts;
    TextView tvDebt;

    String[] data = {"EUR", "UAH", "USD", "RUB"};
    String currency;

    boolean isMine = true;

    DB db;
    SimpleCursorAdapter scAdapter;
    Cursor cursor1;
    Cursor cursor2;

    // создаем адаптер
    ArrayAdapter<String> adapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        {
            etName = (EditText) findViewById(R.id.etName);
            etDebt = (EditText) findViewById(R.id.etDebt);
            btnAdd = (Button) findViewById(R.id.btnAdd);
            btnMine = (Button) findViewById(R.id.btnMinePart);
            btnOthers = (Button) findViewById(R.id.btnOthersPart);
            lvDebts = (ListView) findViewById(R.id.lvDebts);
            tvDebt = (TextView) findViewById(R.id.tvDebt);
        }

        // открываем подключение к БД
        db = new DB(this);
        db.open();

        // получаем курсор
        cursor1 = db.getAllData(DB_TABLE1);
        cursor2 = db.getAllData(DB_TABLE2);

        startManagingCursor(cursor1);

        // формируем столбцы сопоставления
        String[] from = new String[]{DB.COLUMN_TXT1, DB.COLUMN_TXT2, DB.COLUMN_TXT3};
        int[] to = new int[]{R.id.textViewName, R.id.textViewMoney, R.id.textViewValuta};

        // создааем адаптер и настраиваем список
        scAdapter = new SimpleCursorAdapter(this, R.layout.debter_example, cursor1, from, to);
        lvDebts.setAdapter(scAdapter);

        // добавляем контекстное меню к списку
        registerForContextMenu(lvDebts);

        // адаптер
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, data);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        final Spinner spinner = (Spinner) findViewById(R.id.spinner);
        spinner.setAdapter(adapter);

        // заголовок
        spinner.setPrompt("UAH");

        // выделяем элемент
        spinner.setSelection(2);

        // устанавливаем обработчик нажатия
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                // показываем позиция нажатого элемента
                Toast.makeText(getBaseContext(), "Position = " + position, Toast.LENGTH_SHORT).show();
                currency = "Валюта : " + data[position];
                spinner.setPrompt(currency);
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });

        onClickMinePart(new View(this));
    }

    public void onClickAdd(View view) {
        String name = etName.getText().toString();
        String debt = "Долг : " + etDebt.getText().toString();

        // формируем столбцы сопоставления
        String[] from = new String[]{DB.COLUMN_TXT1, DB.COLUMN_TXT2, DB.COLUMN_TXT3};
        int[] to = new int[]{R.id.textViewName, R.id.textViewMoney, R.id.textViewValuta};

        if (isMine) {
            db.addRec(DB_TABLE1, name, debt, currency);

            // создаем адаптер
            cursor1.requery();
            startManagingCursor(cursor1);

            // создааем адаптер и настраиваем список
            scAdapter = new SimpleCursorAdapter(this, R.layout.debter_example, cursor1, from, to);
        } else {
            db.addRec(DB_TABLE2, name, debt, currency);
            // создаем адаптер
            cursor2.requery();
            startManagingCursor(cursor2);

            // создааем адаптер и настраиваем список
            scAdapter = new SimpleCursorAdapter(this, R.layout.debter_example, cursor2, from, to);
        }

        lvDebts.setAdapter(scAdapter);
    }

    public void onClickMinePart(View view) {
        tvDebt.setText(MINE_DEBTS);
        isMine = true;

        // формируем столбцы сопоставления
        String[] from = new String[]{DB.COLUMN_TXT1, DB.COLUMN_TXT2, DB.COLUMN_TXT3};
        int[] to = new int[]{R.id.textViewName, R.id.textViewMoney, R.id.textViewValuta};

        // создаем адаптер
        cursor1.requery();
        startManagingCursor(cursor1);

        // создааем адаптер и настраиваем список
        scAdapter = new SimpleCursorAdapter(this, R.layout.debter_example, cursor1, from, to);

        lvDebts.setAdapter(scAdapter);
    }

    public void onClickOthersPart(View view) {
        tvDebt.setText(OTHERS_DEBTS);
        isMine = false;

        // формируем столбцы сопоставления
        String[] from = new String[]{DB.COLUMN_TXT1, DB.COLUMN_TXT2, DB.COLUMN_TXT3};
        int[] to = new int[]{R.id.textViewName, R.id.textViewMoney, R.id.textViewValuta};

        // создаем адаптер
        cursor2.requery();
        startManagingCursor(cursor2);

        // создааем адаптер и настраиваем список
        scAdapter = new SimpleCursorAdapter(this, R.layout.debter_example, cursor2, from, to);

        lvDebts.setAdapter(scAdapter);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0, CM_DELETE_ID, 0, "Удалить");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getItemId() == CM_DELETE_ID) {
            // получаем из пункта контекстного меню данные по пункту списка
            AdapterView.AdapterContextMenuInfo acmi = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
            // извлекаем id записи и удаляем соответствующую запись в БД
            if (isMine) {
                db.delRec(DB_TABLE1, acmi.id);
                // обновляем курсор
                cursor1.requery();
            } else {
                db.delRec(DB_TABLE2, acmi.id);
                // обновляем курсор
                cursor2.requery();
            }

            return true;
        }
        return super.onContextItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // закрываем подключение при выходе
        db.close();
    }
}