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
        // разрешение было дано если он вернул
        ) == PackageManager.PERMISSION_GRANTED
        if (permissionGranted){
            requestContacts()
        } else {
            Log.d("MainActivity", "Permission denied")
        }
    }

    private fun requestContacts() {
        thread {
            //2)
            //получаем данные о контактах
            val cursor = contentResolver.query(
                //2.1
                //Передаем Uri на получение списков контактов
                ContactsContract.Contacts.CONTENT_URI,
                null,
                null,
                null,
                null
            )
            //2.2
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
}