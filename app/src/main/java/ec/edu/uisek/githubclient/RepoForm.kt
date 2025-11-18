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
        RetrofitClient.gitHubApiService
    }

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

    private fun showMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}