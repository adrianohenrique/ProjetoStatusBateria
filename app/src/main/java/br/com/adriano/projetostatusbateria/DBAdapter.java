package br.com.adriano.projetostatusbateria;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBAdapter {

//declaração do nome das tabelas e suas colunas, essas Strings constantes e estáticas são utilizadas para definir as Strings de criação das tabelas do banco

    public static final String TABELA_MONITOR = "CATEGORIA";
    public static final String COLUNA_ID_MONITOR = "_id";
    public static final String COLUNA_CARGA_BATERIA = "DESCRICAO";
    public static final String COLUNA_TIME = "TIME";
    //Criação da tabela
    private static final String MONITOR_CREATE_TABLE = "CREATE TABLE "
            + TABELA_MONITOR + "  (" + COLUNA_ID_MONITOR
            + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COLUNA_CARGA_BATERIA + COLUNA_TIME +");";


    private static final String TAG = "DbAdapter";
    private DatabaseHelper myDBHelper;
    private SQLiteDatabase myDB;

    private static final String DB_NAME = "ABP";
    private static final int DATABASE_VERSION = 1;

    private final Context mCtx;

    // Construtor DBAdapter
    public DBAdapter(Context ctx) {
        this.mCtx = ctx;
    }
/*O método DbAdapter.open é o responsável por instânciar a mDbHelper e por adiquirir acesso com
 permissão de escrita ao banco (leitura também). Veja que mDb também é instânciado e fechado
junto de mDbHelper*/

    public DBAdapter open() throws SQLException {
        myDBHelper = new DatabaseHelper(mCtx);
        myDB = myDBHelper.getWritableDatabase(); //mDb é o suficiente para inserir, atualizar, remover e buscar informações nas tabelas através dos métodos insert,update, delete e query
        return this;
    }
// Método responsável por fechar o banco

    public void close() {
        myDBHelper.close();
        myDB.close();
    }

    public void adicionarColuna(){
        myDB.execSQL("ALTER TABLE "+TABELA_MONITOR+" ADD "+COLUNA_TIME + " String");
        Log.w("DbAdapter", "Coluna adicionada com sucesso!");
    }


    /*estender a classe SQLiteOpenHelper e sobrescrever alguns métodos,
     como o onOpen(SQLiteDatabase db), onCreate(SQLiteDatabase db) e
     o onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
     a classe private DatabaseHelper que é a responsável por criar,
     atualizar e nos dar um meio de acesso ao mesmo*/

    private static class DatabaseHelper extends SQLiteOpenHelper {


//É executado toda vez que se cria um objeto dessa classe e o
// nosso ali faz com que a utilização de chaves estrangeiras seja ativada para o banco

        @Override
        public void onOpen(SQLiteDatabase db)
        {
            super.onOpen(db);
            if (!db.isReadOnly())
            {
                db.execSQL("PRAGMA foreign_keys=ON;");
            }
        }

        //construtor da classe sobrescrito
        DatabaseHelper(Context context) {
            super(context, DB_NAME, null, DATABASE_VERSION);
        }

/*É executado somente na primeira vez que a classe DbAdapter é instânciada.
 Esse método recebe como parâmetro um objeto do tipo SQLiteDatabase que é o
 verdadeiro objeto de acesso ao banco. Este objeto é utilizado para executar a
 criação das tabelas TABELA_MONITOR e TABELA_EMPRESTIMOS, depois disso é utilizado
 para inserir alguns valores padrão na tabela TABELA_MONITOR.*/

        @Override
        public void onCreate(SQLiteDatabase db) {

            db.execSQL(MONITOR_CREATE_TABLE);
            // db.execSQL(EMPRESTIMOS_CREATE_TABLE);

            //Insere valores iniciais na tabela CATEGORIA
            ContentValues values = new ContentValues();
            values.put(COLUNA_CARGA_BATERIA, "Carga_Bateria");
            db.insert(TABELA_MONITOR, null, values);

            Log.w("DbAdapter", "DB criado com sucesso!");
        }
//Também recebe um SQLiteDatabase como parâmetro, mas muitas vezes o que nos
// interessa nesse método são os outros dois parâmetros oldVersion e newVersion.
//Eles nos informam o valor da versão antiga e da nova versão do banco. Quando estamos
// atualizando um aplicativo de versão, caso haja uma nova versão do banco definida


        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Atualizando o banco de dados da versão " + oldVersion
                    + " para " + newVersion
                    + ", todos os dados serão perdidos!");
            db.execSQL("DROP TABLE IF EXISTS " + TABELA_MONITOR);
            onCreate(db);
        }


    }
