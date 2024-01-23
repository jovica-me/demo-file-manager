package me.jovica.notesapp.domain

import jakarta.persistence.*
import me.jovica.notesapp.security.user.UserAccountEntity
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

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_account_entity_id", nullable = false)
    open var userAccountEntity: UserAccountEntity? = null

    @ManyToOne(optional = false)
    @JoinColumn(name = "folder_entity_id", nullable = false)
    open var folderEntity: FolderEntity? = null


}