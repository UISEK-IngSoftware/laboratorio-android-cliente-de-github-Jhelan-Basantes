package ec.edu.uisek.githubclient

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import ec.edu.uisek.githubclient.databinding.ActivityMainBinding
import ec.edu.uisek.githubclient.models.Repo
import ec.edu.uisek.githubclient.services.GithubApiService
import ec.edu.uisek.githubclient.services.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var reposAdapter: ReposAdapter
    private val apiService: GithubApiService by lazy {
        RetrofitClient.gitHubApiService
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        binding.newRepoFab.setOnClickListener {
            displayNewRepoForm()
        }
    }

    override fun onResume() {
        super.onResume()
        fetchRepositories()
    }

    private fun setupRecyclerView() {
        reposAdapter = ReposAdapter(::handleEditClick, ::handleDeleteClick)
        binding.reposRecyclerView.adapter = reposAdapter
    }

    private fun fetchRepositories() {
        apiService.getRepos().enqueue(object : Callback<List<Repo>> {
            override fun onResponse(call: Call<List<Repo>>, response: Response<List<Repo>>) {
                if (response.isSuccessful) {
                    val repos = response.body()
                    if (repos != null && repos.isNotEmpty()) {
                        reposAdapter.updateRepositories(repos)
                    } else {
                        showMessage("No se encontraron repositorios")
                    }
                } else {
                    handleApiError(response.code())
                }
            }

            override fun onFailure(call: Call<List<Repo>>, t: Throwable) {
                showMessage("No se pudieron cargar repositorios")
            }
        })
    }

    private fun handleEditClick(repo: Repo) {
        val intent = Intent(this, RepoForm::class.java).apply {
            putExtra("repo", repo)
        }
        startActivity(intent)
    }

    private fun handleDeleteClick(repo: Repo) {
        AlertDialog.Builder(this)
            .setTitle("Confirmar Eliminación")
            .setMessage("¿Estás seguro de que quieres eliminar el repositorio '${repo.name}'?")
            .setPositiveButton("Sí") { _, _ ->
                deleteRepository(repo)
            }
            .setNegativeButton("No", null)
            .show()
    }

    private fun deleteRepository(repo: Repo) {
        apiService.deleteRepo(repo.owner.login, repo.name).enqueue(object : Callback<Unit> {
            override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                if (response.isSuccessful) {
                    showMessage("Repositorio eliminado exitosamente")
                    fetchRepositories()
                } else {
                    handleApiError(response.code())
                }
            }

            override fun onFailure(call: Call<Unit>, t: Throwable) {
                showMessage("Error al eliminar el repositorio")
            }
        })
    }

    private fun displayNewRepoForm() {
        val intent = Intent(this, RepoForm::class.java)
        startActivity(intent)
    }

    private fun handleApiError(code: Int) {
        val errorMessage = when (code) {
            401 -> "No autorizado"
            403 -> "Prohibido"
            404 -> "No encontrado"
            else -> "Error: $code"
        }
        showMessage(errorMessage)
    }

    private fun showMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}