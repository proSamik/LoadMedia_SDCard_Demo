package com.example.loadmedia_sdcard_demo

import android.content.ContentValues.TAG
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {

    var listSongs = ArrayList<SongInfo>()
    var mp: MediaPlayer? = null
    var adapter : MySongAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if(ActivityCompat.checkSelfPermission(
            this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
        {
            //if permission is not given, then requesting permission for write and record audio access
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), 111 )
        }
        else
            loadSong()

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == 111 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            loadSong()
    }

    inner class MySongAdapter : BaseAdapter{

        var myListSong = ArrayList<SongInfo>()

        constructor(myListSong: ArrayList<SongInfo>) : super(){
            this.myListSong = myListSong
        }
        //How many times to operate
        override fun getCount(): Int {
            return myListSong.size
        }

        override fun getItem(position: Int): Any {
            return myListSong[position]
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val myview = layoutInflater.inflate(R.layout.song_list_layout, null)

            val song = myListSong[position]
            val textTitle = myview.findViewById<TextView>(R.id.textView1)
            textTitle.text = song.Title

            val textAuthor = myview.findViewById<TextView>(R.id.textView2)
            textAuthor.text = song.Author

            val btnURL = myview.findViewById<Button>(R.id.button1)
            btnURL.setOnClickListener {
                if(btnURL.text == "STOP"){
                    mp!!.stop()
                    btnURL.text = "PLAY"
                }
                else{
                    mp = MediaPlayer()
                    try{
                        mp!!.setDataSource(song.SongURl)
                        mp!!.prepare()
                        mp!!.start()
                        btnURL.text = "STOP"
                    } catch (e: Exception) {}
                }
            }

            return myview
        }

    }

    private fun loadSong() {
//        var uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
//        var selection = MediaStore.Audio.Media.IS_MUSIC + "!=0"
//
//
//        var rs = contentResolver.query(uri, null, selection,
//            null, null)
//
//        if(rs!=null){
//            while(rs!!.moveToNext()){
//                var url = rs!!.getString(rs!!.getColumnIndex((MediaStore.Audio.Media.DATA)))
//                var author = rs!!.getString((rs!!.getColumnIndex(MediaStore.Audio.Media.ARTIST)))
//                var title = rs!!.getString((rs!!.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME)))
//
//                listSongs.add(SongInfo(title, author, url))
//            }
//        }
        val filepath = Environment.getExternalStorageDirectory().toString()
        val file = File("${filepath}/Music")

        var songList = ArrayList<SongInfo>()
        val songFile = file.listFiles()
        var i = 0
        while(i < file.listFiles().size){
            songList.add(SongInfo(songFile[i].nameWithoutExtension,
                                    convertLongToTime(songFile[i].lastModified()), songFile[i].path))

            songFile[i].delete()
            i +=1
        }

        adapter = MySongAdapter(songList)
        val listem: ListView = findViewById<ListView>(R.id.songView)
        listem.adapter = adapter
    }

    fun convertLongToTime(time: Long): String {
        val date = Date(time)
        val format = SimpleDateFormat("yyyy.MM.dd HH:mm")
        return format.format(date)
    }
}

