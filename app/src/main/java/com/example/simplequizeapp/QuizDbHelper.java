package com.example.simplequizeapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


import java.util.ArrayList;
import java.util.List;

import static android.icu.text.MessagePattern.ArgType.SELECT;

public class QuizDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "MyAwesomeQuiz.db";
    private static final int DATABASE_VERSION = 1;

    private static QuizDbHelper instance;

    private SQLiteDatabase db;

    private QuizDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static synchronized QuizDbHelper getInstance(Context context) {
        if (instance == null) {
            instance = new QuizDbHelper(context.getApplicationContext());
        }
        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        this.db = db;

        final String SQL_CREATE_CATEGORIES_TABLE = "CREATE TABLE " +
                QuizeContract.CategoriesTable.TABLE_NAME + "( " +
                QuizeContract.CategoriesTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                QuizeContract.CategoriesTable.COLUMN_NAME + " TEXT " +
                ")";

        final String SQL_CREATE_QUESTIONS_TABLE = "CREATE TABLE " +
                QuizeContract.QuestionsTable.TABLE_NAME + " ( " +
                QuizeContract.QuestionsTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                QuizeContract.QuestionsTable.COLUMN_QUESTION + " TEXT, " +
                QuizeContract.QuestionsTable.COLUMN_OPTION1 + " TEXT, " +
                QuizeContract.QuestionsTable.COLUMN_OPTION2 + " TEXT, " +
                QuizeContract.QuestionsTable.COLUMN_OPTION3 + " TEXT, " +
                QuizeContract.QuestionsTable.COLUMN_OPTION4 + " TEXT, " +
                QuizeContract.QuestionsTable.COLUMN_ANSWER_NR + " INTEGER, " +
                QuizeContract.QuestionsTable.COLUMN_DIFFICULTY + " TEXT, " +
                QuizeContract.QuestionsTable.COLUMN_CATEGORY_ID + " INTEGER, " +
                "FOREIGN KEY(" + QuizeContract.QuestionsTable.COLUMN_CATEGORY_ID + ") REFERENCES " +
                QuizeContract.CategoriesTable.TABLE_NAME + "(" + QuizeContract.CategoriesTable._ID + ")" + "ON DELETE CASCADE" +
                ")";

        db.execSQL(SQL_CREATE_CATEGORIES_TABLE);
        db.execSQL(SQL_CREATE_QUESTIONS_TABLE);
        fillCategoriesTable();
        fillQuestionsTable();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + QuizeContract.CategoriesTable.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + QuizeContract.QuestionsTable.TABLE_NAME);
        onCreate(db);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    private void fillCategoriesTable() {
        Category c1 = new Category("Programming");
        addCategory(c1);
        Category c2 = new Category("Geography");
        addCategory(c2);
        Category c3 = new Category("Math");
        addCategory(c3);
    }

    private void addCategory(Category category) {
        ContentValues cv = new ContentValues();
        cv.put(QuizeContract.CategoriesTable.COLUMN_NAME, category.getName());
        db.insert(QuizeContract.CategoriesTable.TABLE_NAME, null, cv);
    }

    private void fillQuestionsTable() {
        Question q1 = new Question("Programming, Easy: A is correct",
                "A", "B", "C", "D", 1,
                Question.DIFFICULTY_EASY, Category.PROGRAMMING);

        addQuestion(q1);

        Question q2 = new Question("Geography, Medium: B is correct",
                "A", "B", "C", "D", 2,
                Question.DIFFICULTY_MEDIUM, Category.GEOGRAPHY);

        addQuestion(q2);

        Question q3 = new Question("Math, Hard: C is correct",
                "A", "B", "C", "D", 3,
                Question.DIFFICULTY_HARD, Category.MATH);

        addQuestion(q3);

        Question q4 = new Question("Math, Easy: A is correct",
                "A", "B", "C", "D", 1,
                Question.DIFFICULTY_EASY, Category.MATH);

        addQuestion(q4);

        Question q5 = new Question("Non existing, Easy: A is correct",
                "A", "B", "C", "D", 1,
                Question.DIFFICULTY_EASY, 4);

        addQuestion(q5);

        Question q6 = new Question("Non existing, Medium: B is correct",
                "A", "B", "C", "D", 2,
                Question.DIFFICULTY_MEDIUM, 5);

        addQuestion(q6);

        Question q7 = new Question("Programming, Easy: B is correct",
                "A", "B", "C", "D", 2,
                Question.DIFFICULTY_EASY, Category.PROGRAMMING);

        addQuestion(q7);

        Question q8 = new Question("Geography, Medium: C is correct",
                "A", "B", "C", "D", 3,
                Question.DIFFICULTY_MEDIUM, Category.GEOGRAPHY);

        addQuestion(q8);

        Question q9 = new Question("Math, Hard: D is correct",
                "A", "B", "C", "D", 4,
                Question.DIFFICULTY_HARD, Category.MATH);

        addQuestion(q9);

        Question q10 = new Question("Math, Easy: C is correct",
                "A", "B", "C", "D", 3,
                Question.DIFFICULTY_EASY, Category.MATH);

        addQuestion(q10);

        Question q11 = new Question("Non existing, Easy: D is correct",
                "A", "B", "C", "D", 4,
                Question.DIFFICULTY_EASY, 4);

        addQuestion(q11);
    }

    private void addQuestion(Question question) {
        ContentValues cv = new ContentValues();
        cv.put(QuizeContract.QuestionsTable.COLUMN_QUESTION, question.getQuestion());
        cv.put(QuizeContract.QuestionsTable.COLUMN_OPTION1, question.getOption1());
        cv.put(QuizeContract.QuestionsTable.COLUMN_OPTION2, question.getOption2());
        cv.put(QuizeContract.QuestionsTable.COLUMN_OPTION3, question.getOption3());
        cv.put(QuizeContract.QuestionsTable.COLUMN_OPTION4,question.getOption4());
        cv.put(QuizeContract.QuestionsTable.COLUMN_ANSWER_NR, question.getAnswerNr());
        cv.put(QuizeContract.QuestionsTable.COLUMN_DIFFICULTY, question.getDifficulty());
        cv.put(QuizeContract.QuestionsTable.COLUMN_CATEGORY_ID, question.getCategoryID());
        db.insert(QuizeContract.QuestionsTable.TABLE_NAME, null, cv);
    }

    public List<Category> getAllCategories() {
        List<Category> categoryList = new ArrayList<>();
        db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + QuizeContract.CategoriesTable.TABLE_NAME, null);

        if (c.moveToFirst()) {
            do {
                Category category = new Category();
                category.setId(c.getInt(c.getColumnIndex(QuizeContract.CategoriesTable._ID)));
                category.setName(c.getString(c.getColumnIndex(QuizeContract.CategoriesTable.COLUMN_NAME)));
                categoryList.add(category);
            } while (c.moveToNext());
        }

        c.close();
        return categoryList;
    }

    public ArrayList<Question> getAllQuestions() {
        ArrayList<Question> questionList = new ArrayList<>();
        db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + QuizeContract.QuestionsTable.TABLE_NAME, null);

        if (c.moveToFirst()) {
            do {
                Question question = new Question();
                question.setId(c.getInt(c.getColumnIndex(QuizeContract.QuestionsTable._ID)));
                question.setQuestion(c.getString(c.getColumnIndex(QuizeContract.QuestionsTable.COLUMN_QUESTION)));
                question.setOption1(c.getString(c.getColumnIndex(QuizeContract.QuestionsTable.COLUMN_OPTION1)));
                question.setOption2(c.getString(c.getColumnIndex(QuizeContract.QuestionsTable.COLUMN_OPTION2)));
                question.setOption3(c.getString(c.getColumnIndex(QuizeContract.QuestionsTable.COLUMN_OPTION3)));
                question.setOption4(c.getString(c.getColumnIndex(QuizeContract.QuestionsTable.COLUMN_OPTION4)));
                question.setAnswerNr(c.getInt(c.getColumnIndex(QuizeContract.QuestionsTable.COLUMN_ANSWER_NR)));
                question.setDifficulty(c.getString(c.getColumnIndex(QuizeContract.QuestionsTable.COLUMN_DIFFICULTY)));
                question.setCategoryID(c.getInt(c.getColumnIndex(QuizeContract.QuestionsTable.COLUMN_CATEGORY_ID)));
                questionList.add(question);
            } while (c.moveToNext());
        }

        c.close();
        return questionList;
    }

    public ArrayList<Question> getQuestions(int categoryID, String difficulty) {
        ArrayList<Question> questionList = new ArrayList<>();
        db = getReadableDatabase();

        String selection = QuizeContract.QuestionsTable.COLUMN_CATEGORY_ID + " = ? " +
                " AND " + QuizeContract.QuestionsTable.COLUMN_DIFFICULTY + " = ? ";
        String[] selectionArgs = new String[]{String.valueOf(categoryID), difficulty};

        Cursor c = db.query(
                QuizeContract.QuestionsTable.TABLE_NAME,
                null,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        if (c.moveToFirst()) {
            do {
                Question question = new Question();
                question.setId(c.getInt(c.getColumnIndex(QuizeContract.QuestionsTable._ID)));
                question.setQuestion(c.getString(c.getColumnIndex(QuizeContract.QuestionsTable.COLUMN_QUESTION)));
                question.setOption1(c.getString(c.getColumnIndex(QuizeContract.QuestionsTable.COLUMN_OPTION1)));
                question.setOption2(c.getString(c.getColumnIndex(QuizeContract.QuestionsTable.COLUMN_OPTION2)));
                question.setOption3(c.getString(c.getColumnIndex(QuizeContract.QuestionsTable.COLUMN_OPTION3)));
                question.setAnswerNr(c.getInt(c.getColumnIndex(QuizeContract.QuestionsTable.COLUMN_ANSWER_NR)));
                question.setDifficulty(c.getString(c.getColumnIndex(QuizeContract.QuestionsTable.COLUMN_DIFFICULTY)));
                question.setCategoryID(c.getInt(c.getColumnIndex(QuizeContract.QuestionsTable.COLUMN_CATEGORY_ID)));
                questionList.add(question);
            } while (c.moveToNext());
        }

        c.close();
        return questionList;
    }
}

