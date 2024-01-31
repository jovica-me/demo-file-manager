package me.jovica.notesapp.domain

import jakarta.persistence.*
import me.jovica.notesapp.domain.files.FileEntity
import me.jovica.notesapp.domain.files.FolderEntity
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
    open var publishedPages: MutableSet<PublishedPageEntity> = mutableSetOf()


}