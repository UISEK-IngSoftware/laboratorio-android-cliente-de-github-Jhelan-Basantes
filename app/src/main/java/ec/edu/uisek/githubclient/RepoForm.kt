package ec.edu.uisek.githubclient

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import ec.edu.uisek.githubclient.databinding.ActivityRepoFormBinding
import ec.edu.uisek.githubclient.models.Repo
import ec.edu.uisek.githubclient.models.RepoRequest
import ec.edu.uisek.githubclient.services.GithubApiService
import ec.edu.uisek.githubclient.services.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RepoForm : AppCompatActivity() {

    private lateinit var binding: ActivityRepoFormBinding
    private var repo: Repo? = null
    private val apiService: GithubApiService by lazy {
        RetrofitClient.getApiService()
    }

    /**
     * Inicializa la actividad, configura el binding, recupera los datos del intent si existen
     * y configura los listeners para los botones de guardar y cancelar.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRepoFormBinding.inflate(layoutInflater)
        setContentView(binding.root)

        repo = intent.getParcelableExtra("repo")

        repo?.let {
            binding.repoNameInput.setText(it.name)
            binding.repoDescriptionInput.setText(it.description)
        }

        binding.saveButton.setOnClickListener {
            saveRepo()
        }

        binding.cancelButton.setOnClickListener {
            finish()
        }
    }

    /**
     * Recopila la información del formulario y realiza la petición a la API para crear
     * o actualizar un repositorio, manejando la respuesta correspondiente.
     */
    private fun saveRepo() {
        val name = binding.repoNameInput.text.toString()
        val description = binding.repoDescriptionInput.text.toString()
        val repoRequest = RepoRequest(name, description)

        val call = if (repo == null) {
            apiService.createRepo(repoRequest)
        } else {
            apiService.updateRepo(repo!!.owner.login, repo!!.name, repoRequest)
        }

        call.enqueue(object : Callback<Repo> {
            override fun onResponse(call: Call<Repo>, response: Response<Repo>) {
                if (response.isSuccessful) {
                    val action = if (repo == null) "creado" else "actualizado"
                    showMessage("Repositorio $action exitosamente")
                    finish()
                } else {
                    handleApiError(response.code())
                }
            }

            override fun onFailure(call: Call<Repo>, t: Throwable) {
                val action = if (repo == null) "crear" else "actualizar"
                showMessage("Error al $action el repositorio")
            }
        })
    }

    /**
     * Procesa el código de error HTTP devuelto por la API y muestra un mensaje amigable al usuario.
     */
    private fun handleApiError(code: Int) {
        val errorMessage = when (code) {
            401 -> "No autorizado"
            403 -> "Prohibido"
            404 -> "No encontrado"
            422 -> "Error de validación"
            else -> "Error: $code"
        }
        showMessage(errorMessage)
    }

    /**
     * Muestra un mensaje breve (Toast) en la pantalla.
     */
    private fun showMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
