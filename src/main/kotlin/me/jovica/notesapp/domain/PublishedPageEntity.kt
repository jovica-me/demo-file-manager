package me.jovica.notesapp.domain

import jakarta.persistence.*
import java.util.*

@Entity
@Table(name = "published_page_entity")
open class PublishedPageEntity {
    @Id
    @Column(name = "id", nullable = false)
    open var id: UUID? = null

    @ManyToOne(cascade = [CascadeType.ALL], optional = false)
    @JoinColumn(name = "owner_user_id", nullable = false)
    open var owner: UserEntity? = null

    @Column(name = "description", nullable = false, columnDefinition = "LONGTEXT")
    open var description: String? = null

    @Column(name = "title", nullable = false)
    open var title: String? = null

    @Column(name = "content", nullable = false, columnDefinition = "LONGTEXT")
    open var content: String? = null
}