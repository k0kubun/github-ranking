package com.github.k0kubun.gitstar_ranking

import java.util.concurrent.BlockingQueue
import java.util.concurrent.LinkedBlockingQueue
import org.jooq.SQLDialect
import org.jooq.impl.DSL
import org.jooq.impl.DefaultConfiguration
import org.postgresql.ds.PGSimpleDataSource

data class DatabaseConfiguration(
    val host: String,
    val port: Int,
    val user: String,
    val password: String,
    val database: String,
) {
    val dslContext = DSL.using(
        DefaultConfiguration().set(SQLDialect.POSTGRES).set(
            PGSimpleDataSource().also {
                // stringtype for citext: https://github.com/jOOQ/jOOQ/issues/7093
                it.setUrl("jdbc:postgresql://$host:$port/$database?stringtype=unspecified")
                it.user = user
                it.password = password
            }
        )
    )

    constructor(env: Map<String, String> = System.getenv()) : this(
        host = env.getOrDefault("DATABASE_HOST", "127.0.0.1"),
        port = env.getOrDefault("DATABASE_PORT", "5432").toInt(),
        user = env.getOrDefault("DATABASE_USER", "postgres"),
        password = env.getOrDefault("DATABASE_PASSWORD", ""),
        database = env.getOrDefault("DATABASE_NAME", "gitstar_ranking"),
    )
}

data class QueueConfiguration(
    val rankingQueue: BlockingQueue<Boolean> = LinkedBlockingQueue(),
    val userStarScanQueue: BlockingQueue<Boolean> = LinkedBlockingQueue(),
    val userFullScanQueue: BlockingQueue<Boolean> = LinkedBlockingQueue(),
)

data class GitstarRankingConfiguration(
    val database: DatabaseConfiguration = DatabaseConfiguration(),
    val queue: QueueConfiguration = QueueConfiguration(),
)
