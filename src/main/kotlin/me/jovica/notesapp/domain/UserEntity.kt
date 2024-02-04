package me.jovica.notesapp.domain

import jakarta.persistence.*
import me.jovica.notesapp.domain.files.FileEntity
import me.jovica.notesapp.domain.files.FolderEntity
import me.jovica.notesapp.files.domain.FolderPermissionsEntity
import me.jovica.notesapp.security.user.UserAccountEntity
import java.util.*


@Entity
@Table(name = "user_entity")
open class UserEntity {
    @Id
    @Column(name = "id", nullable = false)
    open var id: UUID? = null


    @OneToOne
    @MapsId
    @JoinColumn(name = "id")
    open var userAccount: UserAccountEntity? = null;

    @OneToOne(mappedBy = "topFolderOfUser", cascade = [CascadeType.ALL], optional = false, orphanRemoval = true)
    open var topFolder: FolderEntity? = null

    @OneToMany(mappedBy = "owner", orphanRemoval = true)
    open var folderEntities: MutableSet<FolderEntity> = mutableSetOf()


    @OneToMany(fetch = FetchType.EAGER, mappedBy = "owner", orphanRemoval = true)
    open var files: MutableSet<FileEntity> = mutableSetOf()


    @OneToMany(mappedBy = "owner", orphanRemoval = true)
    open var publishedPageEntities: MutableSet<PublishedPageEntity> = mutableSetOf()

    @OneToMany(mappedBy = "userEntity", cascade = [CascadeType.ALL], orphanRemoval = true)
    open var folderPermissionsEntities: MutableSet<FolderPermissionsEntity> = mutableSetOf()

    @ManyToMany(fetch = FetchType.EAGER, cascade = [CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH])
    @JoinTable(
        name = "user_entity_file_entities",
        joinColumns = [JoinColumn(name = "user_entity_id")],
        inverseJoinColumns = [JoinColumn(name = "file_entities_id")]
    )
    open var accessFiles: MutableSet<FileEntity> = mutableSetOf()
}