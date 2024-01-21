package me.jovica.notesapp.domain

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.util.*

@Entity
@Table(name = "published_page_entity")
open class PublishedPageEntity {
    @Id
    @Column(name = "id", nullable = false)
    open var id: UUID? = null
}