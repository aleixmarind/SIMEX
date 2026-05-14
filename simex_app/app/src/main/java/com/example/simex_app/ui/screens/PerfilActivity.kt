package com.example.simex_app.ui.screens

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.simex_app.R
import com.example.simex_app.data.network.RetrofitClient
import com.example.simex_app.databinding.ActivityPerfilBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.ByteArrayOutputStream
import java.io.File
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

class PerfilActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPerfilBinding
    private var clienteId: Int = -1
    private var nombreUsuario: String = "Usuario"
    private var imageTarget: String = ""

    private val AES_KEY = "SimexSecureKey12"
    private val IV = "SimexIVVector123"

    private val selectImageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            data?.data?.let { uri ->
                procesarYGuardarLocal(uri)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPerfilBinding.inflate(layoutInflater)
        setContentView(binding.root)

        clienteId = intent.getIntExtra("CLIENTE_ID", -1)
        nombreUsuario = intent.getStringExtra("USER_NAME") ?: "Usuario"

        setupBottomNavigation(clienteId, nombreUsuario)
        obtenerDatosPerfil(clienteId)

        binding.btnLogout.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }

        binding.btnSubirFrontal.setOnClickListener {
            imageTarget = "frontal"
            abrirGaleria()
        }

        binding.btnSubirTrasera.setOnClickListener {
            imageTarget = "trasera"
            abrirGaleria()
        }
    }

    private fun abrirGaleria() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        selectImageLauncher.launch(intent)
    }

    private fun procesarYGuardarLocal(uri: Uri) {
        lifecycleScope.launch {
            try {
                val bitmap = withContext(Dispatchers.IO) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                        ImageDecoder.decodeBitmap(ImageDecoder.createSource(contentResolver, uri))
                    } else {
                        @Suppress("DEPRECATION")
                        MediaStore.Images.Media.getBitmap(contentResolver, uri)
                    }
                }

                val outputStream = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 70, outputStream)
                val imageBytes = outputStream.toByteArray()

                val encryptedData = withContext(Dispatchers.Default) {
                    encriptarAES(imageBytes)
                }

                val fileName = "dni_${imageTarget}_$clienteId.enc"
                val file = File(filesDir, fileName)
                withContext(Dispatchers.IO) {
                    file.writeBytes(encryptedData)
                }

                Toast.makeText(this@PerfilActivity, "DNI $imageTarget guardado localmente", Toast.LENGTH_SHORT).show()
                
                subirAlServidor(file)

                if (imageTarget == "frontal") {
                    binding.ivDniFrontal.setImageBitmap(bitmap)
                    binding.ivDniFrontal.visibility = View.VISIBLE
                } else {
                    binding.ivDniTrasera.setImageBitmap(bitmap)
                    binding.ivDniTrasera.visibility = View.VISIBLE
                }

            } catch (e: Exception) {
                Log.e("LOCAL_STORAGE", "Error al procesar DNI: ${e.message}")
                Toast.makeText(this@PerfilActivity, "Error al procesar la imagen", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun subirAlServidor(file: File) {
        lifecycleScope.launch {
            try {
                val mediaType = MediaType.parse("application/octet-stream")
                val requestFile = RequestBody.create(mediaType, file)
                val body = MultipartBody.Part.createFormData("file", file.name, requestFile)

                val response = withContext(Dispatchers.IO) {
                    RetrofitClient.dniInstance.uploadDniEncrypted(clienteId, body)
                }

                if (response.isSuccessful) {
                    Log.d("SERVER_UPLOAD", "Éxito: ${response.body()}")
                    Toast.makeText(this@PerfilActivity, "DNI enviado al servidor", Toast.LENGTH_SHORT).show()
                } else {
                    Log.e("SERVER_UPLOAD", "Error: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("SERVER_UPLOAD", "Fallo: ${e.message}")
            }
        }
    }

    private fun encriptarAES(data: ByteArray): ByteArray {
        val secretKey = SecretKeySpec(AES_KEY.toByteArray(), "AES")
        val ivSpec = IvParameterSpec(IV.toByteArray())
        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivSpec)
        return cipher.doFinal(data)
    }

    private fun desencriptarAES(data: ByteArray): ByteArray {
        val secretKey = SecretKeySpec(AES_KEY.toByteArray(), "AES")
        val ivSpec = IvParameterSpec(IV.toByteArray())
        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
        cipher.init(Cipher.DECRYPT_MODE, secretKey, ivSpec)
        return cipher.doFinal(data)
    }

    private fun obtenerDatosPerfil(id: Int) {
        lifecycleScope.launch {
            try {
                val usuario = withContext(Dispatchers.IO) {
                    RetrofitClient.instance.getUsuario(id)
                }

                binding.tvFullName.text = "${usuario.nom} ${usuario.cognoms}"
                binding.tvEmail.text = usuario.correu
                binding.tvUserId.text = "#${usuario.id}"
                binding.tvUserRole.text = if (usuario.rolId == 1004) "CLIENTE" else "AGENTE"

                cargarImagenLocal("frontal")
                cargarImagenLocal("trasera")

            } catch (e: Exception) {
                Log.e("PERFIL_ERROR", "Error al obtener perfil: ${e.message}")
            }
        }
    }

    private fun cargarImagenLocal(type: String) {
        lifecycleScope.launch(Dispatchers.IO) {
            val fileName = "dni_${type}_$clienteId.enc"
            val file = File(filesDir, fileName)
            
            if (file.exists()) {
                try {
                    val encryptedData = file.readBytes()
                    val decryptedData = desencriptarAES(encryptedData)
                    val bitmap = BitmapFactory.decodeByteArray(decryptedData, 0, decryptedData.size)
                    
                    withContext(Dispatchers.Main) {
                        if (type == "frontal") {
                            binding.ivDniFrontal.setImageBitmap(bitmap)
                            binding.ivDniFrontal.visibility = View.VISIBLE
                        } else {
                            binding.ivDniTrasera.setImageBitmap(bitmap)
                            binding.ivDniTrasera.visibility = View.VISIBLE
                        }
                    }
                } catch (e: Exception) {
                    Log.e("DECRYPT_ERROR", "Error al cargar DNI $type: ${e.message}")
                }
            }
        }
    }

    private fun setupBottomNavigation(id: Int, nombre: String) {
        binding.bottomNavigation.selectedItemId = R.id.nav_perfil
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    val esAgente = binding.tvUserRole.text == "AGENTE"
                    val destination = if (esAgente) HomePageAgenteActivity::class.java else HomePageClienteActivity::class.java
                    val intent = Intent(this, destination)
                    intent.putExtra("CLIENTE_ID", id)
                    intent.putExtra("USER_NAME", nombre)
                    startActivity(intent)
                    finish()
                    true
                }
                R.id.nav_juego -> {
                    val intent = Intent(this, SnakeGameActivity::class.java)
                    intent.putExtra("CLIENTE_ID", id)
                    intent.putExtra("USER_NAME", nombre)
                    intent.putExtra("ES_AGENTE", binding.tvUserRole.text == "AGENTE")
                    startActivity(intent)
                    finish()
                    true
                }
                else -> false
            }
        }
    }
}
