package br.com.adriano.projetostatusbateria;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.util.Log;
import android.widget.TextView;
import java.util.Calendar;


public class MainActivity extends ActionBarActivity {
    //Variáves para o TextView
    private TextView contentTVBD;
    private TextView contentTVBateria;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //Enviando as informações para o TextView 1
        contentTVBateria = (TextView) this.findViewById(R.id.textView1);
        this.registerReceiver(this.monitorBateria, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));

        /*Pode ser usado para uma consulta específica
        //Addicioneir 01 -  exemplo simples de acesso a uma categoria através de um Cursor
        //Criação do Objeto
        DBAdapter db = new DBAdapter(getApplicationContext());
        db.open(); //Abrindo o Banco
        //db.InserirDados("teste 27"); //teste para adicionar conteúdo

        Long idCategoria = 11L;
        String descricao = "";
        Cursor c2 = db.consultarCategoria(idCategoria);
        //Cursor c2 = db.consultarTodasCategorias();
        if(c2 != null){
                descricao = c2.getString(c2.getColumnIndex(DBAdapter.COLUNA_CARGA_BATERIA));
        }

//Fechando o Banco e o Cursor
        db.close();
        c2.close();
//Resultado da consulto via Logs
        //Log.w("Script ", "Categoria: " + descricao);
        //Add 01 Até aqui

        //Mostrando na tela o resultado da consulta em banco

       contentTVBD = (TextView) this.findViewById(R.id.textView2);
       contentTVBD.setText("Descrição da coluna: " + String.valueOf(descricao));*/
        /* Para visualizar todos os registros
        DBAdapter db = new DBAdapter(getApplicationContext());

        db.open();
        Cursor c = db.retornarTodosDados();
       // db.adicionarColuna();
        c.moveToFirst();
        db.close();
        StringBuilder sb = new StringBuilder();
       // while (!c.isAfterLast()) {
         //   Log.i("script","índice:" + c.getColumnName(2)+" - "+c.getString(1)+"%--"+c.getString(2));
           // sb.append(String.valueOf(c.getString(1))+"-");
          //  c.moveToNext();
      //  }*/
        contentTVBD = (TextView) this.findViewById(R.id.textView3);
        //contentTVBD.setText("Descrição da coluna: " + sb.toString());
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private BroadcastReceiver monitorBateria = new BroadcastReceiver(){
        @Override
        public void onReceive(Context arg0, Intent intent) {

            //Variáves
            Calendar c = Calendar.getInstance();
            int hora = c.get(Calendar.HOUR_OF_DAY);
            int minuto = c.get(Calendar.MINUTE);
            int segundo = c.get(Calendar.SECOND);
            int milisegundo = c.get(Calendar.MILLISECOND);
            String time_consulta= new String (hora+":"+minuto+":"+segundo+":"+milisegundo);

            String  technology= intent.getExtras().getString(BatteryManager.EXTRA_TECHNOLOGY);
            int  temperature= intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE,0);
            int  voltage= intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE,0);
            int  levelInfo= intent.getIntExtra(BatteryManager.EXTRA_LEVEL,0);
            int  plugged= intent.getIntExtra(BatteryManager.EXTRA_PLUGGED,0);

            int level = intent.getIntExtra("level", 0);//variável que vai receber o valor em porcentagem

            Log.w("Script", "Bateria:" + level + "%");//Parte do código que informa o valor da porcentagem via log
            Log.w("Script/Time:", time_consulta );

            DBAdapter db = new DBAdapter(getApplicationContext());

            db.open(); //Abrindo o Banco
            StringBuilder sb = new StringBuilder();



            db.InserirDados(String.valueOf(level)); //Inserindo os dados da bateria no banco
            db.consultarTodosDados();//consultando os dados inseridos

            db.close();

            contentTVBateria.setText(
                    "A Bateria está com: " + String.valueOf(level) + "% de carga\n"+
                            "Technology: "+technology+"\n"+
                            "Temperature: "+temperature+"\n"+
                            "Nível: "+levelInfo+"%\n"+
                            "Plugged: "+plugged+"\n"+
                            "Voltage: "+voltage+"\n"); // Construção dos dados que será apresentado na tela


        }


    };

}