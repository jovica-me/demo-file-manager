package me.jovica.notesapp.domain.files

import jakarta.persistence.*
import me.jovica.notesapp.domain.UserEntity
import me.jovica.notesapp.files.domain.FolderPermissionsEntity
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

    @ManyToOne(cascade = [CascadeType.ALL], optional = false)
    @JoinColumn(name = "owner_user_id", nullable = false)
    open var owner: UserEntity? = null

    @ManyToOne
    @JoinColumn(name = "parent_folder_id")
    open var parentFolder: FolderEntity? = null

    @OneToMany(mappedBy = "parentFolder")
    open var childrenFolders: MutableList<FolderEntity> = mutableListOf()


    @OneToMany(mappedBy = "folderEntity", orphanRemoval = true)
    open var fileEntities: MutableSet<FileEntity> = mutableSetOf()

    @OneToOne(cascade = [CascadeType.ALL], orphanRemoval = true)
    @JoinColumn(name = "top_folder_of_user_id", unique = true)
    open var topFolderOfUser: UserEntity? = null

    @OneToMany(mappedBy = "folderEntity", cascade = [CascadeType.ALL], orphanRemoval = true)
    open var folderPermissionsEntities: MutableSet<FolderPermissionsEntity> = mutableSetOf()


}