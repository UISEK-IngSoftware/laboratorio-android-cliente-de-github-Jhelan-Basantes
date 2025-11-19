package ec.edu.uisek.githubclient

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ec.edu.uisek.githubclient.databinding.FragmentRepoItemBinding
import ec.edu.uisek.githubclient.models.Repo

class ReposViewHolder(private val binding: FragmentRepoItemBinding) :
    RecyclerView.ViewHolder(binding.root) {
    /**
     * Vincula los datos de un repositorio a las vistas del item, incluyendo nombre, descripción,
     * lenguaje e imagen del propietario, y configura los listeners de los botones.
     */
    fun bind(repo: Repo, onEditClick: (Repo) -> Unit, onDeleteClick: (Repo) -> Unit) {
        binding.repoName.text = repo.name
        binding.repoDescription.text = repo.description
        binding.repoLang.text = repo.language
        Glide.with(binding.root.context)
            .load(repo.owner.avatar_url)
            .placeholder(R.mipmap.ic_launcher)
            .error(R.mipmap.ic_launcher)
            .circleCrop()
            .into (binding.repoOwnerImage)

        binding.editButton.setOnClickListener { onEditClick(repo) }
        binding.deleteButton.setOnClickListener { onDeleteClick(repo) }
    }
}

class ReposAdapter(
    private val onEditClick: (Repo) -> Unit,    
    private val onDeleteClick: (Repo) -> Unit
): RecyclerView.Adapter<ReposViewHolder>() {
    private var repositories: List<Repo> = emptyList()
    
    /**
     * Devuelve la cantidad total de elementos en la lista de repositorios.
     */
    override fun getItemCount(): Int = repositories.size

    /**
     * Crea y devuelve un nuevo ViewHolder inflando el diseño del item del repositorio.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReposViewHolder {
        val binding = FragmentRepoItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ReposViewHolder(binding)
    }

    /**
     * Vincula los datos del repositorio en la posición dada con el ViewHolder.
     */
    override fun onBindViewHolder(holder: ReposViewHolder, position: Int) {
        holder.bind(repositories[position], onEditClick, onDeleteClick)
    }

    /**
     * Actualiza la lista de repositorios y notifica al adaptador para refrescar la vista.
     */
    fun updateRepositories(newRepositories: List<Repo>) {
        repositories = newRepositories
        notifyDataSetChanged()
    }
}
