package ar.reloadersystem.sendmaildominio;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import javax.mail.AuthenticationFailedException;
import javax.mail.MessagingException;

public class MainActivity extends AppCompatActivity {

    private EditText user;
    private EditText pass;
    private EditText subject;
    private EditText body;
    private EditText receive;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage();
            }
        });

        user = (EditText) findViewById(R.id.username);
        pass = (EditText) findViewById(R.id.password);
        subject = (EditText) findViewById(R.id.subject);
        body = (EditText) findViewById(R.id.body);
        receive = (EditText) findViewById(R.id.toreceive);
    }

    private void sendMessage() {
        String[] receptor = {"labsistemas@gmail.com"}; // el mail que recibe  pueden tomar  tmb  los datos desde los  EditText
        SendEmailAsyncTask email = new SendEmailAsyncTask();
        email.activity = this;
        email.m = new Mail("reloader@gmail.com", "solucioninfo10");  //el mail quien envia  y su contraseña real porque sino te dara error en mi caso traigo desde un Servicio a mi BD con retrofit
        email.m.set_from("relooder@gmail.com"); //mail del que envia
        email.m.setBody(body.getText().toString());
        email.m.set_to(receptor);
        email.m.set_subject(subject.getText().toString());
        email.execute();
    }

    public void displayMessage(String message) {
        Snackbar.make(findViewById(R.id.fab), message, Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    } // si desean  pueden  usar un snack bar para mostrar   //activity.displayMessage("Email enviado"); descomenten esto en el asyntask
}

class SendEmailAsyncTask extends AsyncTask<Void, Void, Boolean> {
    Mail m;
    MainActivity activity;
    String mensaje;

    @Override
    protected Boolean doInBackground(Void... params) {

        try {
            if (m.send()) {
                //activity.displayMessage("Email enviado");
                mensaje = "Email enviado";
                handlerMessage(activity, mensaje); //  handler  es un controlador porque estamos en un hilo, y va al hilo principal porque el toast esta en el UI

            } else {
                mensaje = "Error al enviar mail";
                handlerMessage(activity, mensaje);
            }
            return true;
        } catch (AuthenticationFailedException e) {
            Log.e(SendEmailAsyncTask.class.getName(), "Bad account details");
            e.printStackTrace();

            mensaje = "Error de usuario";
            handlerMessage(activity, mensaje);
            return false;

        } catch (MessagingException e) {
            Log.e(SendEmailAsyncTask.class.getName(), "Email failed");
            e.printStackTrace();
            mensaje = "Error al enviar mail";
            handlerMessage(activity, mensaje);

            return false;
        } catch (Exception e) {
            e.printStackTrace();

            mensaje = "Error inesperado";
            handlerMessage(activity, mensaje);
            return false;
        }

    }

    private void handlerMessage(final Activity activity, final String mensaje) {

        Handler handler = new Handler(activity.getMainLooper());

        handler.post(new Runnable() {
            public void run() {
                Toast.makeText(activity, mensaje, Toast.LENGTH_LONG).show();
            }
        });
    }
}
