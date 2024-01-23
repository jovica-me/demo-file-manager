package me.jovica.notesapp.domain

import jakarta.persistence.*
import me.jovica.notesapp.security.user.UserAccountEntity
import java.util.*


@Entity
@Table(name = "folder_entity")
open class FolderEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    open var id: UUID? = null

    @Column(name = "name")
    open var name: String? = null

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_account_entity_id", nullable = false)
    open var userAccountEntity: UserAccountEntity? = null

    @Column(name = "parent_id", insertable = false, updatable = false)
    private var parentId: UUID? = null

    @OneToMany(fetch = FetchType.EAGER, cascade = [CascadeType.ALL])
    @JoinColumn(name = "parent_id")
    private val childrenFolders: Set<FolderEntity>? = null


    @OneToMany(mappedBy = "folderEntity", orphanRemoval = true)
    open var fileEntities: MutableSet<FileEntity> = mutableSetOf()


}