//Método chamado quando se deseja inserir uma categoria no banco, como ID_CATEGORIA possui auto
// incremento somente precisamos passar sua descrição como parâmetro do método

    public long InserirDados(String descricao) {
        ContentValues values = new ContentValues();

        values.put(COLUNA_CARGA_BATERIA, descricao);

        return myDB.insert(TABELA_MONITOR, null, values);
    }

 
// O método delete recebe como parâmetros o nome da tabela, uma String com o nome da coluna que será
// utilizada no WHERE concatenada com a condição, no caso o “=” e uma interrogação. A interrogação
// vai ser substituída pelo primeiro valor do array de String que é passado como último parâmetro para o método.
// Estamos dizendo: “Remova uma linha da tabela TABELA_MONITOR ondeCOLUNA_ID_CATEGORIA for igual a idCategoria“.


    public boolean removerCategoria(long idCategoria) {

        return myDB.delete(TABELA_MONITOR, COLUNA_ID_MONITOR + "=?",
                new String[] { String.valueOf(idCategoria) }) > 0;
    }

    //Caso seja preciso passar dois parâmetros para o WHERE ao invés de somente um.
    public boolean removerCategoria(long idCategoria, String descricao) {

        return myDB.delete(TABELA_MONITOR, COLUNA_ID_MONITOR + "=? AND "
                        + COLUNA_CARGA_BATERIA + "=?",
                new String[] { String.valueOf(idCategoria), descricao }) > 0;
    }
//Se for preciso ter acesso a todas as linhas guardadas na tabela TABELA_MONITOR
   /* public Cursor consultarTodasCategorias() {

        return myDB.query(TABELA_MONITOR, new String[] { COLUNA_ID_MONITOR,
                COLUNA_CARGA_BATERIA }, null, null, null, null, null);

       return  myDB.rawQuery("SELECT  "+ COLUNA_CARGA_BATERIA+ " FROM "+TABELA_MONITOR, null);
    }*/

    public void consultarTodosDados() {

        Cursor c = myDB.rawQuery("SELECT  "+ COLUNA_ID_MONITOR+","+COLUNA_CARGA_BATERIA+","+COLUNA_TIME+ " FROM "+TABELA_MONITOR, null);
        c.moveToFirst();

        while (!c.isAfterLast()) {
            Log.i("script","índice:" + c.getString(0)+" - "+c.getString(1)+"%");
            c.moveToNext();
        }
    }

    public Cursor retornarTodosDados() {

        Cursor c = myDB.rawQuery("SELECT  "+ COLUNA_ID_MONITOR+","+COLUNA_CARGA_BATERIA+","+COLUNA_TIME+ " FROM "+TABELA_MONITOR, null);
        return c;
    }
    //
//Quando for necessário retornar somente uma categoria
    public Cursor consultarCategoria(long idCategoria) throws SQLException {

        Cursor mCursor =

                myDB.query(true, TABELA_MONITOR, new String[] { COLUNA_ID_MONITOR,
                                COLUNA_CARGA_BATERIA }, COLUNA_ID_MONITOR + "=?",
                        new String[] { String.valueOf(idCategoria) }, null, null, null,
                        null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;


    }
    //Para atualizar o conteúdo de uma linha na tabela TABELA_MONITOR utilizamos um ContentValues
    public boolean atualizarCategoria(long idCategoria, String descricao) {
        ContentValues values = new ContentValues();
        values.put(COLUNA_CARGA_BATERIA, descricao);

        return myDB.update(TABELA_MONITOR, values, COLUNA_ID_MONITOR + "=?",
                new String[] { String.valueOf(idCategoria) }) > 0;
    }

}
