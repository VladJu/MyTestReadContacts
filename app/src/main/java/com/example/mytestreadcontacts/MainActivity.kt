package com.example.mytestreadcontacts

import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //1)
        //Getting permission from user
        val permissionGranted = ActivityCompat.checkSelfPermission(
            this,
            //название разрешения которое хотим проверить (return Granted or Denied)
            android.Manifest.permission.READ_CONTACTS
        ) == PackageManager.PERMISSION_GRANTED
        if (permissionGranted) {
            requestContacts()
        } else {
            requestPermission()
        }
    }

    //2)запросим разрешение у user
    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(android.Manifest.permission.READ_CONTACTS),
            //нужен чтобы понять какое именно разрешение дал пользователь
            READ_CONTACTS_RC
        )
    }

    //3)Чтобы узнать дал ли user разрешение или нет вызовется после выбора
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        //3.1)Если был вызван после запроса на разрешение чтения контактов
        if (requestCode == READ_CONTACTS_RC && grantResults.isNotEmpty()){
            //3.2)узнаем что выбрал пользователь
            val permissionGranted =grantResults[0] == PackageManager.PERMISSION_GRANTED
            if (permissionGranted) {
                requestContacts()
            } else {
                Log.d("MainActivity", "Permission Denied")
            }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun requestContacts() {
        thread {
            //4)
            //получаем данные о контактах
            val cursor = contentResolver.query(
                //4.1
                //Передаем Uri на получение списков контактов
                ContactsContract.Contacts.CONTENT_URI,
                null,
                null,
                null,
                null
            )
            //4.2
            //Получаем данные из Cursor
            while (cursor?.moveToNext() == true) {
                val id = cursor.getInt(
                    cursor.getColumnIndexOrThrow(ContactsContract.Contacts._ID)
                )
                val name = cursor.getString(
                    cursor.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME)
                )
                val contact = Contact(id, name)
                Log.d("MainActivity", "Contact: $contact")
            }
            cursor?.close()
        }
    }

    companion object {
        private const val READ_CONTACTS_RC = 100
    }
}