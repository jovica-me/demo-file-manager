package me.jovica.notesapp.files.domain

import jakarta.persistence.*
import me.jovica.notesapp.domain.UserEntity
import me.jovica.notesapp.domain.files.FolderEntity
import org.hibernate.type.NumericBooleanConverter
import java.util.*

@Entity
@Table(name = "folder_permissions_entity")
open class FolderPermissionsEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    open var id: UUID? = null


    @Column(name = "read")
    @Convert(converter = NumericBooleanConverter::class)
    open var read: Boolean? = null

    @Column(name = "write")
    @Convert(converter = NumericBooleanConverter::class)
    open var write: Boolean? = null


    @ManyToOne(optional = false)
    open var userEntity: UserEntity? = null

    @ManyToOne(cascade = [CascadeType.ALL], optional = false)
    @JoinColumn(name = "folder_entity_id", nullable = false)
    open var folderEntity: FolderEntity? = null
}



