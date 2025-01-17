package me.jovica.notesapp.domain.files

import jakarta.persistence.*
import me.jovica.notesapp.domain.UserEntity
import me.jovica.notesapp.files.domain.FileMessageEntity
import org.hibernate.proxy.HibernateProxy
import java.util.*

@Entity
@Table(name = "file_entity")
open class FileEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    open var id: UUID? = null

    @Column(name = "name")
    open var name: String? = null

    @Column(name = "text")
    open var text: String? = null

    @ManyToOne(cascade = [CascadeType.ALL], optional = false)
    open var folderEntity: FolderEntity? = null


    @ManyToOne(optional = false)
    @JoinColumn(name = "owner_user_id", nullable = false)
    open var owner: UserEntity? = null


    @ManyToMany(fetch = FetchType.EAGER, mappedBy = "accessFiles", cascade = [CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH])
    open var hasAccess: MutableSet<UserEntity> = mutableSetOf()

    @OneToMany(mappedBy = "fileEntity", cascade = [CascadeType.ALL], orphanRemoval = true)
    open var messages: MutableList<FileMessageEntity> = mutableListOf()
    final override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null) return false
        val oEffectiveClass =
            if (other is HibernateProxy) other.hibernateLazyInitializer.persistentClass else other.javaClass
        val thisEffectiveClass =
            if (this is HibernateProxy) this.hibernateLazyInitializer.persistentClass else this.javaClass
        if (thisEffectiveClass != oEffectiveClass) return false
        other as FileEntity

        return id != null && id == other.id
    }

    final override fun hashCode(): Int =
        if (this is HibernateProxy) this.hibernateLazyInitializer.persistentClass.hashCode() else javaClass.hashCode()
}