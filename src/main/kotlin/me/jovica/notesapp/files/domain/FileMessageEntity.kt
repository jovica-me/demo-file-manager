package me.jovica.notesapp.files.domain

import jakarta.persistence.*
import me.jovica.notesapp.domain.UserEntity
import me.jovica.notesapp.domain.files.FileEntity
import java.time.LocalDateTime
import java.util.*

@Entity
@Table(name = "file_message_entity")
open class FileMessageEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    open var id: UUID? = null


    @Column(name = "text", nullable = false)
    open var text: String? = null

    @ManyToOne(cascade = [CascadeType.ALL], optional = false)
    @JoinColumn(name = "file_entity_id", nullable = false)
    open var fileEntity: FileEntity? = null

    @Column(name = "timestamp", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    open var timestamp: LocalDateTime? = null


    @ManyToOne(cascade = [CascadeType.ALL], optional = false)
    @JoinColumn(name = "user_entity_id", nullable = false)
    open var userEntity: UserEntity? = null
}