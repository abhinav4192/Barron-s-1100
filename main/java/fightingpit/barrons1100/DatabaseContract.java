package fightingpit.barrons1100;

import android.provider.BaseColumns;

/**
 * Created by AG on 07-Oct-15.
 */
public class DatabaseContract {
    public static final  int    DATABASE_VERSION    = 1;
    public static final  String DATABASE_NAME       = "database.db";
//    private static final String PRIMARY_KEY         = "PRIMARY KEY";
//    private static final String FOREIGN_KEY         = "FOREIGN KEY";
//    private static final String UNIQUE              = "UNIQUE";
//    private static final String REFERENCES          = "REFERENCES ";
    private static final String COMMA_SEP           = ", ";

    public static final String[] SQL_CREATE_TABLE_ARRAY = {
            WordListDB.CREATE_TABLE,
    };

    public static final String[] SQL_DROP_TABLE_ARRAY = {
            WordListDB.DROP_TABLE,
    };

    private DatabaseContract() {}

    public static abstract class WordListDB implements BaseColumns {

        public static final String TABLE_NAME = "WORD_LIST";
        public static final String WORD     = "WORD";
        public static final String MEANING   = "MEANING";
        public static final String FAVOURITE = "FAVOURITE";
        public static final String PROGRESS = "PROGRESS";
        public static final String SET_NUMBER = "SET_NUMBER";

        public static final String CREATE_TABLE = "CREATE TABLE " +
                TABLE_NAME + " (" +
                WORD + " TEXT PRIMARY KEY NOT NULL" + COMMA_SEP +
                MEANING + " TEXT NOT NULL" + COMMA_SEP +
                FAVOURITE + " INTEGER NOT NULL" + COMMA_SEP +
                PROGRESS + " INTEGER NOT NULL" + COMMA_SEP +
                SET_NUMBER + " INTEGER NOT NULL" +
                " )";

        public static final String DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
    }
}
