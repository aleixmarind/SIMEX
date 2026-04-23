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
import android.util.Base64
import android.util.Log
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
import java.io.ByteArrayOutputStream
import java.net.Socket
import java.security.KeyStore
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec

class PerfilActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPerfilBinding
    private var clienteId: Int = -1
    private var nombreUsuario: String = "Usuario"
    private var imageTarget: String = ""

    private val KEY_ALIAS = "SimexDniKey"

    private val selectImageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            data?.data?.let { uri ->
                procesarYSubirImagen(uri)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPerfilBinding.inflate(layoutInflater)
        setContentView(binding.root)

        clienteId = intent.getIntExtra("CLIENTE_ID", -1)
        nombreUsuario = intent.getStringExtra("USER_NAME") ?: "Usuario"

        if (clienteId == -1) {
            Toast.makeText(this, "Error: Sesión no válida", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

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

    private fun procesarYSubirImagen(uri: Uri) {
        lifecycleScope.launch {
            try {
                val bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    ImageDecoder.decodeBitmap(ImageDecoder.createSource(contentResolver, uri))
                } else {
                    @Suppress("DEPRECATION")
                    MediaStore.Images.Media.getBitmap(contentResolver, uri)
                }

                val base64Image = withContext(Dispatchers.Default) {
                    encodeImageToBase64(bitmap)
                }

                val status = withContext(Dispatchers.IO) {
                    enviarImagenEncriptadaSocket(base64Image)
                }

                if (status) {
                    val response = withContext(Dispatchers.IO) {
                        if (imageTarget == "frontal") {
                            RetrofitClient.instance.subirDniFrontal(clienteId, base64Image)
                        } else {
                            RetrofitClient.instance.subirDniTrasero(clienteId, base64Image)
                        }
                    }

                    if (response.isSuccessful) {
                        Toast.makeText(this@PerfilActivity, "DNI subido con éxito", Toast.LENGTH_SHORT).show()
                        obtenerDatosPerfil(clienteId)
                    } else {
                        Toast.makeText(this@PerfilActivity, "Error al guardar en base de datos", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@PerfilActivity, "Error en el canal de seguridad", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e("UPLOAD_ERROR", "Error: ${e.message}")
                Toast.makeText(this@PerfilActivity, "Error al procesar la imagen", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun encodeImageToBase64(bitmap: Bitmap): String {
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, outputStream)
        val byteArray = outputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.NO_WRAP)
    }

    private fun getOrCreateSecretKey(): SecretKey {
        val keyStore = KeyStore.getInstance("AndroidKeyStore")
        keyStore.load(null)
        
        if (!keyStore.containsAlias(KEY_ALIAS)) {
            val keyGenerator = KeyGenerator.getInstance("AES", "AndroidKeyStore")
            val keyGenParameterSpec = android.security.keystore.KeyGenParameterSpec.Builder(
                KEY_ALIAS,
                android.security.keystore.KeyProperties.PURPOSE_ENCRYPT or android.security.keystore.KeyProperties.PURPOSE_DECRYPT
            )
                .setBlockModes(android.security.keystore.KeyProperties.BLOCK_MODE_CBC)
                .setEncryptionPaddings(android.security.keystore.KeyProperties.ENCRYPTION_PADDING_PKCS7)
                .setRandomizedEncryptionRequired(false) 
                .build()
            
            keyGenerator.init(keyGenParameterSpec)
            return keyGenerator.generateKey()
        }

        val entry = keyStore.getEntry(KEY_ALIAS, null) as KeyStore.SecretKeyEntry
        return entry.secretKey
    }

    private suspend fun enviarImagenEncriptadaSocket(base64: String): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val secretKey = getOrCreateSecretKey()

                val iv = ByteArray(16)
                SecureRandom().nextBytes(iv)
                val ivSpec = IvParameterSpec(iv)

                val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
                cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivSpec)
                
                val encryptedData = cipher.doFinal(base64.toByteArray())

                // Conexión real por socket al servidor de seguridad
                val serverIp = "10.0.2.2" 
                val serverPort = 8888
                
                Socket(serverIp, serverPort).use { socket ->
                    val outputStream = socket.getOutputStream()
                    outputStream.write(iv) 
                    outputStream.write(encryptedData)
                    outputStream.flush()
                }
                
                Log.d("SECURITY_SOC", "Imagen encriptada y IV enviados por socket real")
                true
            } catch (e: Exception) {
                Log.e("SECURITY_ERROR", "Fallo en canal seguro: ${e.message}", e)
                false
            }
        }
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

                usuario.dniFotoFrontal?.let {
                    if (it.length > 10) binding.ivDniFrontal.setImageBitmap(decodeBase64ToBitmap(it))
                }
                usuario.dniFotoTrasera?.let {
                    if (it.length > 10) binding.ivDniTrasera.setImageBitmap(decodeBase64ToBitmap(it))
                }

            } catch (e: Exception) {
                Log.e("PERFIL_ERROR", "Error: ${e.message}")
            }
        }
    }

    private fun decodeBase64ToBitmap(base64Str: String): Bitmap? {
        return try {
            val decodedBytes = Base64.decode(base64Str, Base64.DEFAULT)
            BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
        } catch (e: Exception) { null }
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
                else -> false
            }
        }
    }
}
