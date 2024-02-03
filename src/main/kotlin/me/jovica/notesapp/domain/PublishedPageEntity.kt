package me.jovica.notesapp.domain

import jakarta.persistence.*
import java.util.*

@Entity
@Table(name = "published_page_entity")
open class PublishedPageEntity {
    @Id
    @Column(name = "id", nullable = false)
    open var id: UUID? = null

    @Column(name = "description")
    open var description: String? = null

    @Column(name = "title")
    open var title: String? = null

    @Column(name = "content")
    open var content: String? = null

    @ManyToOne(optional = false)
    @JoinColumn(name = "owner_id", nullable = false)
    open var owner: UserEntity? = null
}